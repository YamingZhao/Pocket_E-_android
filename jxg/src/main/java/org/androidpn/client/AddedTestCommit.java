package org.androidpn.client;

/**
 * Created by i310736(Yaming.Zhao@sap.com) on 10/14/2017.
 */

public class AddedTestCommit {
    private String singleFlag;

    public AddedTestCommit(String singleFlag) {
        this.singleFlag = singleFlag;
    }

    private void echoTest(String test){
        singleFlag = "private";
    }
}
