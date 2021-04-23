package com.chatgen.chatgen;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.chatgen.chatgen.models.ChatbotEventResponse;
import com.chatgen.chatgen.models.ConfigService;
import com.chatgen.chatgen.models.JavaScriptInterface;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.disk.FileCache;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.cache.StagingArea;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
                        InputStream inputStream = readFromCacheSync(resourceUrl);
                        if (inputStream != null) {
                            return new WebResourceResponse("image/jpg", "UTF-8", inputStream);
                        }
                    } catch (Exception e) {
                        return super.shouldInterceptRequest(view,request);
                    }
                }
                return super.shouldInterceptRequest(view,request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
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
            @SuppressLint("LongLogTag")
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebViewConsoleMessage- Line- " + consoleMessage.lineNumber(), consoleMessage.message());
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
                    myWebView.evaluateJavascript(jsScript, null);
                }
                else {
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
            String rawPath = context.getFilesDir() + "/cg-widget-" + widgetVersion + "/load.html";
            File rawFile = new File(rawPath);
            if(rawFile.exists()){
                String filePath = "file:///" + rawPath;
                File loadFile = new File( filePath );
                botUrl = loadFile.toString() + configUrl;
            }
        }
        return botUrl;
    }


    public WebResourceResponse getWebResourceResponseFromAsset(String assetPath, String mimeType, String encoding, Context context) throws IOException {
        InputStream inputStream =  getActivity().getAssets().open(assetPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int statusCode = 200;
            String reasonPhase = "OK";
            Map<String, String> responseHeaders = new HashMap<String, String>();
            responseHeaders.put("Access-Control-Allow-Origin", "*");
            return new WebResourceResponse(mimeType, encoding, statusCode, reasonPhase, responseHeaders, inputStream);
        }
        return new WebResourceResponse(mimeType, encoding, inputStream);
    }

    public static InputStream readFromCacheSync(String imageUrl) {
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(imageUrl), null);
        StagingArea stagingArea = StagingArea.getInstance();
        EncodedImage encodedImage = stagingArea.get(cacheKey);
        if (encodedImage != null) {
            return encodedImage.getInputStream();
        }

        try {
            return readFromDiskCache(cacheKey);
        } catch (Exception e) {
            return null;
        }
    }

    private static InputStream readFromDiskCache(final CacheKey key) throws IOException {
        try {
            FileCache fileCache = ImagePipelineFactory.getInstance().getMainFileCache();
            final BinaryResource diskCacheResource = fileCache.getResource(key);
            if (diskCacheResource == null) {
                FLog.v("Webviewconsole", "Disk cache miss for %s", key.toString());
                return null;
            }
            PooledByteBuffer byteBuffer;
            final InputStream is = diskCacheResource.openStream();
            FLog.v("Webviewconsole", "Successful read from disk cache for %s", key.toString());
            return is;
        } catch (IOException ioe) {
            return null;
        }
    }
}
