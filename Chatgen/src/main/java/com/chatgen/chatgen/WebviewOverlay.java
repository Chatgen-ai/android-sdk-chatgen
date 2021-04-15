package com.chatgen.chatgen;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chatgen.chatgen.models.ChatbotEventResponse;
import com.chatgen.chatgen.models.ConfigService;
import com.chatgen.chatgen.models.JavaScriptInterface;

import java.io.File;

public class WebviewOverlay extends Fragment{
    private static final int RESULT_OK = 1;
    private final String TAG = "ChatGenChat";
    private WebView myWebView;
    public long start;
    public long end = 0;

    public ValueCallback<Uri[]> uploadMessage;
    private ValueCallback<Uri> mUploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myWebView = (WebView) preLoadWebView();
        return myWebView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != WebviewOverlay.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(getContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }

    public View preLoadWebView() {
        // Preload start
        final Context context = getActivity();
        myWebView = new WebView(context);
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(myWebView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setSupportMultipleWindows(true);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.getSettings().setAllowFileAccess(true);
        myWebView.getSettings().setAllowContentAccess(true);
        myWebView.getSettings().setGeolocationDatabasePath(context.getFilesDir().getPath());
        //Performance
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            myWebView.setWebContentsDebuggingEnabled(true);
        }
        myWebView.addJavascriptInterface(new JavaScriptInterface((BotWebView) getActivity(), myWebView), "ChatgenHandler");
        CookieManager cookieManager = CookieManager.getInstance();

        cookieManager.setAcceptCookie(true);
        cookieManager.acceptCookie();
        cookieManager.setAcceptFileSchemeCookies(true);
        cookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(myWebView, true);
        }


        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("WebViewConsoleMessage", "on page finished ========>");
                new Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                boolean aid = CookieManager.getInstance().hasCookies();
                                String foo = cookieManager.getCookie("file:///android_asset/cg-widget/index.html");
                                Log.d("WebViewConsoleMessage", "doesIt: " + aid + " foo: " + foo + " at: " + myWebView.getUrl());
                            }
                        },
                        7000
                );
            }
        });

        myWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebViewConsoleMessage", consoleMessage.message());
                return true;
            }

            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }


            // For Lollipop 5.0+ Devices
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    Toast.makeText(context, "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }
        });
        String botUrl = getBotUrl(context);
        myWebView.loadUrl(botUrl);
        this.start = System.nanoTime();
        return myWebView;
    }


    //Empty url string on bot-close
    public void closeBot() {
        myWebView.loadUrl("");
    }

    public void onMessage() {
        Log.d("WebViewConsoleMessage", "received");
        if (this.end == 0) {
            this.end = System.nanoTime();
            Log.d("WebViewConsoleMessage", "took " + (end - start)/1000000 + " ms");
        }
    }

    //Widget will call this function when bot is completely loaded
    public void botLoaded() {
        Chatgen.getInstance().emitEvent(new ChatbotEventResponse("bot-loaded", ""));
    }

    // Sending messages to bot
    public void sendMessage(String s) {
        String jsScript = "javascript:(setTimeout(function(){ChatGen.sendMessage('"+s+"')}, 500))";
        myWebView.post(new Runnable() {
            @Override
            public void run() {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    Log.d("SendingMessageScript", jsScript);
                    myWebView.evaluateJavascript(jsScript, null);
                }
                else {
                    Log.d("SendingMessageScript2", jsScript);
                    myWebView.loadUrl(jsScript);
                }
            }
        });
    }

    private String getBotUrl(Context context) {
        String widgetKey = ConfigService.getInstance().getConfig().widgetKey;
        String widgetVersion = ConfigService.getInstance().getConfig().version;
        String serverRoot = ConfigService.getInstance().getConfig().serverRoot;
        String interactionId = ConfigService.getInstance().getConfig().dialogId;
        String configUrl = "?server=" + serverRoot + "&key=" + widgetKey + "&interactionId=" + interactionId + "&isChatGenSDK=1";
        String botUrl = "file:///android_asset/cg-widget/load.html" + configUrl;
        File widgetDir = new File(context.getFilesDir(), "cg-widget-"+widgetVersion);
        if(widgetDir.isDirectory()){
            String filePath = "file:///" + context.getFilesDir() + "/cg-widget-" + widgetVersion + "/load.html";
            File yourFile = new File( filePath );
            botUrl = yourFile.toString() + configUrl;
        }
        return botUrl;
    }
}

