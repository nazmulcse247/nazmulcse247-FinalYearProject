package com.nazmul.finalyearproject.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nazmul.finalyearproject.Utils.Common;

import io.paperdb.Paper;

public class MyLocationReceiver extends BroadcastReceiver {

    public static final String ACTION = "com.nazmul.finalyearproject.UPDATE_LOCATION";

    DatabaseReference publication;
    String uid;

    public MyLocationReceiver() {
        publication = FirebaseDatabase.getInstance().getReference(Common.PUBLIC_LOCATION);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Paper.init(context);
        uid = Paper.book().read(Common.USER_UID_SAVE_KEY);

        if (intent != null){

            final String action = intent.getAction();
            if (action.equals(ACTION)){
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null){
                    Location location = result.getLastLocation();
                    if (Common.logedUser != null){   //app in foreground
                        publication.child(Common.logedUser.getUid()).setValue(location);
                    }
                    else {
                        publication.child(uid).setValue(location);
                    }
                }
            }

        }

    }
}
