package com.example.lufthansa;

import android.content.Context;

public interface EventListener {
    public void receiveData(int typeOfRequest, String[] params);
    public Context getCtx();
}
