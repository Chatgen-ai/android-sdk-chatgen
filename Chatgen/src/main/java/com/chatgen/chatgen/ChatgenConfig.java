package com.chatgen.chatgen;

public class ChatgenConfig {
    public String widgetKey;
    public String dialogId = "";
    public String version = "";
    public String serverRoot = "app";
    public String apiRoot = "api";
    public ChatgenConfig(String widgetKey) {
        this.widgetKey = widgetKey;
    }
}
