package net.wezu.jxg.pay;

/**
 * Created by snox on 2016/5/11.
 */
public interface IMobilePayService<T extends PayInit, K extends Product> {

    public void init(T innit);

    public String getPayOrderDetail(K product);

    public void pay(K product);

    public void setOnPayListener(OnPayListener listener);

    public boolean checkPayChannel();

    public String getPayVersion();
}
