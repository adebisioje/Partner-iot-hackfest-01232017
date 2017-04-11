 'use strict';
 var Client = require('azure-iot-device').Client;
 var Protocol = require('azure-iot-device-mqtt').Mqtt;

 var connectionString = 'HostName=adojeiothub.azure-devices.net;DeviceId=mySuperStar;SharedAccessKey=EzdMDrc9TfQKP22LMC8mhg==';
 var client = Client.fromConnectionString(connectionString, Protocol);

 client.open(function(err) {
 if (err) {
     console.error('could not open IotHub client' + err);
 }  else {
     console.log('client opened');

     client.getTwin(function(err, twin) {
     if (err) {
         console.log(err.message);
         console.error('could not get twin' + err.message);
        
     } else {
      
         var patch = {
            connectivity: {
                 type: 'cellular'
         }

         };

         twin.properties.reported.update(patch, function(err) {
             if (err) {
                 console.error('could not update twin');
             } else {
                 console.log('twin state reported');
                 process.exit();
             }
         });
     }
     });
 }
 });
