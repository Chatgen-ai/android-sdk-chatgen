package com.chatgen.chatgen;

import com.chatgen.chatgen.models.ChatbotEventResponse;

public interface BotEventListener {
    void onSuccess(ChatbotEventResponse botEvent);
}
