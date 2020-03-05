package com.nazmul.finalyearproject;

import android.Manifest;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nazmul.finalyearproject.Model.User;
import com.nazmul.finalyearproject.Utils.Common;

import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {



    DatabaseReference user_information;
    private static final int MY_REQUEST_CODE =7117;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Paper.init(this);

        //init firebase

        user_information = FirebaseDatabase.getInstance().getReference(Common.USER_INFORMATION);

        //init providers

        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()

        );

        //Request permission

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        showSignInOption();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this,"You Must accept Permission",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();


    }


    private void showSignInOption(){
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .build(),MY_REQUEST_CODE);

    }



    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE){

            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK){
                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                //Check User already Exist in database

                user_information.orderByKey()
                        .equalTo(firebaseUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null){

                                    if (!dataSnapshot.child(firebaseUser.getUid()).exists()){

                                        Common.logedUser = new User(firebaseUser.getUid(),firebaseUser.getEmail());
                                        user_information.child(Common.logedUser.getUid())
                                                .setValue(Common.logedUser);

                                    }



                                }

                                else {
                                    Common.logedUser =dataSnapshot.child(firebaseUser.getUid()).getValue(User.class);

                                }

                                //save uid and set background
                                Paper.book().write(Common.USER_UID_SAVE_KEY,Common.logedUser.getUid());

                                updateToken(firebaseUser);
                                setupUI();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }
        }
    }

    private void setupUI() {
        startActivity(new Intent(MainActivity.this,HomeActivity.class));
        finish();


    }

    private void updateToken(final FirebaseUser firebaseUser) {

        final DatabaseReference token = FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        token.child(firebaseUser.getUid())
                                .setValue(instanceIdResult.getToken());

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,""+e.getMessage(),Toast.LENGTH_LONG).show();

            }
        });


    }

    //button long press


}
