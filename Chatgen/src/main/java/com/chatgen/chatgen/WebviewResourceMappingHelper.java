package com.chatgen.chatgen;

import com.chatgen.chatgen.Chatgen;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebviewResourceMappingHelper {
    private static WebviewResourceMappingHelper instance;
    private List<LocalAssetMapModel> localAssetMapModelList;
    private List<String> overridableExtensions = new ArrayList<>(Arrays.asList("js", "css", "png", "jpg", "woff", "ttf", "eot", "ico"));

    private WebviewResourceMappingHelper(){

    }

    public static WebviewResourceMappingHelper getInstance(){
        if(instance == null){
            instance = new WebviewResourceMappingHelper();
        }
        return instance;
    }

    public String getLocalAssetPath(String url){
        Log.d("LocalAssetPath: ", url);
        if(StringUtils.isEmpty(url)){
            return "";
        }
//        if(localAssetMapModelList == null){
//            localAssetMapModelList = getLocalAssetList();
//        }
//        if(CollectionUtils.isNotEmpty(localAssetMapModelList)){
//            for(LocalAssetMapModel localAssetMapModel : localAssetMapModelList){
//                if(localAssetMapModel.url.equals(url)){
//                    return localAssetMapModel.asset_url;
//                }
//            }
//        }
        return url;
    }

    public String getLocalFilePath(String url, Context context){
        String localFilePath = "";
        String fileNameForUrl = getLocalFileNameForUrl(url);
        if(StringUtils.isNotEmpty(fileNameForUrl) && fileExists(fileNameForUrl, context)){
            localFilePath = getFileFullPath(fileNameForUrl, context);
        }
        return localFilePath;
    }

    public String getLocalFileNameForUrl(String url){
        String localFileName = "";
        String[] parts = url.split("/");
        if(parts.length > 0){
            localFileName = parts[parts.length-1];
        }
        return localFileName;
    }

    private boolean fileExists(String fileName, Context context){
        String path = context.getFilesDir() + "/cg-widget/" + fileName;
        return new File(path).exists();
    }

    private String getFileFullPath(String relativePath, Context context){
        return context.getFilesDir() + "/cg-widget/" + relativePath;
    }

//    private List<LocalAssetMapModel> getLocalAssetList(){
//        List<LocalAssetMapModel> localAssetMapModelList = new ArrayList<>();
//        String pageData = null;
//        try {
//            pageData = ResourceAccessHelper.getJsonData(Application.getCurrentInstance(), "web-assets/map.json");
//        } catch (IOException e) {
//        }
//        if(pageData !=null){
//            Type listType = new TypeToken<ArrayList<LocalAssetMapModel>>() {
//            }.getType();
//            localAssetMapModelList = new Gson().fromJson(pageData,listType);
//        }
//
//        pageData = null;
//        try {
//            pageData = ResourceAccessHelper.getJsonData(Application.getCurrentInstance(), "web-assets/fonts-map.json");
//        } catch (IOException e) {
//        }
//        if(pageData !=null){
//            Type listType = new TypeToken<ArrayList<LocalAssetMapModel>>() {
//            }.getType();
//            List<LocalAssetMapModel> fontsMap = new Gson().fromJson(pageData,listType);
//            localAssetMapModelList.addAll(fontsMap);
//        }
//        return localAssetMapModelList;
//    }

    public List<String> getOverridableExtensions(){
        return overridableExtensions;
    }

    public String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public String getMimeType(String fileExtension){
        String mimeType = "";
        switch (fileExtension){
            case "css" :
                mimeType = "text/css";
                break;
            case "js" :
                mimeType = "text/javascript";
                break;
            case "png" :
                mimeType = "image/png";
                break;
            case "jpg" :
                mimeType = "image/jpeg";
                break;
            case "ico" :
                mimeType = "image/x-icon";
                break;
            case "woff" :
            case "ttf" :
            case "eot" :
                mimeType = "application/x-font-opentype";
                break;
        }
        return mimeType;
    }

    public static WebResourceResponse getWebResourceResponseFromAsset(String assetPath, String mimeType, String encoding, Context context) throws IOException{
        InputStream inputStream =  context.getAssets().open(assetPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int statusCode = 200;
            String reasonPhase = "OK";
            Map<String, String> responseHeaders = new HashMap<String, String>();
            responseHeaders.put("Access-Control-Allow-Origin", "*");
            return new WebResourceResponse(mimeType, encoding, statusCode, reasonPhase, responseHeaders, inputStream);
        }
        return new WebResourceResponse(mimeType, encoding, inputStream);
    }

    public static WebResourceResponse getWebResourceResponseFromFile(String filePath, String mimeType, String encoding) throws FileNotFoundException {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int statusCode = 200;
            String reasonPhase = "OK";
            Map<String, String> responseHeaders = new HashMap<String, String>();
            responseHeaders.put("Access-Control-Allow-Origin","*");
            return new WebResourceResponse(mimeType, encoding, statusCode, reasonPhase, responseHeaders, fileInputStream);
        }
        return new WebResourceResponse(mimeType, encoding, fileInputStream);
    }

    private class LocalAssetMapModel{
        String url;
        String asset_url;
    }
}
