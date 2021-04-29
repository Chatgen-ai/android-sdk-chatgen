package com.chatgenmessenger.chat;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.chatgenmessenger.chat.R;
import com.chatgenmessenger.chat.models.ChatbotEventResponse;

public class BotWebView extends AppCompatActivity {

    WebviewOverlay wb;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Chatgen.getInstance().setLocalListener(botEvent -> {
            switch (botEvent.getCode()){
                case "closeBot" :
                    closeBot();
                    break;
                case "sendMessage" :
                    wb.sendMessage(botEvent.getData());
                    break;
            }
        });
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.bot_web_view);
        wb = new WebviewOverlay();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container, wb).commit();
    }

    public void closeBot(){
        Chatgen.getInstance().emitEvent(new ChatbotEventResponse("bot-closed", ""));
        wb.closeBot();
        this.finish();
    }

    public void botLoaded(){
        wb.botLoaded();
    }

    public void onMessage() {
        wb.onMessage();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeBot();
    }
}

