/*
 * [MainActivity.java]
 *
 * (c) 2019, Amazon.com, Inc. or its affiliates. All Rights Reserved.
 */

package com.amazon.sample.admmessenger;

import android.os.Bundle;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.amazon.device.messaging.ADM;
import com.amazon.sample.admmessenger.MyServerMsgHandler;

/**
 * Main activity of the sample app.
 * Provides a minimal UI to display messages coming from the server.
 *
 * @version Revision: 1, Date: 11/11/2012
 */
public class MainActivity extends Activity
{
    /** Tag for logs. */
    private final static String TAG = "ADMMessenger";

    /** Catches intents sent from the onMessage() callback to update the UI. */
    private BroadcastReceiver msgReceiver;

    /** {@inheritDoc} */
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tView = (TextView)findViewById(R.id.textMsgServer);
        tView.setMovementMethod(new ScrollingMovementMethod());

        /* Register app with ADM. */
        register();
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {     
        /* Handle item selection. */
        switch (item.getItemId()) {
            case R.id.menu_register:
                register();
                return true;
            case R.id.menu_unregister:
                unregister();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onResume()
    {
        /* String to access message field from data JSON. */
        final String msgKey = getString(R.string.json_data_msg_key);

        /* String to access timeStamp field from data JSON. */
        final String timeKey = getString(R.string.json_data_time_key);

        /* Intent action that will be triggered in onMessage() callback. */
        final String intentAction = getString(R.string.intent_msg_action);

        /* Intent category that will be triggered in onMessage() callback. */
        final String msgCategory = getString(R.string.intent_msg_category);

        final Intent nIntent = getIntent();
        if(nIntent != null)
        {
            /* Extract message from the extras in the intent. */
            final String msg = nIntent.getStringExtra(msgKey);
            final String srvTimeStamp = nIntent.getStringExtra(timeKey);

            /* If msgKey and timeKey extras exist then we're coming from clicking a notification intent. */
            if (msg != null && srvTimeStamp != null)
            {
                Log.i(TAG, msg);
                /* Display the message in the UI. */
                final TextView tView = (TextView)findViewById(R.id.textMsgServer);
                tView.append("Server Time Stamp: " + srvTimeStamp + "\nMessage from server: " + msg + "\n\n");

                /* Clear notifications if any. */
                final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(getResources().getInteger(R.integer.sample_app_notification_id));
            }
        }

        /* Listen for messages coming from SampleADMMessageHandler onMessage() callback. */
        msgReceiver = createBroadcastReceiver(msgKey, timeKey);
        final IntentFilter messageIntentFilter= new IntentFilter(intentAction);
        messageIntentFilter.addCategory(msgCategory);
        this.registerReceiver(msgReceiver, messageIntentFilter);
        super.onResume();
    }

    /**
     * Create a {@link BroadcastReceiver} for listening to messages from ADM.
     * 
     * @param msgKey String to access message field from data JSON.
     * @param timeKey String to access timeStamp field from data JSON.
     * @return {@link BroadcastReceiver} for listening to messages from ADM.
     */
    private BroadcastReceiver createBroadcastReceiver(final String msgKey,
            final String timeKey) 
    {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
        {

            /** {@inheritDoc} */
            @Override
            public void onReceive(final Context context, final Intent broadcastIntent)
            {
                if(broadcastIntent != null){

                    /* Extract message from the extras in the intent. */
                    final String msg = broadcastIntent.getStringExtra(msgKey);
                    final String srvTimeStamp = broadcastIntent.getStringExtra(timeKey);

                    if (msg != null && srvTimeStamp != null)
                    {
                        Log.i(TAG, msg);

                        /* Display the message in the UI. */
                        final TextView tView = (TextView)findViewById(R.id.textMsgServer);
                        tView.append("Server Time Stamp: " + srvTimeStamp + "\nMessage from server: " + msg + "\n\n");
                    }

                    /* Clear notifications if any. */
                    final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(context.getResources().getInteger(R.integer.sample_app_notification_id));
                }
            }
        };
        return broadcastReceiver;
    }
    
    /** {@inheritDoc} */
    @Override
    public void onPause()
    {
        this.unregisterReceiver(msgReceiver);
        super.onPause();
    }

    /**
     * Register the app with ADM and send the registration ID to your server
     */
    private void register()
    {
        final ADM adm = new ADM(this);
        if (adm.isSupported())
        {
            if(adm.getRegistrationId() == null)
            {
                adm.startRegister();
            } else {
                /* Send the registration ID for this app instance to your server. */
                /* This is a redundancy since this should already have been performed at registration time from the onRegister() callback */
                /* but we do it because our python server doesn't save registration IDs. */
                final MyServerMsgHandler srv = new MyServerMsgHandler();
                srv.registerAppInstance(getApplicationContext(), adm.getRegistrationId());
            }
        }
    }

    /**
     * Unregister the app with ADM. 
     * Your server will get notified from the SampleADMMessageHandler:onUnregistered() callback
     */
    private void unregister()
    {
        final ADM adm = new ADM(this);
        if (adm.isSupported())
        {
            if(adm.getRegistrationId() != null)
            {
                adm.startUnregister();
            }
        }
        final TextView tView = (TextView)findViewById(R.id.textMsgServer);
        tView.append("You are now unregistered\n\n");
    }
}
