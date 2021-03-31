package com.example.chatgen;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
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
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(android.R.id.content), (v, insets) -> {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                    params.bottomMargin = insets.getSystemWindowInsetBottom();
                    return insets.consumeSystemWindowInsets();
                });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeBot();
    }
}
