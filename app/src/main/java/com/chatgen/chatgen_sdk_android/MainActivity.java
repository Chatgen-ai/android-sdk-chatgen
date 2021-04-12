package com.chatgen.chatgen_sdk_android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.chatgen.chatgen.Chatgen;

public class MainActivity extends AppCompatActivity {
    Chatgen chatgen;
    public Boolean sendMessageStatus = false;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        chatgen = Chatgen.getInstance();
        FrameLayout frameLayout = findViewById(R.id.relativeMine);
        chatgen.init(this,"kvMYnFrH");

        chatgen.onEventFromBot(botEvent -> {
            Log.d("GoodLord", botEvent.getCode());
            if(botEvent.getCode() == "bot-loaded" && sendMessageStatus == true){
                sendMessageStatus = false;
                chatgen.sendMessage("Hello How are you");
            }
        });

        setContentView(R.layout.activity_main);
    }

    public void openChat(View v) {
        chatgen.startChatbotWithDialog(this, "816892");
    }

    public void startDialog(View v){
        chatgen.startChatbotWithDialog(this, "493784");
    }

    public void sendChatMessage(View v) {
        chatgen.startChatbotWithDialog(this, "816892");
        sendMessageStatus = true;
    }
}