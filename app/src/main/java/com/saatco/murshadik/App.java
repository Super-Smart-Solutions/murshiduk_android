package com.saatco.murshadik;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import com.onesignal.OneSignal;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.saatco.murshadik.Helpers.ChatHelper;
import com.saatco.murshadik.fcm.LoginService;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.QBResRequestExecutor;
import com.saatco.murshadik.utils.SharedPrefsHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import me.leolin.shortcutbadger.ShortcutBadger;


/**
 * Created by Bassam Fouad
 */

public class App extends Application implements LifecycleEventObserver {

    //App credentials production
    private static final String APPLICATION_ID = "87912";
    private static final String AUTH_KEY = "DppnsjQXHBOy4FR";
    private static final String AUTH_SECRET = "SgQKhv6ecWPR7M2";
    private static final String ACCOUNT_KEY = "TEV4mDntUqZdYXbCRSSf";

    /*
App credentials testing
    private static final String APPLICATION_ID = "94859";
    private static final String AUTH_KEY = "eyJ7fB3khHJkD8u";
    private static final String AUTH_SECRET = "6GwwSD-ec6YXKMu";
    private static final String ACCOUNT_KEY = "sByAdF1Vz_dbDMZfzxmc";
*/


    public static final String USER_DEFAULT_PASSWORD = "quickblox";

    //Chat settings
    public static final int CHAT_PORT = 5223;
    public static final int SOCKET_TIMEOUT = 300;
    public static final boolean KEEP_ALIVE = true;
    public static final boolean USE_TLS = true;
    public static final boolean AUTO_MARK_DELIVERED = true;
    public static final boolean RECONNECTION_ALLOWED = true;
    public static final boolean ALLOW_LISTEN_NETWORK = true;


    private static App instance;

    public static QBResRequestExecutor qbResRequestExecutor;

    public static QBChatDialog groupJoinDialog;

    SharedPrefsHelper sharedPrefsHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        FirebaseApp.initializeApp(this);


        if (Build.VERSION.SDK_INT < 25) {

            try {
                // Google Play will install latest OpenSSL
                ProviderInstaller.installIfNeeded(getApplicationContext());
                SSLContext sslContext;
                sslContext = SSLContext.getInstance("TLSv1.2");
                sslContext.init(null, null, null);
                sslContext.createSSLEngine();

                Log.v("fornite", "yes");
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                    | NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
                Log.v("fornite", "no");

            }
        }

        boolean isFirstTime = PrefUtil.getBoolean(getApplicationContext(), "is_first_name");

        if (!isFirstTime) {
            LanguageUtil.saveLanguage(getApplicationContext(), "ar");
        }


        LanguageUtil.changeLanguage(this);


        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId("66cd2946-17f1-4129-a370-d06fb0ef9ebd");

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        initCredentials();

        if (qbResRequestExecutor == null)
            qbResRequestExecutor = new QBResRequestExecutor();

        // addObserver
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        sharedPrefsHelper = SharedPrefsHelper.getInstance();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


    }

    @Override
    public void onTerminate() {
        super.onTerminate();


    }

    ///////////////////////////////////////////////

    @Override
    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
        switch (event){
            case ON_RESUME:
                onAppResume();
                break;

            case ON_STOP:
                onAppStop();
                break;

            case ON_DESTROY:
                onAppDestroy();
                break;

        }
    }
    public void onAppResume() {
        Log.d("AppController", "Foreground");

        PrefUtil.writeBooleanValue(getApplicationContext(), "is_chat_pick", false);

        if (!QBChatService.getInstance().isLoggedIn()) {
            if (sharedPrefsHelper.hasQbUser()) {
                QBUser qbUser2 = sharedPrefsHelper.getQbUser();
                LoginService.start(getApplicationContext(), qbUser2);
            }
        }

    }



    public void onAppStop() {
        Log.d("AppController", "Background ON_STOP");
        //  unJoinGroup();
        disconnectWithChat();
        int shortcut_badger_counter = PrefUtil.getInteger(this, Consts.SHORTCUT_BADGER_COUNTER) +
                PrefUtil.getInteger(getApplicationContext(), Consts.SHORTCUT_BADGER_COUNTER_CHAT_MSG);
        if (shortcut_badger_counter > 0)
            ShortcutBadger.applyCount(getApplicationContext(), shortcut_badger_counter);

    }

    public void onAppDestroy() {
        Log.d("AppController", "Background ON_DESTROY");
        unJoinGroup();
        PrefUtil.writeBooleanValue(getApplicationContext(), "is_chat_pick", false);
        disconnectWithChat();
    }





    private void initCredentials() {
        QBSettings.getInstance().init(getApplicationContext(), APPLICATION_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

    }

    public static Context getInstance() {
        return instance;
    }

    public static boolean isInternetAvailable() {

        ConnectivityManager cm =
                (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    protected void unJoinGroup() {

        Log.v("thor", "leavess group");

        if (groupJoinDialog != null) {

            try {
                groupJoinDialog.leave();
                groupJoinDialog = null;
            } catch (XMPPException | SmackException.NotConnectedException ignored) {

            }
        }

    }



    private void disconnectWithChat() {


        if (!PrefUtil.getBoolean(getInstance(), "is_chat_pick")) {
            boolean isLoggedIn = ChatHelper.getInstance().getQbChatService().isLoggedIn();
            if (!isLoggedIn) {
                return;
            }

            ChatHelper.getInstance().getQbChatService().logout(new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    ChatHelper.getInstance().getQbChatService().destroy();
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }
    }



}


