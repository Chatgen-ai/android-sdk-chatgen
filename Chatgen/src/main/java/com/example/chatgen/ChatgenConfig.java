package com.example.chatgen;

import android.webkit.WebView;

public class ChatgenConfig {
    public String widgetKey;
    public String dialogId = "ChatGenLive";
    public WebviewOverlay webView = new WebviewOverlay();
    public ChatgenConfig(String widgetKey) {
        this.widgetKey = widgetKey;
    }
}
