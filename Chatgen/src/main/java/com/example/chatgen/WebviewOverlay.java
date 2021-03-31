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

import static android.app.Activity.RESULT_OK;

public class WebviewOverlay extends Fragment {
    private final String TAG = "ChatGenChat";
    private WebView myWebView;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
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
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setSupportMultipleWindows(true);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.getSettings().setAllowFileAccess(true);
        myWebView.getSettings().setGeolocationDatabasePath(context.getFilesDir().getPath());
        if (Build.VERSION.SDK_INT > 17) {
            myWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        myWebView.addJavascriptInterface(new JavaScriptInterface((BotWebView) getActivity(), myWebView), "ChatgenHandler");

        myWebView.setWebViewClient(new myWebClient());

        myWebView.setWebChromeClient(new WebChromeClient() {

            private View mCustomView;
            private CustomViewCallback mCustomViewCallback;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebViewConsoleMessage", consoleMessage.message());
                return true;
            }

            public void onHideCustomView() {
                ((FrameLayout) getActivity().getWindow().getDecorView()).removeView(this.mCustomView);
                this.mCustomView = null;
                getActivity().getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                getActivity().setRequestedOrientation(this.mOriginalOrientation);
                this.mCustomViewCallback.onCustomViewHidden();
                this.mCustomViewCallback = null;
            }

            public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
                if (this.mCustomView != null) {
                    onHideCustomView();
                    return;
                }
                this.mCustomView = paramView;
                this.mOriginalSystemUiVisibility = getActivity().getWindow().getDecorView().getSystemUiVisibility();
                this.mOriginalOrientation = getActivity().getRequestedOrientation();
                this.mCustomViewCallback = paramCustomViewCallback;
                ((FrameLayout) getActivity().getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                getActivity().getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(context);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(url));
                        startActivity(browserIntent);
                        return true;
                    }
                });
                return true;
            }
        });
        String widgetKey = ConfigService.getInstance().getConfig().widgetKey;
        String interactionId = ConfigService.getInstance().getConfig().dialogId;
        String botUrl = "https://test-nlp.selekt.in/public/empty.html?server=test&key="+widgetKey;
        if(!interactionId.isEmpty()){
          botUrl = botUrl + "#" + interactionId;
        }
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
        String interactionId = ConfigService.getInstance().getConfig().dialogId;
        String finalJsScript = "javascript:(ChatGen.openWidget())";
        myWebView.post(new Runnable() {
            @Override
            public void run() {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    Log.d("botloadedifi", finalJsScript);
                    myWebView.evaluateJavascript(finalJsScript, null);
                }
                else {
                    Log.d("botloadedifi", finalJsScript);
                    myWebView.loadUrl(finalJsScript);
                }
            }
        });
        Chatgen.getInstance().emitEvent(new ChatbotEventResponse("bot-loaded", ""));
    }

    // Sending messages to bot
    public void sendMessage(String s) {
        Log.d("sendingmessage", "tt = " + s);
        String jsScript = "javascript:(setTimeout(function(){ChatGen.sendMessage('"+s+"')}, 3000))";
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

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
        }
    }
}

