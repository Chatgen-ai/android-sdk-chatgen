package com.chatgenmessenger.chat;

import org.json.JSONObject;

public class ChatgenConfig {
    public String widgetKey;
    public String dialogId = "";
    public String version = "";
    public String serverRoot = "app";
    public String apiRoot = "api";
    public JSONObject visitorAttributes;
    public ChatgenConfig(String widgetKey) {
        this.widgetKey = widgetKey;
    }
}
