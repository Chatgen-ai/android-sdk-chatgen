package com.chatgenmessenger.chat;

import org.json.JSONObject;

public class ChatgenConfig {
    public String widgetKey;
    public String dialogId = "";
    public String version = "";
    public String serverRoot = "app";
    public String apiRoot = "api";
    public String activeChatId = "";
    public Boolean isWebviewActive = false;
    public Boolean continuePreviousChat = false;
    public String fcmToken = "";
    public JSONObject visitorAttributes;
    public ChatgenConfig(String widgetKey) {
        this.widgetKey = widgetKey;
    }
}
