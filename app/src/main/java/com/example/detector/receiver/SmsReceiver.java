package com.example.detector.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.detector.model.MainMessage;
import com.example.detector.observer.ObservableObject;
import com.example.detector.request.RequestUrl;
import com.example.detector.service.PopupService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SMS_RECEIVER";

    private Context context;

    public SmsReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceiver() 호출");

        this.context = context;

        NetworkTask task = new NetworkTask(intent);
        task.execute();
    }

    public class NetworkTask extends AsyncTask<Void, Void, MainMessage> {

        Object arg;

        public NetworkTask(Object arg) {
            this.arg = arg;
        }

        @Override
        protected MainMessage doInBackground(Void... voids) {
            Intent intent = (Intent) arg;
            Bundle bundle = intent.getExtras();
            SmsMessage[] messages = parseSmsMessage(bundle);

            String sender = "";
            String content = "";
            Date date = null;
            String time = "";
            if (messages.length > 0) {
                sender = messages[0].getOriginatingAddress();
                content = messages[0].getMessageBody().toString();
                date = new Date(messages[0].getTimestampMillis());
                time = date.getHours() + ":" + date.getMinutes();
            }

            RequestUrl request = new RequestUrl();
            int count = request.request(content);

            String result = "";

            if(count == -1) {
                result = "존재하지 않는 URL";
            } else if(count <= 3) {
                result = "위험";
            } else {
                result = "안전";
            }

            MainMessage msg = new MainMessage(result, sender, content, time);

            return msg;
        }

        @Override
        protected void onPostExecute(MainMessage mainMessage) {
            super.onPostExecute(mainMessage);

            Intent service = new Intent(context, PopupService.class);
            service.putExtra("msg", mainMessage);
            context.startService(service);

            ObservableObject.getInstance().updateValue(mainMessage);
        }

        private SmsMessage[] parseSmsMessage(Bundle bundle) {
            Object[] objs = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[objs.length];

            for(int i=0; i<objs.length; i++){
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
                Log.d(TAG, "messages[" + i + "] : " + messages[i]);
            }

            return messages;
        }
    }
}