package com.chatgen.chatgen_sdk_android;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chatgenmessenger.chat.Chatgen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Chatgen chatgen;
    Boolean sendMessageStatus = false;
    String interactionId = "517155";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        chatgen = Chatgen.getInstance();
        chatgen.init(this, "mllw2FpN");
        super.onCreate(savedInstanceState);
        chatgen.onEventFromBot(botEvent -> {
            if(botEvent.getCode() == "bot-loaded" && sendMessageStatus == true){
                sendMessageStatus = false;
                chatgen.sendMessage("Hello, I am John.");
            }
        });
        onNewIntent(getIntent());
        setContentView(R.layout.activity_main);
    }

    public void openChat(View v) {
        chatgen.startChatbot(this, false);
    }

    public void startDialog(View v){
        chatgen.startChatbotWithDialog(this, interactionId, true);
    }

    public void sendChatMessage(View v) {
        chatgen.startChatbot(this, false);
        sendMessageStatus = true;
    }

    public void setAttributes(View v) {
        Random rand = new Random();
        int empSalary = rand.nextInt(100000);
        JSONObject visitorAttributes = new JSONObject();
        try {
            visitorAttributes.put("name", "John");
            visitorAttributes.put("emp_salary", empSalary);
            visitorAttributes.put("emp_name", "Mohan SR "+empSalary);
            chatgen.identify(visitorAttributes);
            Context context = getApplicationContext();
            CharSequence text = "User's name is now set, Please click on START CHATBOT WITH DIALOG FLOW button now!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void subscribeNotifications(View v){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        CharSequence text = "Firebase Messaging Token is generated.";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                        toast.show();
                        // Log and toast
                        Log.d("Notifytoken", token);
                        chatgen.sendFCMToken(token);
                    }
                });
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if(extras != null) {
            if(chatgen.isChatGenPush(extras)){
                chatgen.pushMessage(getApplicationContext(), extras);
            }
        }
    }
}