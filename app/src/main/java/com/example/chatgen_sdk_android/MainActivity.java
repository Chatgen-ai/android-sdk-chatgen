package com.example.chatgen_sdk_android;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatgen.Chatgen;
import com.example.chatgen.ChatgenConfig;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Chatgen chatgen;
    public Boolean sendMessageStatus = false;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatgen = new Chatgen();
        chatgen.init("kvMYnFrH");

        chatgen.getInstance().onEventFromBot(botEvent -> {
            Log.d("GoodLord", botEvent.getCode());
            if(botEvent.getCode() == "bot-loaded" && sendMessageStatus == true){
                sendMessageStatus = false;
                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            public void run() {
                                chatgen.sendMessage("Hello How are you");
                            }
                        },
                        3000);
            }
        });
    }

    public void openChat(View v){
        chatgen.startChatbotWithDialog(this, "live");
    }

    public void startDialog(View v){
        chatgen.startChatbotWithDialog(this, "mohansairaju");
    }

    public void sendChatMessage(View v) {
        chatgen.startChatbotWithDialog(this, "live");
        sendMessageStatus = true;
    }
}
