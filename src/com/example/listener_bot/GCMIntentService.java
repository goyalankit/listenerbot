package com.example.listener_bot;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class GCMIntentService extends GCMBaseIntentService {

    public static final String PREFS_NAME = "MyPrefsFile";
    private int count;

    private void generateNotification(Context context, String message, Intent receivedIntent) {
        message = receivedIntent.getExtras().get("data").toString();

        writeToCache(message);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.bot)
                        .setContentTitle("listener bot")
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                        .setAutoCancel(true);


        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("message",message);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)

        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    @Override
    protected void onError(Context arg0, String arg1) {
        Log.e("Registration", "Got an error!");
        Log.e("Registration", arg0.toString() + arg1.toString());
    }

    @Override
    protected void onMessage(Context arg0, Intent arg1) {
        Log.i("Registration", "Got a message!");
        Log.i("Registration", arg1.getExtras().get("data").toString());
        generateNotification(arg0, "", arg1);
        // Note: this is where you would handle the message and do something in your app.
    }

    @Override
    protected void onRegistered(Context arg0, String arg1) {
        Log.i("Registration", "Just registered!");
        Log.i("Registration", arg0.toString() + arg1.toString());
        //postData("http://192.168.1.4:9000/register", "");
        // This is where you need to call your server to record the device toekn and registration id.
    }

    @Override
    protected void onUnregistered(Context arg0, String arg1) {
    }

    public void postData(String url, String secret, String push_key) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("secret", secret));
            nameValuePairs.add(new BasicNameValuePair("push_key", push_key));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

    public void writeToCache(String message){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        count =settings.getInt("number_of_entries",0);
        SharedPreferences.Editor editor = settings.edit();

        if(message!=null && message!="NO_MESSAGE" && message != "no messages")
            count = count + 1;
        editor.putInt("number_of_entries",count);
        editor.putString("message"+count,message);
        editor.commit();
    }


}
