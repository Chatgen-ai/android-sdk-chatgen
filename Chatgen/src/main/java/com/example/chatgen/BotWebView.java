package com.example.chatgen;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;

import com.example.chatgen.models.ChatbotEventResponse;
import com.example.chatgen.models.ConfigService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;

public class BotWebView extends AppCompatActivity {

    WebviewOverlay wb;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Chatgen.getInstance().setLocalListener(botEvent -> {
            Log.d("botwebviewmohan", botEvent.getCode());
            switch (botEvent.getCode()){
                case "closeBot" :
                    closeBot();
                    break;
                case "sendMessage" :
                    wb.sendMessage(botEvent.getData());
                    break;
            }
        });
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.bot_web_view);
        copyAssets();

        wb = new WebviewOverlay();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container, wb).commit();
    }

    public void closeBot(){
        Chatgen.getInstance().emitEvent(new ChatbotEventResponse("bot-closed", ""));
        wb.closeBot();
        this.finish();
    }

    public void botLoaded(){
        wb.botLoaded();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeBot();
    }

    private void copyAssets() {
        File myDir = new File(this.getFilesDir(), "cg-widget");
        if(!myDir.isDirectory()){
            myDir.mkdir();
        }
        String widgetDirectory = this.getFilesDir() + "/cg-widget";
        AssetManager assetManager = this.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("www");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("www/"+filename);
                String outDir = widgetDirectory;
                File outFile = new File(outDir, filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch(IOException e) {
                Log.e("AssetCopyFailure", "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
