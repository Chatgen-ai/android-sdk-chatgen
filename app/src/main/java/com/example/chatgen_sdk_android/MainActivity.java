package com.example.chatgen_sdk_android;

import android.os.Bundle;
import android.os.Looper;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
        chatgen = Chatgen.getInstance();
        FrameLayout frameLayout = findViewById(R.id.relativeMine);
        chatgen.init(this,"kvMYnFrH", frameLayout);

        chatgen.onEventFromBot(botEvent -> {
            Log.d("GoodLord", botEvent.getCode());
            if(botEvent.getCode() == "bot-loaded" && sendMessageStatus == true){
                sendMessageStatus = false;
                chatgen.sendMessage("Hello How are you");
            }
        });

        setContentView(R.layout.activity_main);
    }

    public void openChat(View v){
        chatgen.startChatbot(this);
    }

    public void startDialog(View v){
        chatgen.startChatbotWithDialog(this, "mohansairaju");
    }

    public void sendChatMessage(View v) {
        chatgen.startChatbotWithDialog(this, "live");
        sendMessageStatus = true;
    }
}
