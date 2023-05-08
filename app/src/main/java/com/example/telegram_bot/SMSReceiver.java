package com.example.telegram_bot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private static final String PREFS_NAME = "sms_receiver_prefs";
    private static final String PREF_LAST_MESSAGE_TIME = "last_message_time";

    private static final String KEY_BOT_TOKEN = "bot_token";
    private static final String KEY_CHAT_ID = "chat_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "reached");
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                long currentTime = System.currentTimeMillis();
                SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                long lastMessageTime = prefs.getLong(PREF_LAST_MESSAGE_TIME, 0);
                StringBuilder messageBuilder = new StringBuilder();
                String senderNumber = "";
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    messageBuilder.append(smsMessage.getMessageBody());
                    senderNumber = smsMessage.getOriginatingAddress();
                }
                String message = messageBuilder.toString();
                Log.d(TAG, "Received SMS message: " + message);
                if (currentTime - lastMessageTime >= 100) {
                    new TelegramSenderAsyncTask().execute(context, message, senderNumber);
                    prefs.edit().putLong(PREF_LAST_MESSAGE_TIME, currentTime).apply();
                } else {
                    Log.d(TAG, "Skipping message forwarding because it's too soon since the last message.");
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
            String name = (String) params[2];
            Log.d(TAG, msg);
            String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";


            SharedPreferences prefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
            String apiToken = prefs.getString(KEY_BOT_TOKEN, "");
            String chatId = prefs.getString(KEY_CHAT_ID, "");
            // Concatenate sender number and message with a line separator
            String text = "From: " + name + "-"+  "\t" + msg;

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

