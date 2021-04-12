package com.chatgen.chatgen.models;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.chatgen.chatgen.BotWebView;

public class JavaScriptInterface {
    protected BotWebView parentActivity;
    protected WebView mWebView;

    public JavaScriptInterface(BotWebView _activity, WebView _webView)  {
        parentActivity = _activity;
        mWebView = _webView;
    }

    @JavascriptInterface
    public void closeBot() {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.closeBot();
                parentActivity.finish();
            }
        });
    }

    @JavascriptInterface
    public void botLoaded() {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.botLoaded();
            }
        });
    }

    @JavascriptInterface
    public void onMessage() {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.onMessage();
            }
        });
    }
}

