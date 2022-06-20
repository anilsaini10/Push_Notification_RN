/*
 * [SampleADMMessageHandler.java]
 *
 * (c) 2019, Amazon.com, Inc. or its affiliates. All Rights Reserved.
 */

package com.amazon.sample.admmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amazon.device.messaging.ADMConstants;
import com.amazon.device.messaging.ADMMessageHandlerBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The SampleADMMessageHandler class receives messages sent by ADM via the SampleADMMessageReceiver receiver.
 *
 * @version Revision: 1, Date: 11/11/2012
 */
public class SampleADMMessageHandler extends ADMMessageHandlerBase
{
    /** Tag for logs. */
    private final static String TAG = "ADMSampleIntentBase";

    /**
     * Class constructor.
     */
    public SampleADMMessageHandler()
    {
        super(SampleADMMessageHandler.class.getName());
    }
	
    /**
     * Class constructor, including the className argument.
     * 
     * @param className The name of the class.
     */
    public SampleADMMessageHandler(final String className) 
    {
        super(className);
    }

    /** {@inheritDoc} */
    @Override
    protected void onMessage(final Intent intent) 
    {
        Log.i(TAG, "SampleADMMessageHandler:onMessage");

        /* String to access message field from data JSON. */
        final String msgKey = getString(R.string.json_data_msg_key);

        /* String to access timeStamp field from data JSON. */
        final String timeKey = getString(R.string.json_data_time_key);
        
        /* Intent action that will be triggered in onMessage() callback. */
        final String intentAction = getString(R.string.intent_msg_action);

        /* Extras that were included in the intent. */
        final Bundle extras = intent.getExtras();

        verifyMD5Checksum(extras);
        
        /* Extract message from the extras in the intent. */
        final String msg = extras.getString(msgKey);
        final String time = extras.getString(timeKey);

        if (msg == null || time == null)
        {
            Log.w(TAG, "SampleADMMessageHandler:onMessage Unable to extract message data." +
                    "Make sure that msgKey and timeKey values match data elements of your JSON message");
        }

        /* Create a notification with message data. */
        /* This is required to test cases where the app or device may be off. */
        ADMHelper.createADMNotification(this, msgKey, timeKey, intentAction, msg, time);

        /* Intent category that will be triggered in onMessage() callback. */
        final String msgCategory = getString(R.string.intent_msg_category);
        
        /* Broadcast an intent to update the app UI with the message. */
        /* The broadcast receiver will only catch this intent if the app is within the onResume state of its lifecycle. */
        /* User will see a notification otherwise. */
        final Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(intentAction);
        broadcastIntent.addCategory(msgCategory);
        broadcastIntent.putExtra(msgKey, msg);
        broadcastIntent.putExtra(timeKey, time);
        this.sendBroadcast(broadcastIntent);

    }

    /**
     * This method verifies the MD5 checksum of the ADM message.
     * 
     * @param extras Extra that was included with the intent.
     */
    private void verifyMD5Checksum(final Bundle extras) 
    {
        /* String to access consolidation key field from data JSON. */
        final String consolidationKey = getString(R.string.json_data_consolidation_key);
        
        final Set<String> extrasKeySet = extras.keySet();
        final Map<String, String> extrasHashMap = new HashMap<String, String>();
        for (String key : extrasKeySet)
        {
            if (!key.equals(ADMConstants.EXTRA_MD5) && !key.equals(consolidationKey))
            {
                extrasHashMap.put(key, extras.getString(key));
            }            
        }
        final String md5 = ADMSampleMD5ChecksumCalculator.calculateChecksum(extrasHashMap);
        Log.i(TAG, "SampleADMMessageHandler:onMessage App md5: " + md5);
        
        /* Extract md5 from the extras in the intent. */
        final String admMd5 = extras.getString(ADMConstants.EXTRA_MD5);
        Log.i(TAG, "SampleADMMessageHandler:onMessage ADM md5: " + admMd5);
        
        /* Data integrity check. */
        if(!admMd5.trim().equals(md5.trim()))
        {
            Log.w(TAG, "SampleADMMessageHandler:onMessage MD5 checksum verification failure. " +
            		"Message received with errors");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onRegistrationError(final String string)
    {
        Log.e(TAG, "SampleADMMessageHandler:onRegistrationError " + string);
    }

    /** {@inheritDoc} */
    @Override
    protected void onRegistered(final String registrationId) 
    {
        Log.i(TAG, "SampleADMMessageHandler:onRegistered");
        Log.i(TAG, registrationId);

        /* Register the app instance's registration ID with your server. */
        MyServerMsgHandler srv = new MyServerMsgHandler();
        srv.registerAppInstance(getApplicationContext(), registrationId);
    }

    /** {@inheritDoc} */
    @Override
    protected void onUnregistered(final String registrationId) 
    {
        Log.i(TAG, "SampleADMMessageHandler:onUnregistered");

        /* Unregister the app instance's registration ID with your server. */
        MyServerMsgHandler srv = new MyServerMsgHandler();
        srv.unregisterAppInstance(getApplicationContext(), registrationId);
    }
}
