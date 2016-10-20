package net.wezu.jxg.ui.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.baidu.platform.comapi.map.B;

import net.wezu.jxg.data.PagedResult;
import net.wezu.jxg.data.RequestManager;
import net.wezu.jxg.model.UserMessage;
import net.wezu.jxg.service.MessageResult;
import net.wezu.jxg.service.UserService;
import net.wezu.jxg.ui.base.BaseListActivity;

import java.util.List;

/**
 * Created by snox on 2016/1/27.
 */
public class MessagesListActivity extends BaseListActivity<UserMessage, UserMessageViewHolder> {

    public static void startActivity(Activity activity) {
        activity.startActivity(new Intent(activity, MessagesListActivity.class));
    }

    @Override
    protected void refreshData() {
        UserService.getMessages(new RequestManager.ResponseListener<MessageResult>() {
            @Override
            public void success(MessageResult result, String msg) {
                addDataItems(result.Conversations);
            }

            @Override
            public void error(String msg) {

            }
        });
    }

    @Override
    protected boolean equalItem(UserMessage item1, UserMessage item2) {
        return false;
    }

    @Override
    protected int getListItemLayoutResourceId() {
        return 0;
    }

    @Override
    protected UserMessageViewHolder createViewHolder(Context context, View convertView) {
        return null;
    }
}
