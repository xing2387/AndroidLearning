package com.example.xing.androidlearning.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xing on 5/26/16.
 */
public class NotificationUtil {

    private NotificationManager mNotficationManager;
    private Map<Integer, Notification> notificationsMap = new HashMap<>();

    public NotificationUtil(Context context) {
        mNotficationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public boolean showNotification(String fileName){

        Notification notification = new Notification();

//        ViewGroup
        return false;
    }


}
