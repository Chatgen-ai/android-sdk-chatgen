package com.example.chatgen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatgen.models.ChatbotEventResponse;
import com.example.chatgen.models.ConfigService;
import com.example.chatgen.models.JavaScriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class WebviewOverlay extends Fragment {
    private WebView myWebView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myWebView = (WebView) preLoadWebView();
        return myWebView;
    }


    public View preLoadWebView() {
        // Preload start
        final Context context = getActivity();

        myWebView = new WebView(context);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setSupportMultipleWindows(true);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.getSettings().setAllowFileAccess(true);
        myWebView.getSettings().setGeolocationDatabasePath(context.getFilesDir().getPath());


        if (Build.VERSION.SDK_INT > 17) {
            myWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        myWebView.addJavascriptInterface(new JavaScriptInterface((BotWebView) getActivity(), myWebView), "ChatgenHandler");

        myWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebViewConsole", consoleMessage.message());
                return true;
            }
        });
        String widgetKey = ConfigService.getInstance().getConfig().widgetKey;
        String interactionId = ConfigService.getInstance().getConfig().dialogId;
        String botUrl = "https://test-nlp.selekt.in/public/empty.html?server=test&key="+widgetKey+"#"+interactionId;
        Log.d("botURL", "= "+botUrl);
        myWebView.loadUrl(botUrl);
        return myWebView;
    }

    //Empty url string on bot-close
    public void closeBot() {
        myWebView.loadUrl("");
    }

    //Widget will call this function when bot is completely loaded
    public void botLoaded() {
        String jsScript = "javascript:(ChatGen.openWidget())";
        myWebView.post(new Runnable() {
            @Override
            public void run() {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    Log.d("botloadedifi", "condition");
                    myWebView.evaluateJavascript(jsScript, null);
                }
                else {
                    Log.d("botloadedifi", "Condition");
                    myWebView.loadUrl(jsScript);
                }
            }
        });
        Chatgen.getInstance().emitEvent(new ChatbotEventResponse("bot-loaded", ""));
    }

    // Sending messages to bot
    public void sendMessage(String s) {
        Log.d("sendingmessage", "tt = " + s);
        String jsScript = "javascript:(ChatGen.sendMessage('"+s+"'))";
        myWebView.post(new Runnable() {
            @Override
            public void run() {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    Log.d("ifiams", "condition");
                    myWebView.evaluateJavascript(jsScript, null);
                }
                else {
                    Log.d("Elseiam", "Condition");
                    myWebView.loadUrl(jsScript);
                }
            }
        });
    }

    public void openWidget(String s) {
        String jsScript = "";
        if(s != ""){
            jsScript = "javascript:(function startInteraction(){ChatGen.startInteraction({interactionId:'"+s+"'})})();";
        } else {
            jsScript = "javascript:(function startInteraction(){ChatGen.startNewChat()})();";
        }
        String finalJsScript = jsScript;
        myWebView.post(new Runnable() {
            @Override
            public void run() {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    Log.d("checkDialogIDS", "dID = "+ finalJsScript);
                    myWebView.evaluateJavascript(finalJsScript, null);
                }
                else {
                    myWebView.loadUrl(finalJsScript);
                }
            }
        });
    }
}

