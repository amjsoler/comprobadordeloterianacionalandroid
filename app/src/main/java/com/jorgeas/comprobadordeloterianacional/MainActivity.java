package com.jorgeas.comprobadordeloterianacional;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    boolean permiso = false;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d("debug", "callback - granted");
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    permiso = true;
                } else {
                    Log.d("debug", "callback - notgranted");
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    permiso = false;
                }
            });

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        App.setContext(this);
        App.setWebView(new WebView(App.getContext()));
        App.getWebView().getSettings().setJavaScriptEnabled(true);
        App.getWebView().getSettings().setDomStorageEnabled(true);
        App.getWebView().addJavascriptInterface(new WebAppInterface(), "Android");
        setContentView(App.getWebView());

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            App.setFirebaseToken("");
                        }else{
                            // Get new FCM registration token
                            App.setFirebaseToken(task.getResult());
                        }

                        //myWebView.loadUrl("https://app.comprobadordeloterianacional.com?firebasetoken="+firebaseToken);
                        App.getWebView().loadUrl("http://192.167.1.102:8080?firebasetoken="+App.getFirebaseToken());
                    }
                });

        //Suscripción al canal general para recibir notificaciones globales
        FirebaseMessaging.getInstance().subscribeToTopic("general");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && App.getWebView().canGoBack()) {
            App.getWebView().goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    public boolean solicitarPermisoNotificacionesPadre() {
        Log.d("debug", "solicitarPermiso");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(
                    App.getContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                Log.d("debug", "solicitarPermiso - if");
                permiso = true;
                // You can use the API that requires the permission.
                //requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                Log.d("debug", "solicitarPermiso - else");
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                permiso = false;
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }else{
            permiso = true;
        }

        return permiso;
    }

    public class WebAppInterface {
        @JavascriptInterface
        public boolean solicitarPermisoNotificaciones() {
            solicitarPermisoNotificacionesPadre();

            return permiso;
        }
    }
}