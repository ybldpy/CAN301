package com.can301.coursework.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

public class ViewUtil {

    public static int dp2pixel(Context context, int dp){
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(dp * density);
    }


    public static AlertDialog makeDialog(Context context, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        // 设置自定义布局
        builder.setView(view);
        // 创建对话框
        final AlertDialog customDialog = builder.create();
        return customDialog;
    }

    public static AlertDialog makeAlertDialog(Context context,String title,String message,String positiveBtnMessage,String negativeBtnMessage,DialogInterface.OnClickListener positiveListener,DialogInterface.OnClickListener negativeListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveBtnMessage, positiveListener);
        builder.setNegativeButton(negativeBtnMessage,negativeListener);
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }


    public static Activity searchActivityFromView(View view){

        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;


    }
}
