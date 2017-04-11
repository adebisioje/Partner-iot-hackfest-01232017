package com.mycompany.app;

import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;



/**

 */
public class App 
{
     private static String connString = "HostName=adojeiothub.azure-devices.net;DeviceId=mySuperStar;SharedAccessKey=EzdMDrc9TfQKP22LMC8mhg==";
     private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
     private static String deviceId = "mySuperStar";
     private static DeviceClient client;

    private static final int METHOD_SUCCESS = 200;
    private static final int METHOD_HUNG = 300;
    private static final int METHOD_NOT_FOUND = 404;
    private static final int METHOD_NOT_DEFINED = 404;
    private static String BATHROOM_LIGHT_STATUS = "OFF";

    

    private static int method_default(Object data)
    {
        System.out.println("default method for this device");
        // Insert device specific code here
        return METHOD_NOT_DEFINED;
    }

      private static int method_turn_bathroom_light_on(Object command) 
    {
       BATHROOM_LIGHT_STATUS = "ON";
       System.out.println("bathroom light turned: " + BATHROOM_LIGHT_STATUS);
        return METHOD_SUCCESS;
    }
          private static int method_turn_bathroom_light_off(Object command)
    {
       BATHROOM_LIGHT_STATUS = "OFF";
        System.out.println("bathroom light turned: " + BATHROOM_LIGHT_STATUS);
        return METHOD_SUCCESS;
    }
       protected static class DeviceTwinStatusCallBack implements IotHubEventCallback
       {
        public void execute(IotHubStatusCode status, Object context)
        {
            System.out.println("IoT Hub responded to device twin operation with status " + status.name());
        }
    }
        protected static class DeviceMethodStatusCallBack implements IotHubEventCallback
    {
        public void execute(IotHubStatusCode status, Object context)
        {
            System.out.println("IoT Hub responded to device method operation with status " + status.name());
        }
    }
     protected static class SampleDeviceMethodCallback implements com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodCallback
    {
        @Override
        public DeviceMethodData call(String methodName, Object methodData, Object context)
        {
            DeviceMethodData deviceMethodData ;
            switch (methodName)
            {
            case "bathroom-light_on":
                {
                    int status = method_turn_bathroom_light_on(methodData);
                    deviceMethodData = new DeviceMethodData(status, "executed " + methodName);
                    break;
                }
                case "bathroom-light_off":
                {
                    int status = method_turn_bathroom_light_off(methodData);
                    deviceMethodData = new DeviceMethodData(status, "executed " + methodName);
                    break;
                }
                default:
                {
                    int status = method_default(methodData);
                    deviceMethodData = new DeviceMethodData(status, "executed " + methodName);
                }
            }

            return deviceMethodData;
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
                System.out.println(propertyKey + " changed to " + propertyValue);
            }

        };
        
        try
        {
            client.open();
            System.out.println("Opened connection to IoT Hub.");
         
        
           client.startDeviceTwin(new DeviceTwinStatusCallBack(), null, homeKit, null);
            System.out.println("Starting to device Twin...");
         
            Property prop =  new Property("bathroom-light", BATHROOM_LIGHT_STATUS);


            
            //desired properties 
             homeKit.setDesiredPropertyCallback(new Property("bathroom-light", null), homeKit, null);

            //set the reported light state
             homeKit.setReportedProp(prop);
            

             client.sendReportedProperties(homeKit.getReportedProp());
             System.out.println("Sending reported properties..." );
           
            client.subscribeToDesiredProperties(homeKit.getDesiredProp());
            System.out.println("Waiting for Desired properties");

            client.subscribeToDeviceMethod(new SampleDeviceMethodCallback(), null, new DeviceMethodStatusCallBack(), null);
            System.out.println("Waiting for a command: ");

            
            while(true){
             prop.setValue(BATHROOM_LIGHT_STATUS);

             //set the reported light state
             homeKit.setReportedProp(prop);
        
             client.sendReportedProperties(homeKit.getReportedProp());
             System.out.println("Sending reported properties..." + BATHROOM_LIGHT_STATUS );
             Thread.sleep(30000);//30 seconds
            }
        
    
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
