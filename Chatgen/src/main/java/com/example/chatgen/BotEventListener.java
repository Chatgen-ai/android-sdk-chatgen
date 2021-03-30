package com.example.chatgen;

import com.example.chatgen.models.ChatbotEventResponse;

public interface BotEventListener {
    void onSuccess(ChatbotEventResponse botEvent);
}
