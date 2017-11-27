package com.byteshaft.wahwege.fcm;

import android.util.Log;

import com.byteshaft.wahwege.utils.AppGlobals;
import com.byteshaft.wahwege.utils.Helpers;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FireBaseService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("TAG", "Token " + token);
        AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_FCM_TOKEN, token);

    }
}
