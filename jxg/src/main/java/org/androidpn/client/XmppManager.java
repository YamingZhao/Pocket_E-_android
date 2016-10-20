package org.androidpn.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * @author snox@live.com
 * @date 2015/10/29.
 */
public class XmppManager {

    public static String name = "";

    private static final String LOGTAG = XmppManager.class.getSimpleName();
    private static final String XMPP_RESOURCE_NAME = "AndroidpnClient";

    private Context context;
    private NotificationService.TaskSubmitter taskSubmitter;
    private NotificationService.TaskTracker taskTracker;

    private SharedPreferences sharedPreferences;

    private String xmppHost;
    private int xmppPort;
    private XMPPConnection connection;

    private String username;
    private String password;

    private ConnectionListener connectionListener;

    private PacketListener notificationPacketListener;

    private Handler handler;

    private List<Runnable> taskList;

    private boolean running= false;

    private Future<?> futureTask;

    private Thread reconnection;

    public XmppManager(NotificationService service) {
        context = service;

        taskSubmitter = service.getTaskSubmitter();
        taskTracker = service.getTaskTracker();
        sharedPreferences = service.getSharedPreferences();

        xmppHost = sharedPreferences.getString(Constants.XMPP_HOST, "115.28.171.100");
        xmppPort = sharedPreferences.getInt(Constants.XMPP_PORT, 5222);
        username = sharedPreferences.getString(Constants.XMPP_USERNAME, "test1");
        password = sharedPreferences.getString(Constants.XMPP_PASSWORD, "123456");

        connectionListener = new PersistentConnectionListener(this);
        notificationPacketListener = new NotificationPacketListener(this);

        handler = new Handler();
        taskList = new ArrayList<Runnable>();
        reconnection = new ReconnectionThread(this);
    }

    public Context getContext() {
        return context;
    }

    public void connect() {
        submitLoginTask();
    }

    public void disconnect() {
        terminatePersistentConnection();
    }

    public void terminatePersistentConnection() {
        Runnable runnable = new Runnable() {

            final XmppManager xmppManager = XmppManager.this;

            public void run() {
                if (xmppManager.isConnected()) {
                    xmppManager.getConnection().removePacketListener(
                            xmppManager.getNotificationPacketListener());
                    xmppManager.getConnection().disconnect();
                }
                xmppManager.runTask();
            }
        };
        addTask(runnable);
    }

    public XMPPConnection getConnection() {
        return connection;
    }

