package com.chatgen.chatgen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.android.volley.toolbox.Volley;
import com.chatgen.chatgen.models.ChatbotEventResponse;
import com.chatgen.chatgen.models.ConfigService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Chatgen {

    private static Context webViewContext;
    private static Intent webViewIntent;
    private static Chatgen botPluginInstance;
    private static ChatgenConfig config;
    private static WebView myWebView;
    private static BotEventListener botListener;
    private static BotEventListener localListener;

    String baseUrl = "https://app.chatgen.ai/assets/";
    String cgWidgetVersion = "cg-widget-version";

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
        getRemoteAssets(context);
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

    public void getRemoteAssets(Context context) {
        String apiRoot = ConfigService.getInstance().getConfig().apiRoot;
        String url = "https://"+apiRoot+".chatgen.ai/helper/getSDKMeta";
        SharedPreferences preferences = context.getSharedPreferences(cgWidgetVersion, Context.MODE_PRIVATE);
        String defaultVersion = "";
        String storedVersion = preferences.getString(cgWidgetVersion, defaultVersion);
        config.version = storedVersion;

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String currentVersion = null;
                        try {
                            currentVersion = (String) response.get("version");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(currentVersion.equals(storedVersion)){
                            return;
                        } else {
                            String finalCurrentVersion = currentVersion;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // Display the first 500 characters of the response string.
                                    try {
                                        String widgetDirectoryName = "cg-widget-"+ finalCurrentVersion;
                                        Log.d("currentVersion", widgetDirectoryName);
                                        File myDir = new File(context.getFilesDir(), widgetDirectoryName);
                                        if(!myDir.isDirectory()){
                                            myDir.mkdir();
                                        }
                                        String widgetDirectory = context.getFilesDir() + "/" + widgetDirectoryName;
                                        JSONArray files;
                                        files = (JSONArray) response.get("files");
                                        for(int n=1; n < files.length(); n++){
                                            String assetName = files.getString(n);
                                            final String assetUrl = baseUrl + assetName;
                                            URL url = new URL(assetUrl);
                                            String filename = assetName.substring(assetName.lastIndexOf("/") +1);
                                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                                            OutputStream out = null;
                                            try {
                                                File outFile = new File(widgetDirectory, filename);
                                                out = new FileOutputStream(outFile);
                                                copyFile(in, out);
                                                Log.d("FileCopied", filename);
                                                in.close();
                                                in = null;
                                                out.flush();
                                                out.close();
                                                out = null;
                                            } catch(IOException e) {
                                                Log.e("AssetCopyFailure", "Failed to copy asset file: " + filename, e);
                                            }
                                        }
                                        preferences.edit().putString(cgWidgetVersion, finalCurrentVersion).apply();
                                        config.version = finalCurrentVersion;
                                    } catch (JSONException | MalformedURLException e) {
                                        Log.e("JSONException", e.getMessage());
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        Log.e("IOException", e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(jsonRequest);
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

        String serverRoot = ConfigService.getInstance().getConfig().serverRoot;
        String widgetKey = ConfigService.getInstance().getConfig().widgetKey;
        String botUrl = "file:///android_asset/cg-widget/load.html";
        botUrl += "?server="+serverRoot+"&key=" + widgetKey;

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

