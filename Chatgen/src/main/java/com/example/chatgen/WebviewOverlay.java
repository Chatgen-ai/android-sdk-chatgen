package com.example.chatgen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.chatgen.models.ChatbotEventResponse;
import com.example.chatgen.models.ConfigService;
import com.example.chatgen.models.JavaScriptInterface;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class WebviewOverlay extends Fragment {
    private final String TAG = "ChatGenChat";
    private WebView myWebView;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    public long start;
    public long end = 0;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myWebView = (WebView) preLoadWebView();
        return myWebView;
    }

    //File picker activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data.getDataString() == null) {
                    // If there is no data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }

            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {

                if (null == this.mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
        return;
    }


    public View preLoadWebView() {
        // Preload start
        final Context context = getActivity();
        myWebView = new WebView(context);
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
        myWebView.addJavascriptInterface(new JavaScriptInterface((BotWebView) getActivity(), myWebView), "ChatgenHandler");

        myWebView.setWebViewClient(new myWebClient());

        myWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebViewConsoleMessage", consoleMessage.message());
                return true;
            }
        });
        String widgetKey = ConfigService.getInstance().getConfig().widgetKey;
        String yourFilePath = "file:///" + context.getFilesDir() + "/cg-widget/load.html";
        File yourFile = new File( yourFilePath );
        String botUrl = yourFile.toString();
        botUrl += "?server=test&key=" + widgetKey + "&interactionId=" + ConfigService.getInstance().getConfig().dialogId + "&isChatGenSDK=1";
        Log.d("WebViewConsoleMessage", "URL = "+botUrl);
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

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
            //Your code to do
            Toast.makeText(getActivity(), "Your Internet Connection May not be active Or " + error.getDescription(), Toast.LENGTH_LONG).show();
        }
    }
}

