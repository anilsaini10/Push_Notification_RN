In this document, "your server" refers to the server-side software 
that you must implement to use ADM services.

ADMMessenger is a sample app for Kindle Fire devices that showcases 
the features of ADM. It is meant to be redistributed to 3rd party
developers as a simplistic implementation of ADM.

ADMMessenger consists of two main parts: server and client. 
Python versions of the server implementation are included.

== Python Server ==

Server: A self-contained web sample application written as a Python
script. The web application simulates a range of tasks your server
could implement to send messages to client applications. The server
runs on port 80 by default.

There are two main classes in server.py:
SampleWebApp: handles the logic for interacting with ADM 
services, as well as keeping a list of all devices that have been 
registered with the server.

ServerHandler: handles the minimal tasks required to process incoming
and outgoing web requests and responses. It also generates a very
simple html GUI.

The web application exposes the following routes:
/: displays 'Server running'.
/register: accepts registration IDs from instances of your app and 
registers them with your server.
/unregister: accepts registration IDs from instances of your app and 
unregisters them with your server.
/show-devices: returns html GUI that display all the devices registered 
with your server and that allows you to send custom messages to them. 
/sendmsg: Sends a message to ADM Servers for relaying to an instance of 
your app.

To run the server, perform the following actions: 
1. Change the value of PORT at the beginning of server.py to the port you 
   would like the server to listen on. Make sure this port is opened and
   accessible before proceeding.
2. Change the values of PROD_CLIENT_ID and PROD_CLIENT_SECRET to the ones
   you received from Amazon. These are also located at the beginning of
   server.py
3. Run from the command line:
    > python server.py

== Client ==

Client: An Android app that uses the ADM libraries to handle messages 
your server sends via ADM Servers.

This app will print the messages it receives from the ADM servers. It does 
so in a TextView that exists in its only Activity (MainActivity.java).

To build the client:
You will need to add the ADM library to your project as an external JAR for the client to build properly.
Please refer to the documentation for instructions on how to add ADM to the project as an external JAR.

To set up this sample in Eclipse:
1.  In Eclipse, create a new Android project using the following values:
    - Application Name: "ADMMessenger"
    - Project Name: "ADMMessenger"
    - Package name: "com.amazon.sample.admmessenger"
    - Minimum Required SDK: "API 15: Android 4.0.3 (IceCreamSandwich)" 
    - Target SDK: "API 15: Android 4.0.3 (IceCreamSandwich)"
    - Compile With: "API 15: Android 4.0.3 (IceCreamSandwich)"
    - Theme: "Holo Light with Dark Action Bar"
    Click Next
2.  For each subsequent page in the wizard, click Next to accept the default settings, and then click Finish.
3.  Complete the Android project creation using the default values.
4.  Open the project's properties; for example, right-click the root folder for the project, and then click Properties.
5.  In the Properties window, select Java Build Path from the list of properties. Select the Libraries tab.
6.  Click Add External JARs... and navigate to where you extracted the ADM.zip file.
7.  Open the ADM/lib folder, select the amazon-device-messaging-1.1.0.jar file, and click Open.
8.  You should now see amazon-device-messaging-1.1.0.jar listed in the Properties window. Click OK.
9.  In the Package Explorer for your project, under Referenced Libraries, amazon-device-messaging-1.1.0.jar should now appear.
10.  Copy and paste the contents of ADM/examples/ADMMessenger inside of your project (overwriting the existing files created by Eclipse).
11. To refresh the sources for your project, in Package Explorer, right-click your project, then click Refresh.
12. Open the Eclipse preferences; for example, click on Windows, and then click Preferences.
13. Expand Android and select Build.
14. Click Browse and navigate to the keystore you associated with the sample app. For more information, see https://developer.amazon.com/sdk/adm/credentials.html.
15. Click OK

To run the client in Eclipse:
1. In ADMMessenger/assets/api_key.txt, replace the line of text you see with the API Key you received from Amazon.
2. In ADMMessenger/res/values/strings.xml, change the values of server_address and server_port to values
   that reference your server (see PORT value above).
3. Build, install and run from Eclipse.
