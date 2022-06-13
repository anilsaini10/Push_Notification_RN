/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, { useEffect } from 'react';

import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  View,
  Button
} from 'react-native';

import PushNotificationIOS from "@react-native-community/push-notification-ios";
import PushNotification from "react-native-push-notification";
import md5 from "react-native-md5";
import { sha256 } from 'react-native-sha256';
var AWS = require('aws-sdk');
AWS.config.update({region:'us-east-1'});

// hex_md5: 8c50795fee578cad933eac0d64eb7a0e

const App = () => {

  const NotificationHandler = () => {
    // console.log("Working")
    // PushNotification.localNotificationSchedule({

    //   //... You can use all the options from localNotifications
    //   message: "This is test notification", // (required)
    //   date: new Date(Date.now()), // in 60 secs
    //   allowWhileIdle: false, // (optional) set notification to work while on doze, default: false

    //   /* Android Only Properties */
    //   repeatTime: 1, // (optional) Increment of configured repeatType. Check 'Repeating Notifications' section for more info.
    // });



  }

  const textPush = () => {

    var ID;
    PushNotification.getChannels(function (channel_ids) {
      console.log(channel_ids); // ['channel_id_1']
      ID = channel_ids;
    });

    PushNotification.createChannel(
      {
        channelId: "2", // (required)
        channelName: "My channel", // (required)
        channelDescription: "A channel to categorise your notifications", // (optional) default: undefined.
        playSound: false, // (optional) default: true
        soundName: "default", // (optional) See `soundName` parameter of `localNotification` function
        importance: 4, // (optional) default: 4. Int value of the Android notification importance
        vibrate: true, // (optional) default: true. Creates the default vibration patten if true.
      },
      (created) => console.log(`createChannel returned '${created}'`) // (optional) callback returns whether the channel was created, false means it already existed.
    );

    PushNotification.localNotification({

      title: "My Notification Title", // (optional)
      message: "My Notification Message", // (required)
      channelId: "2",
      color: "red",
    });


    // aws-sdk

    var params = {
      Message: "req.query.message",
      PhoneNumber: '+' + '919116599001',
      MessageAttributes: {
        'AWS.SNS.SMS.SenderID': {
          'DataType': 'String',
          'StringValue': "BAAZ"
        }
      }
    };

    var publishTextPromise = new AWS.SNS({ apiVersion: '2010-03-31' }).publish(params).promise();

    publishTextPromise.then(
      function (data) {
        console.log("data");
      }).catch(
        function (err) {
          console.log(err);
        });
  }

  useEffect(() => {
    
    // let hex_md5v = md5.hex_md5( Date.now() +"" );
    // console.log(">>>>hex_md5:", hex_md5v);

    // let b64_md5v = md5.b64_md5( Date.now() +"" );
    // console.log(">>>>b64_md5:", b64_md5v);

    // let str_md5v = md5.str_md5( Date.now() +"" );
    // console.log(">>>>str_md5:", str_md5v);

    // let hex_hmac_md5v = md5.hex_hmac_md5("my_key", Date.now() +"" );
    // console.log(">>>>hex_hmac_md5:", hex_hmac_md5v);

    // let b64_hmac_md5v = md5.b64_hmac_md5("my_key", Date.now() +"" );
    // console.log(">>>>b64_hmac_md5:", b64_hmac_md5v);

    // let str_hmac_md5v = md5.str_hmac_md5("my_key", Date.now() +"" );
    // console.log(">>>>str_hmac_md5:", str_hmac_md5v);
 
    sha256("Test").then( hash => {
      console.log("SHA-256 =>",hash);
  })
 
  }, [])


  return (
    <View style={{ flex: 1, justifyContent: 'center' }}>
      <View style={{ width: '50%', alignSelf: 'center' }}>

        <Button title='Push Notification' onPress={() => { textPush() }} />
      </View>
    </View>
  );
};

export default App;
