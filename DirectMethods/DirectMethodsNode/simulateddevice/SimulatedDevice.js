 'use strict';

 var Mqtt = require('azure-iot-device-mqtt').Mqtt;
 var DeviceClient = require('azure-iot-device').Client;

 var connectionString = "HostName=adojeiothub.azure-devices.net;DeviceId=mySuperStar;SharedAccessKey=EzdMDrc9TfQKP22LMC8mhg==";
 var client = DeviceClient.fromConnectionString(connectionString, Mqtt);


 function onWriteLine(request, response) {
     console.log(request.payload);

     response.send(200, 'Input was written to log.', function(err) {
         if(err) {
             console.error('An error ocurred when sending a method response:\n' + err.toString());
         } else {
             console.log('Response to method \'' + request.methodName + '\' sent successfully.' );
         }
     });
 }

  client.open(function(err) {
     if (err) {
         console.error('could not open IotHub client');
     }  else {
         console.log('client opened');
         client.onDeviceMethod('writeLine', onWriteLine);
     }
 });

