package com.chatgenmessenger.chat;

import com.chatgenmessenger.chat.models.ChatbotEventResponse;

public interface BotEventListener {
    void onSuccess(ChatbotEventResponse botEvent);
}
