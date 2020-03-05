package com.nazmul.finalyearproject.Remote;

import android.app.DownloadManager;

import com.nazmul.finalyearproject.Model.MyResponse;
import com.nazmul.finalyearproject.Model.Request;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({

            "Content-Type:application/json",
            "Authorization:key=AAAA6I52Zwc:APA91bFgn2LD6n59xqj9aLSfMd1h9pcpNsseWKCeSwI4HdV-hGb3ZKxEbTz8FEZ1j_hbkhBxjGtlT5JESzQxfIyKx7tya4QpOKQo5HHkHcZYHL8We4hFeMv0sy6aGr4q28bgXSt8PnkUeBlcGjvYdI2qZp5l4jZabw"


    })

    @POST("fcm/send")
    Observable<MyResponse> toFriendRequestToUser(@Body Request body);
}
