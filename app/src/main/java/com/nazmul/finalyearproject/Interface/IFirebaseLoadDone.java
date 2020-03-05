package com.nazmul.finalyearproject.Interface;

import java.util.List;

public interface IFirebaseLoadDone {

    void onFirebaseLoadUserNameDone(List<String> lstEmail);
    void onFirebaseLoadFaild(String message);
}
