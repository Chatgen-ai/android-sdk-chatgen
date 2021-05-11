package com.chatgenmessenger.chat.models;

import com.chatgenmessenger.chat.ChatgenConfig;

public class ConfigService {
    private static ConfigService configInstance;
    private ChatgenConfig config;

    private ConfigService() {
        config = new ChatgenConfig("");
    }

    public static ConfigService getInstance() {
        if (configInstance == null) {
            synchronized (ConfigService.class) {
                if (configInstance == null) {
                    configInstance = new ConfigService();
                }
            }
        }
        return configInstance;
    }

    public boolean setConfigData(ChatgenConfig config) {
        if (config != null) {
            this.config = config;
            return true;
        }
        return false;
    }

    public void setWebViewActiveState(Boolean status) {
        config.isWebviewActive = status;
    }

    public void setActiveChatId(String activeChatId) {
        config.activeChatId = activeChatId;
    }

    public ChatgenConfig getConfig() {
        return config;
    }
}
