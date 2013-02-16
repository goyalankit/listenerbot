package com.example.listener_bot;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.google.android.gcm.GCMRegistrar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private TextView textView;
    private String message = "NO_MESSAGE";

    public static final String PREFS_NAME = "MyPrefsFile";
    private int count;
    private ListView mainListView;
    private Button buttonClear;
    private ArrayAdapter<String> listAdapter ;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerGMS(this);

        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.main);

        textView = (TextView) findViewById(R.id.mainText);

        Button buttonStart = (Button)findViewById(R.id.buttonClear);
        buttonStart.setOnClickListener(startListener); // Register the onClick listener with the implementation abov

        List<String> messages = new ArrayList<String>();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        int j = 0;
        String temp_message;
        while(true){
            j++;
            temp_message = settings.getString("message"+j, null);
            if(temp_message == null)
                break;
            else{
            messages.add(temp_message);
            }
        }

        populateList(messages);
        textView.setText("Messages");
    }

    protected void onStop(){
        super.onStop();
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

    public void populateList(List<String> dataList){
        mainListView = (ListView) findViewById( R.id.mainListView );
        listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, dataList);
        mainListView.setAdapter( listAdapter );
    }

    private OnClickListener startListener = new OnClickListener() {
        public void onClick(View v) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
            Toast.makeText(MainActivity.this, "cache cleared", Toast.LENGTH_LONG).show();
            List<String> dataList = new ArrayList<String>();
            populateList(dataList);
        }
    };

    private void registerGMS(Context context){
        GCMRegistrar.checkDevice(context);
        GCMRegistrar.checkManifest(context);
        final String regId = GCMRegistrar.getRegistrationId(context);
        if (regId.equals("")) {
            GCMRegistrar.register(context, "YOUR_SENDER_ID");
        } else {
            Log.v("Registration", "Already registered");
        }
    }

}
