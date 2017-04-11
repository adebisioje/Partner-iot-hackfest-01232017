package com.mycompany.app;

import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Pair;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Device;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Property;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.PropertyCallBack;


import java.io.IOException;
import java.net.URISyntaxException;
import java.text.CollationElementIterator;
import java.util.HashSet;
import java.util.Scanner;
import java.util.UUID;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



/**

 */
public class App 
{
     private static String connString = "HostName=adojeiothub.azure-devices.net;DeviceId=mySuperStar;SharedAccessKey=EzdMDrc9TfQKP22LMC8mhg==";
     private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
     private static String deviceId = "mySuperStar";
     private static DeviceClient client;
       
       protected static class DeviceTwinStatusCallBack implements IotHubEventCallback{
        public void execute(IotHubStatusCode status, Object context){
            System.out.println("IoT Hub responded to device twin operation with status " + status.name());
        }
    }

    public static void main( String[] args ) throws IOException, URISyntaxException
    {
        client = new DeviceClient(connString, protocol);

        // Check the client 
        if (client == null)
        {
            System.out.println("Could not create an IoT Hub client.");
            return;
        }

        System.out.println("Successfully created an IoT Hub client.");

        Device homeKit = new Device()

        {

            @Override
            public void PropertyCall(String propertyKey, Object propertyValue, Object context)
            {
               //this.setReportedProp(new Property("bathroom-light", propertyValue));
                System.out.println(propertyKey + " changed to " + propertyValue);
            }

        };

        try

        {
            client.open();
            System.out.println("Opened connection to IoT Hub.");
         
            client.startDeviceTwin(new DeviceTwinStatusCallBack(), null, homeKit, null);
            System.out.println("Starting to device Twin...");

            //desired properties 
             homeKit.setDesiredPropertyCallback(new Property("bathroom-light", null), homeKit, null);
            

            //set the reported light state
             homeKit.setReportedProp(new Property("bathroom-light", true));
            

             client.sendReportedProperties(homeKit.getReportedProp());
            //
           //System.out.println("This is the desired property" + homeKit.getDesiredProp().propertyValue);
            client.subscribeToDesiredProperties(homeKit.getDesiredProp());
            System.out.println("Waiting for Desired properties");

            //client.subscribeToDeviceMethod(new SampleDeviceMethodCallback(), null, new DeviceMethodStatusCallBack(), null);
         
        }catch(Exception e){
            System.out.println("On exception, shutting down \n" + " Cause: " + e.getCause() + " \n" +  e.getMessage());
            homeKit.clean();
            client.close();
            System.out.println("Shutting down...");
        }

        System.out.println("Press any key to exit...");
        Scanner scanner = new Scanner(System.in);

        scanner.nextLine();
        homeKit.clean();
        client.close();
         

        System.out.println("Shutting down...");
    }
}
