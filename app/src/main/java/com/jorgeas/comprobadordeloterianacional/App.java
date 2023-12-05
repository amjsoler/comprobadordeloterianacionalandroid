package com.jorgeas.comprobadordeloterianacional;

import android.app.Application;
import android.content.Context;
import android.webkit.WebView;

public class App extends Application {
    private static WebView webView;

    private static Context mContext;

    private static String firebaseToken;


    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context mContext) {
        App.mContext = mContext;
    }

    public static WebView getWebView() {
        return webView;
    }

    public static void setWebView(WebView webView) {
        App.webView = webView;
    }

    public static String getFirebaseToken() {
        return App.firebaseToken;
    }

    public static void setFirebaseToken(String firebaseToken) {
        App.firebaseToken = firebaseToken;
    }
}
