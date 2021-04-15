package com.example.chatgen.models;

import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.example.chatgen.BotWebView;
import com.example.chatgen.Chatgen;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public class JavaScriptInterface {
    protected BotWebView parentActivity;
    protected WebView mWebView;
    protected CookieManager cookieManager;

    public JavaScriptInterface(BotWebView _activity, WebView _webView)  {
        parentActivity = _activity;
        mWebView = _webView;
        cookieManager = CookieManager.getInstance();
    }

    @JavascriptInterface
    public String getCookie() {
        Log.d("WebViewConsoleMessage", "get cookie: " + cookieManager.getCookie("https://foo.com"));
        return cookieManager.getCookie("https://foo.com");
    }

    @JavascriptInterface
    public void setCookie(String value) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("WebViewConsoleMessage", "set cookie: " + value);
                cookieManager.setCookie("https://foo.com", value);
                String result = cookieManager.getCookie("https://foo.com");
            }
        });
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

