package com.example.chatgen;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.chatgen.models.ChatbotEventResponse;
import com.example.chatgen.models.ConfigService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Chatgen {

    private static Context webViewContext;
    private static Intent webViewIntent;
    private static Chatgen botPluginInstance;
    private static ChatgenConfig config;
    private static WebView webView;
    private int mCount;
    private TextView countView;
    private static BotEventListener botListener;
    private static BotEventListener localListener;

    public Chatgen(){
        this.botListener = botEvent -> {};
    }

    public static Chatgen getInstance(){
        if (botPluginInstance == null) {
            synchronized (Chatgen.class) {
                if (botPluginInstance == null) {
                    botPluginInstance = new Chatgen();
                }
            }
        }
        return  botPluginInstance;
    }

    public void setLocalListener(BotEventListener localListener){
        this.localListener = localListener;
    }
    public void onEventFromBot(BotEventListener botListener){
        this.botListener = botListener;
    }

    public void init(Context context, String s) {
        config = new ChatgenConfig(s);
        config.webView = new WebviewOverlay();
        ConfigService.getInstance().setConfigData(config);
        Log.d("INIT", "copy assets");
        copyAssets(context);
    }

    public void startChatbot(Context context) {
        config.dialogId = "";
        webViewContext = context;
        webViewIntent = new Intent(webViewContext, BotWebView.class);
        webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        webViewContext.startActivity(webViewIntent);
    }

    public void startChatbotWithDialog(Context context, String dialogId) {
        Log.d("WebViewConsoleMessage", "Start ChatBot with dialog");
        config.dialogId = dialogId;
        webViewContext = context;
        webViewIntent = new Intent(webViewContext, BotWebView.class);
        webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        webViewContext.startActivity(webViewIntent);
    }

    public void closeBot(){
        localListener.onSuccess(new ChatbotEventResponse("closeBot", ""));
    }

    public void sendMessage(String s){
        localListener.onSuccess(new ChatbotEventResponse("sendMessage", s));
    }

    public void emitEvent(ChatbotEventResponse event){
        if(event != null){
            botListener.onSuccess(event);
            localListener.onSuccess(event);
        }
    }

    private void copyAssets(Context context) {
        File myDir = new File(context.getFilesDir(), "cg-widget");
        if(!myDir.isDirectory()){
            myDir.mkdir();
        }
        String widgetDirectory = context.getFilesDir() + "/cg-widget";
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("cg-widget");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("cg-widget/" +filename);
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
