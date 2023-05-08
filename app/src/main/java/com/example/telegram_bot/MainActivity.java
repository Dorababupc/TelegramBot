package com.example.telegram_bot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText botTokenEditText;
    private EditText chatIdEditText;
    private Button submitButton;
    private TextView resultTextView;
    private static final String PREFS_NAME = "myPrefs";
    private static final String KEY_BOT_TOKEN = "bot_token";
    private static final String KEY_CHAT_ID = "chat_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        botTokenEditText = findViewById(R.id.botTokenInput);
        chatIdEditText = findViewById(R.id.chatIdInput);
        submitButton = findViewById(R.id.submitButton);
        resultTextView = findViewById(R.id.resultMessage);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String botToken = botTokenEditText.getText().toString();
                String chatId = chatIdEditText.getText().toString();

                if (TextUtils.isEmpty(botToken) || TextUtils.isEmpty(chatId)) {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(KEY_BOT_TOKEN, botToken);
                editor.putString(KEY_CHAT_ID, chatId);
                editor.apply();

                // Show a Toast message to confirm that the values have been saved
                Toast.makeText(MainActivity.this, "Bot automation settings saved", Toast.LENGTH_SHORT).show();
            }
        });
        startService(new Intent(this, SMSService.class));
    }
}