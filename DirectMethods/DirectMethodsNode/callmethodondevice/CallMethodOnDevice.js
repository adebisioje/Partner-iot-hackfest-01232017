 'use strict';

 var Client = require('azure-iothub').Client;


 var connectionString = 'HostName=adojeiothub.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=83Gfbj8wfjsErg+2Fihv5gLuEFHz4YX8pT0wsoZNGn4=';
 var methodName = 'writeLine';
 var deviceId = 'mySuperStar';


 var client = Client.fromConnectionString(connectionString);


 var methodParams = {
     methodName: methodName,
     payload: 'Hello World!',
     timeoutInSeconds: 30
 };

 client.invokeDeviceMethod(deviceId, methodParams, function (err, result) {
     if (err) {
         console.error('Failed to invoke method \'' + methodName + '\': ' + err.message);
     } else {
         console.log(methodName + ' on ' + deviceId + ':');
         console.log(JSON.stringify(result, null, 2));
     }
 });
