package com.example.chatgen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.chatgen.models.ChatbotEventResponse;
import com.example.chatgen.models.ConfigService;

public class BotWebView extends AppCompatActivity {

    WebviewOverlay wb;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Chatgen.getInstance().setLocalListener(botEvent -> {
            Log.d("botwebviewmohan", botEvent.getCode());
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
        wb = ConfigService.getInstance().getConfig().webView;
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

    @Override
    public void onBackPressed() {
        closeBot();
    }
}
