package com.example.chatgen;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatgen.models.ChatbotEventResponse;
import com.example.chatgen.models.ConfigService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

public class Chatgen {

    private static Context webViewContext;
    private static Intent webViewIntent;
    private static Chatgen botPluginInstance;
    private static ChatgenConfig config;
    private static WebView myWebView;
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
        ConfigService.getInstance().setConfigData(config);
        Log.d("INIT", "copy assets");
        copyAssets(context);
        loadWebview(context);
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

    public void loadWebview(Context context){
        myWebView = new WebView(context);

        String widgetKey = ConfigService.getInstance().getConfig().widgetKey;
        String yourFilePath = "file:///" + context.getFilesDir() + "/cg-widget/load.html";
        File yourFile = new File( yourFilePath );
        String botUrl = yourFile.toString();
        botUrl += "?server=test&key=" + widgetKey;

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setSupportMultipleWindows(true);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.getSettings().setAllowFileAccess(true);
        myWebView.getSettings().setGeolocationDatabasePath(context.getFilesDir().getPath());
        //Performance
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            myWebView.setWebContentsDebuggingEnabled(true);
        }

        myWebView.setWebViewClient(new WebViewClient());

        myWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebViewConsoleMessage", consoleMessage.message());
                return true;
            }
        });

        Log.d("WebViewConsoleMessage", "URL = "+botUrl);
        myWebView.loadUrl(botUrl);
    }
}
