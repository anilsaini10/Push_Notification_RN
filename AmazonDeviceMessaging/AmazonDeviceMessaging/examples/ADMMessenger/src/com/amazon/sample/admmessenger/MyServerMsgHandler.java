/*
 * [MyServerMsgHandler.java]
 *
 * (c) 2019, Amazon.com, Inc. or its affiliates. All Rights Reserved.
 */
package com.amazon.sample.admmessenger;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * MyServerMsgHandler abstracts the actions needed by your app to support ADM with your server.
 * It allows the asynchronous sending of http requests to prevent blocking the main thread.
 *
 * @version Revision: 1, Date: 11/11/2012
 */
public class MyServerMsgHandler 
{
    /** Tag for logs. */
    private final static String TAG = "ADMMessenger";

    /** The server action "/register" sends the app instance's unique registration ID to your server. */
    private final static String REGISTER_ROUTE = "/register";

    /** The server action "/unregister" notifies your server that this app instance is no longer registered with ADM. */
    private final static String UNREGISTER_ROUTE = "/unregister";

    /** 
     * Sends an asynchronous http request to your server, to avoid blocking the main thread.
     * 
     * @param context Your application's current context.
     * @param httpRequest The http request to send.
     */
    private void sendHttpRequest(final Context context, final String httpRequest)
    {
        Log.i(TAG,"Sending http request " + httpRequest);
        new AsyncTask<Void, Void, Void>() 
        {
            /** {@inheritDoc} */
            protected Void doInBackground(final Void ...params)
            {
                HttpURLConnection connection = null;
                try 
                {   
                    /* Connect to your server and send the http request */
                    final URL serverUrl = new URL(httpRequest);
                    connection = (HttpURLConnection)serverUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(0);
                    connection.setReadTimeout(0);
                    connection.connect();
                    
                    /* Read your server's reply */
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    final StringBuilder strBuilder = new StringBuilder();
                    
                    String tempStr = null;
                    while ((tempStr = reader.readLine()) != null)
                    {
                        strBuilder.append(tempStr + '\n');
                    }
                    reader.close();
                    
                    /* Log your server's response  */
                    Log.i(TAG, strBuilder.toString());

                }
                catch (MalformedURLException e) 
                {
                    Log.e(TAG, e.getMessage(), e);
                }
                catch (ProtocolException e) 
                {
                    Log.e(TAG, e.getMessage(), e);
                } 
                catch (IOException e) 
                {
                    Log.e(TAG, e.getMessage(), e);
                }
                finally
                {
                    if (connection != null)
                    {
                        connection.disconnect();
                    }                    
                }

                return null;
            }
        }.execute();
    }

    /** 
     * Sends the app instance's unique registration ID to your server. 
     * 
     * @param context Your application's current context.
     * @param registrationId Your application instance's registration ID.
     */
    public void registerAppInstance(final Context context, final String registrationId)
    {
        Log.i(TAG,"Sending registration id to 3rd party server " + registrationId);

        /* Build the URL to address your server. Values for server_address and server_port must be set correctly in your string.xml file. */
        String URL = context.getString(R.string.server_address) + ":" + context.getString(R.string.server_port);
        Log.i(TAG, URL);

        /* Add the registration ID into the request URL. */
        String fullUrl = URL + REGISTER_ROUTE + "?device="+ registrationId;

        /* Send the registration request asynchronously to prevent blocking the main thread. */
        sendHttpRequest(context, fullUrl);
    }

    /**
     * Notifies your server that this app instance is no longer registered with ADM. 
     *
     * @param context Your application's current context.
     * @param registrationId Your application instance's registration ID.
     */
    public void unregisterAppInstance(final Context context, final String registrationId)
    {
        Log.i(TAG,"Sending unregistration id to 3rd party server " + registrationId);

        /* Build the URL to address your server. Values for server_address and server_port must be set correctly in your string.xml file. */
        String URL = context.getString(R.string.server_address) + ":" + context.getString(R.string.server_port);
        Log.i(TAG, URL);

        /* Add to the request URL the registration ID for the unregistered app instance. */
        String fullUrl = URL + UNREGISTER_ROUTE + "?device="+ registrationId;

        /* Send the unregister request asynchronously to prevent blocking the main thread. */
        sendHttpRequest(context, fullUrl);
    }
}
