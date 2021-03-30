package com.example.chatgen;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.chatgen.models.ChatbotEventResponse;
import com.example.chatgen.models.ConfigService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Chatgen {

    private static Context webViewContext;
    private static Intent webViewIntent;
    private static Chatgen botPluginInstance;
    private static ChatgenConfig config;
    private int mCount;
    private TextView countView;
    private WebView webView;
    private static BotEventListener botListener;
    private static BotEventListener localListener;
    public Chatgen(){
        this.botListener = botEvent -> {};
    }

    public static Chatgen getInstance(){
        if (botPluginInstance == null) {
            synchronized (Chatgen.class) {
                if (botPluginInstance == null) {
                    botPluginInstance = new Chatgen();
                }
            }
        }
        return  botPluginInstance;
    }

    public void setLocalListener(BotEventListener localListener){
        this.localListener = localListener;
    }
    public void onEventFromBot(BotEventListener botListener){
        this.botListener = botListener;
    }

    public static void init(String s) {
        config = new ChatgenConfig(s);
        config.webView = new WebviewOverlay();
        ConfigService.getInstance().setConfigData(config);
    }

    public static void startChatbot(Context context) {
        config.dialogId = "ChatGenLive";
        webViewContext = context;
        webViewIntent = new Intent(webViewContext, BotWebView.class);
        webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        webViewContext.startActivity(webViewIntent);
    }

    public static void startChatbotWithDialog(Context context, String dialogId) {
        config.dialogId = dialogId;
        webViewContext = context;
        webViewIntent = new Intent(webViewContext, BotWebView.class);
        webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        webViewContext.startActivity(webViewIntent);
    }

    public static void closeBot(){
        localListener.onSuccess(new ChatbotEventResponse("closeBot", ""));
    }

    public static void sendMessage(String s){
        localListener.onSuccess(new ChatbotEventResponse("sendMessage", s));
    }

    public void emitEvent(ChatbotEventResponse event){
        if(event != null){
            botListener.onSuccess(event);
            localListener.onSuccess(event);
        }
    }
}
