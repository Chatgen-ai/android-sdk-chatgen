package com.chatgen.chatgen;

public class ChatgenConfig {
    public String widgetKey;
    public String dialogId = "";
    public String version = "";
    public String serverRoot = "app2";
    public String apiRoot = "api2";
    public ChatgenConfig(String widgetKey) {
        this.widgetKey = widgetKey;
    }
}
