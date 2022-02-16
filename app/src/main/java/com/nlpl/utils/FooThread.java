package com.nlpl.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class FooThread extends  Thread{
    Handler mHandler;

    public FooThread(Handler h) {
        mHandler = h;
    }

    public void run() {
        Message msg = mHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("state", 1);
        msg.setData(b);
        mHandler.sendMessage(msg);
    }
}
