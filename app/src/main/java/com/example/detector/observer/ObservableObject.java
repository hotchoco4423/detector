package com.example.detector.observer;

import android.util.Log;

import java.util.Observable;

public class ObservableObject extends Observable {

    private static final String TAG = "Observer";

    private static ObservableObject instance = new ObservableObject();

    public static ObservableObject getInstance() {
        Log.d(TAG, "getInstance()");
        return instance;
    }

    private ObservableObject() {
        Log.d(TAG, "ObservableObject()");
    }

    public void updateValue(Object data) {
        Log.d(TAG, "updateValue()");
        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }
}
