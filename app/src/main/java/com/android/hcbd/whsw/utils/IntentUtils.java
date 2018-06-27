package com.android.hcbd.whsw.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

/**
 * Created by guocheng on 2017/3/31.
 */

public class IntentUtils {

    public static Intent getWordFileIntent(Context context,String param) {
        Intent intent = null;
        try {
            intent = new Intent("android.intent.action.VIEW");
            Uri uri = null;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                //intent.addCategory("android.intent.category.DEFAULT");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                uri = FileProvider.getUriForFile(context, "com.android.hcbd.whsw.provider", new File(param ));
            }else{
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                uri = Uri.fromFile(new File(param));
            }
            intent.setDataAndType(uri, "application/msword");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    //android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(Context context,String param ){
        Intent intent = null;
        try {
            intent = new Intent("android.intent.action.VIEW");
            Uri uri = null;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                //intent.addCategory("android.intent.category.DEFAULT");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                uri = FileProvider.getUriForFile(context, "com.android.hcbd.whsw.provider", new File(param ));
            }else{
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                uri = Uri.fromFile(new File(param));
            }
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    //android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent( String param ){
        Intent intent = null;
        try {
            intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(param ));
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    //android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent( String param ){
        Intent intent = null;
        try {
            intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(param ));
            intent.setDataAndType(uri, "application/x-chm");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    //android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent( String param, boolean paramBoolean){
        Intent intent = null;
        try {
            intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (paramBoolean){
                Uri uri1 = Uri.parse(param );
                intent.setDataAndType(uri1, "text/plain");
            }else{
                Uri uri2 = Uri.fromFile(new File(param ));
                intent.setDataAndType(uri2, "text/plain");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    //android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent( String param ){
        Intent intent = null;
        try {
            intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromFile(new File(param ));
            intent.setDataAndType(uri, "application/pdf");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

    /**
     * 判断Intent 是否存在 防止崩溃
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        return list.size() > 0;
    }

}
