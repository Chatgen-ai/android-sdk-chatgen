package com.example.chatgen_sdk_android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.chatgenmessenger.chat.Chatgen;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Chatgen chatgen;
    public Boolean sendMessageStatus = false;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        chatgen = Chatgen.getInstance();
        chatgen.init(this,"mllw2FpN");

        Random rand = new Random();
        int empSalary = rand.nextInt(100000);

        JSONObject visitorAttributes = new JSONObject();
        try {
            JSONObject customAttributes = new JSONObject();
            customAttributes.put("emp_name", "Mohan Sai Raju");
            customAttributes.put("emp_salary", empSalary);
            visitorAttributes.put("name", "Mohan"+empSalary);
            visitorAttributes.put("custom_attributes", customAttributes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        chatgen.identify(visitorAttributes);

        FrameLayout frameLayout = findViewById(R.id.relativeMine);

        chatgen.onEventFromBot(botEvent -> {
            Log.d("GoodLord", botEvent.getCode());
            if(botEvent.getCode() == "bot-loaded" && sendMessageStatus == true){
                sendMessageStatus = false;
                chatgen.sendMessage("Hello How are you");
            }
        });

        setContentView(R.layout.activity_main);
    }

    public void openChat(View v) {
        chatgen.startChatbotWithDialog(this, "");
    }

    public void startDialog(View v){
        chatgen.startChatbotWithDialog(this, "517155");
    }

    public void sendChatMessage(View v) {
        chatgen.startChatbotWithDialog(this, "");
        sendMessageStatus = true;
    }
}
