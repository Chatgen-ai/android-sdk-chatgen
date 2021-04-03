package com.example.chatgen.models;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.example.chatgen.BotWebView;
import com.example.chatgen.Chatgen;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public class JavaScriptInterface {
    protected BotWebView parentActivity;
    protected WebView mWebView;

    public JavaScriptInterface(BotWebView _activity, WebView _webView)  {
        parentActivity = _activity;
        mWebView = _webView;
    }

    @JavascriptInterface
    public void  receiveMessage(String s) {
        ChatbotEventResponse incomingEvent = new Gson().fromJson(s, ChatbotEventResponse.class);
        // Pass-through events (Bot will not close)
        Map<String, Object> retMap = new Gson().fromJson(
                incomingEvent.data, new TypeToken<HashMap<String, Object>>() {}.getType());
        Log.d("Event from Bot", "receiveMessage: "+incomingEvent.code);
        Chatgen.getInstance().emitEvent(incomingEvent);
    }

    @JavascriptInterface
    public void closeBot() {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.closeBot();
                parentActivity.finish();
            }
        });
    }

    @JavascriptInterface
    public void botLoaded() {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.botLoaded();
            }
        });
    }

    @JavascriptInterface
    public void onMessage() {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.onMessage();
            }
        });
    }
}

