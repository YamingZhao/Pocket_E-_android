package net.wezu.jxg.receiver;

/**
 * Created by snox on 2016/3/12.
 */
public class PushMessageCommand {

    public static final String NEWORDER_TO_WORKER = "0001";
    public static final String ORDER_CATCHED_TO_USER = "0002";
    public static final String ORDER_WORKER_START_TO_USER = "0004";
    public static final String ORDER_CANCELED_TO_WORKER = "0006";
    public static final String ORDER_WORKER_ADD_TIPFEE = "0008";
    public static final String ORDER_OFFLINEPAY_TO_WORKER = "0009";

    public static final String ORDER_PAYED_TO_WORKER = "0010";
    public static final String ORDER_OFFLINEPAY_TO_USER = "0011";


    public static final String KICK_OUT = "0020";

}