    public void setConnection(XMPPConnection connection) {
        this.connection = connection;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public PacketListener getNotificationPacketListener() {
        return notificationPacketListener;
    }

    public void startReconnectionThread() {
        synchronized (reconnection) {
            if (!reconnection.isAlive()) {
                reconnection.setName("Xmpp Reconnection Thread");
                reconnection.start();
            }
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void reregisterAccount() {
        removeAccount();
        submitLoginTask();
        runTask();
    }

    public List<Runnable> getTaskList() {
        return taskList;
    }

    public Future<?> getFutureTask() {
        return futureTask;
    }

    public void runTask() {
        synchronized (taskList) {
            running = false;
            futureTask = null;
            if (!taskList.isEmpty()) {
                Runnable runnable = (Runnable) taskList.get(0);
                taskList.remove(0);
                running = true;
                futureTask = taskSubmitter.submit(runnable);
                if (futureTask == null) {
                    taskTracker.decrease();
                }
            }
        }
        taskTracker.decrease();
    }

    private String newRandomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    private boolean isAuthenticated() {
        return isConnected() && connection.isAuthenticated();
    }

    private boolean isRegistered() {
        return sharedPreferences.contains(Constants.XMPP_USERNAME)
                && sharedPreferences.contains(Constants.XMPP_PASSWORD);
    }

    private void submitConnectTask() {
        addTask(new ConnectTask());
    }

    private void submitRegisterTask() {
        submitConnectTask();
        addTask(new RegisterTask());
    }

    private void submitLoginTask() {
        submitRegisterTask();
        addTask(new LoginTask());
    }

    private void addTask(Runnable runnable) {
        taskTracker.increase();
        synchronized (taskList) {
            if (taskList.isEmpty() && !running) {
                running = true;

                futureTask = taskSubmitter.submit(runnable);
                if (futureTask == null) {
                    taskTracker.decrease();
                }
            } else {
                taskList.add(runnable);
            }
        }
    }

    private void removeAccount() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.XMPP_USERNAME);
        editor.remove(Constants.XMPP_PASSWORD);
        editor.commit();
    }

    private class ConnectTask implements Runnable {

        final XmppManager xmppManager;

        private ConnectTask() {
            this.xmppManager = XmppManager.this;
        }

        @Override
        public void run() {
            if (!xmppManager.isConnected()) {
                ConnectionConfiguration configuration = new ConnectionConfiguration(xmppHost, xmppPort);

                configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
                configuration.setSASLAuthenticationEnabled(false);
                configuration.setCompressionEnabled(false);

                XMPPConnection connection = new XMPPConnection(configuration);
                xmppManager.setConnection(connection);

                try {
                    connection.connect();
                    Log.i(LOGTAG, "连接到XMPP服务器成功");

                    ProviderManager.getInstance().addIQProvider("notification",
                            "androidpn:iq:notification", new NotificationIQProvider());
                } catch (XMPPException e) {
                    e.printStackTrace();
                    Log.e(LOGTAG, "XMPP connection failed", e);
                }
                xmppManager.runTask();
            } else {
                Log.i(LOGTAG, "XMPP已经连接");
                xmppManager.runTask();
            }
        }
    }

    private class RegisterTask implements Runnable {
        final XmppManager xmppManager;

        private RegisterTask() {
            xmppManager = XmppManager.this;
        }

        @Override
        public void run() {
            if (!xmppManager.isRegistered()) {
                final String newUsername = newRandomUUID();
                final String newPassword = newRandomUUID();

                name = newUsername;
                Registration registration = new Registration();

                PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
                        registration.getPacketID()), new PacketTypeFilter(IQ.class));

                PacketListener packetListener = new PacketListener() {
                    @Override
                    public void processPacket(Packet packet) {

                        if (packet instanceof IQ) {
                            IQ response = (IQ) packet;
                            if (response.getType() == IQ.Type.ERROR) {
                                if (!response.getError().toString().contains("409")) {
                                    Log.e(LOGTAG, "Unknown error while registering XMPP account! " + response.getError().getCondition());
                                }
                            } else if (response.getType() == IQ.Type.RESULT) {
                                xmppManager.setUsername(newUsername);
                                xmppManager.setPassword(newPassword);

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constants.XMPP_USERNAME, newUsername);
                                editor.putString(Constants.XMPP_PASSWORD, newPassword);
                                editor.commit();

                                xmppManager.runTask();
                            }
                        }
                    }
                };

                connection.addPacketListener(packetListener, packetFilter);

                registration.setType(IQ.Type.SET);

                registration.addAttribute("username", newUsername);
                registration.addAttribute("password", newPassword);

                connection.sendPacket(registration);
            } else {
                name = username;
                xmppManager.runTask();
            }
        }
    }

    private class LoginTask implements Runnable {

        final XmppManager xmppManager;

        private LoginTask() {
            this.xmppManager = XmppManager.this;
        }

        @Override
        public void run() {
            if (!xmppManager.isAuthenticated()) {

                try {



                    xmppManager.getConnection().login(
                            xmppManager.getUsername(),
                            xmppManager.getPassword(),
                            XMPP_RESOURCE_NAME);

                    Log.d(LOGTAG, "成功登陆到服务器");

                    if (xmppManager.getConnectionListener() != null) {
                        xmppManager.getConnection().addConnectionListener(
                                xmppManager.getConnectionListener()
                        );
                    }

                    PacketFilter filter = new PacketTypeFilter(NotificationIQ.class);

                    PacketListener packetListener = xmppManager.getNotificationPacketListener();

                    connection.addPacketListener(packetListener, filter);

                    xmppManager.runTask();

                } catch (XMPPException e) {
                    e.printStackTrace();

                    Log.e(LOGTAG, "Login. Failed to login to xmpp server. Caused by: "+ e.getMessage());

                    String INVALID_CREDENTIALS_ERROR_CODE = "401";
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
                        xmppManager.reregisterAccount();    // 重新注册账号
                        return;
                    }
                    xmppManager.startReconnectionThread(); // 重连
                }
            } else {
                Log.i(LOGTAG, "已经登录啦");
                xmppManager.runTask();
            }

        }
    }
}
