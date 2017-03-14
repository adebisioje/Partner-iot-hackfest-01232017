# Partner-iot-hackfest-01232017

This folder contains code that was created while hacking with an IOT Partner 

## Problem Statement 
    How might we, using Azure IOT hubs, solve connectivity issues when sending the state of devices to Azure 

## Solution in Code
**DeviceToCloudMessaging** conatins code that waits for a message from Azure( to ensure that there is no connectivity issue) before sending a device state message back to the cloud. When there is an interruption in the network connection, no messages are sent. However, when the device is back "online" it gets back on the same thread, waits to get a message from the cloud and then sends the state of the device. 

**DeviceTwins** - Ideally, device twins should be used to manage the states of devices. However, during the hackfest, device twins was not yet implemented in Java (which is what the Partner really wanted), so we impleneted device twins in .Net and Node to show them the functionality. Once Device Twins came out in Java, I was implemented the functionality in Java.

**iot-java-get-started** - This contains the implementation of getting started on IOT-hub - https://docs.microsoft.com/en-us/azure/iot-hub/iot-hub-how-to. We used this to introduce IOT Hub to the Partner  


