package com.example.chatgen;

public class ChatgenConfig {
    public String widgetKey;
    public String dialogId = "ChatGenLive";
    public WebviewOverlay webView = new WebviewOverlay();
    public ChatgenConfig(String widgetKey) {
        this.widgetKey = widgetKey;
    }
}
