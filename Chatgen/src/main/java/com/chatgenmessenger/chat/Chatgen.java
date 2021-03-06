package com.chatgenmessenger.chat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chatgenmessenger.chat.models.ChatbotEventResponse;
import com.chatgenmessenger.chat.models.ConfigService;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.chatgenmessenger.chat.WebviewResourceMappingHelper.getWebResourceResponseFromAsset;

public class Chatgen {
    private static Context webViewContext;
    private static Intent webViewIntent;
    private static Chatgen botPluginInstance;
    private static ChatgenConfig config;
    private static WebView myWebView;
    private static BotEventListener botListener;
    private static BotEventListener localListener;

    String baseUrl = "https://chatgen-static-files.s3.ap-south-1.amazonaws.com/";
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
        getRemoteAssets(context);
        preLoadWebView(context);
    }

    public void startChatbot(Context context, Boolean continuePreviousChat) {
        config.dialogId = "";
        config.continuePreviousChat = continuePreviousChat;
        webViewContext = context;
        webViewIntent = new Intent(webViewContext, BotWebView.class);
        webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        webViewContext.startActivity(webViewIntent);
    }

    public void startChatbotWithDialog(Context context, String dialogId, Boolean continuePreviousChat) {
        config.continuePreviousChat = continuePreviousChat;
        config.dialogId = dialogId;
        webViewContext = context;
        webViewIntent = new Intent(webViewContext, BotWebView.class);
        webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        webViewContext.startActivity(webViewIntent);
    }

    public void identify(JSONObject visitorAttributes) {
        config.visitorAttributes = visitorAttributes;
    }

    public void closeBot(){
        localListener.onSuccess(new ChatbotEventResponse("closeBot", ""));
    }

    public void sendMessage(String s){
        localListener.onSuccess(new ChatbotEventResponse("sendMessage", s));
    }

    public static void emitEvent(ChatbotEventResponse event){
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
                                        File myDir = new File(context.getFilesDir(), widgetDirectoryName);
                                        if(!myDir.isDirectory()){
                                            myDir.mkdir();
                                        }
                                        String widgetDirectory = context.getFilesDir() + "/" + widgetDirectoryName;
                                        JSONArray files;
                                        files = (JSONArray) response.get("files");
                                        for(int n=0; n < files.length(); n++){
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
                                                Log.d("webviewconsole", filename);
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

    public void preLoadWebView(Context context){
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

        myWebView.setWebViewClient(new WebViewClient() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String resourceUrl = request.getUrl().toString();
                String fileExtension = WebviewResourceMappingHelper.getInstance().getFileExt(resourceUrl);
                if(WebviewResourceMappingHelper.getInstance().getOverridableExtensions().contains(fileExtension)){
                    String encoding = "UTF-8";
                    String assetName = WebviewResourceMappingHelper.getInstance().getLocalAssetPath(resourceUrl);
                    if (StringUtils.isNotEmpty(assetName)) {
                        String mimeType = WebviewResourceMappingHelper.getInstance().getMimeType(fileExtension);
                        if (StringUtils.isNotEmpty(mimeType)) {
                            try {
                                return getWebResourceResponseFromAsset(assetName, mimeType, encoding, context);
                            } catch (IOException e) {
                                return super.shouldInterceptRequest(view, request);
                            }
                        }
                    }
                    String localFilePath = WebviewResourceMappingHelper.getInstance().getLocalFilePath(resourceUrl, context);
                    if (StringUtils.isNotEmpty(localFilePath)) {
                        String mimeType = WebviewResourceMappingHelper.getInstance().getMimeType(fileExtension);
                        if(StringUtils.isNotEmpty(mimeType)){
                            try {
                                return WebviewResourceMappingHelper.getWebResourceResponseFromFile(localFilePath, mimeType, encoding);
                            } catch (FileNotFoundException e) {
                                return super.shouldInterceptRequest(view,request);
                            }
                        }
                    }
                }
                if (fileExtension.endsWith("jpg")) {
                    try {
                        InputStream inputStream = WebviewOverlay.readFromCacheSync(resourceUrl);
                        if (inputStream != null) {
                            return new WebResourceResponse("image/jpg", "UTF-8", inputStream);
                        }
                    } catch (Exception e) {
                        return super.shouldInterceptRequest(view,request);
                    }
                }
                return super.shouldInterceptRequest(view,request);
            }

        });

        myWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("PreloadWebviewConsole", consoleMessage.message());
                return true;
            }
        });
        myWebView.loadUrl(botUrl);
    }

    public void sendFCMToken(String fcmToken){
        config.fcmToken = fcmToken;
    }

    public void pushMessage(Context context, Map message, Integer notificationIcon) {

        if(ConfigService.getInstance().getConfig().isWebviewActive){
            return;
        }
        String chatId = (String) message.get("chat_id");
        config.activeChatId = chatId;
        Intent intent = new Intent(context, BotWebView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Resources resources = context.getResources();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "channel_id")
                .setContentTitle((CharSequence) message.get("title"))
                .setContentText((CharSequence) message.get("body"))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setContentInfo((CharSequence) message.get("title"))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(notificationIcon);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification Channel is required for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("channel description");
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    public Boolean isChatGenPush(Map message) {
        try{
            String org = (String) message.get("org");
            if(org.equals("ChatGen")) {
                return true;
            }
            return false;
        }catch (NullPointerException e) {
            return false;
        }
    }

    public Boolean isChatGenPush(Bundle extras) {
        try {
            String org = extras.getString("org");
            if(org.equals("ChatGen")){
                return true;
            }
            return false;
        }catch (NullPointerException e){
            return false;
        }
    }

    public void pushMessage(Context context, Bundle extras) {
        try {
            String chat_id = extras.getString("chat_id");
            config.activeChatId = chat_id;
            webViewContext = context;
            webViewIntent = new Intent(webViewContext, BotWebView.class);
            webViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            webViewContext.startActivity(webViewIntent);
        }catch (NullPointerException e){
            Log.d("NULLPOINTER", e.getMessage());
        }
    }
}

