package com.example.chatgen.models;

import com.example.chatgen.Chatgen;
import com.example.chatgen.ChatgenConfig;

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

    public ChatgenConfig getConfig() {
        return config;
    }
}
