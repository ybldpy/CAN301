package com.can301.coursework.util;

import android.app.ProgressDialog;
import android.content.Context;

public class SyncUtil {


    public static ProgressDialog makeProcessDialog(Context context,String text,boolean cancelable){

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(text);
        progressDialog.setCancelable(cancelable);
        return progressDialog;
    }


}
