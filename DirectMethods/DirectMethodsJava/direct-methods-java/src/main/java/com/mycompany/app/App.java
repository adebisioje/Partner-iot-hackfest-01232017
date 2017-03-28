package com.mycompany.app;
import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
   private static final int METHOD_SUCCESS = 200;
    private static final int METHOD_HUNG = 300;
    private static final int METHOD_NOT_FOUND = 404;
    private static final int METHOD_NOT_DEFINED = 404;

    private static int method_default(Object data)
    {
        System.out.println("default method for this device");
        // Insert device specific code here
        return METHOD_NOT_DEFINED;
    }

    private static int method_writeLine(Object command)
    {
        System.out.println("Hello World! - I am writing a line");
        return METHOD_SUCCESS;
    
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
               case "writeLine" :
                {
                    int status = method_writeLine(methodData);
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

    /**
     * 
     */
  
    public static void main(String[] args)
            throws IOException, URISyntaxException
    {
        System.out.println("Starting...");
        System.out.println("Beginning setup.");


        String connString = "HostName=adojeiothub.azure-devices.net;DeviceId=mySuperStar;SharedAccessKey=EzdMDrc9TfQKP22LMC8mhg==";;

        IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;

        System.out.println("Successfully read input parameters.");
        System.out.format("Using communication protocol %s.\n",
                protocol.name());

        DeviceClient client = new DeviceClient(connString, protocol);

        System.out.println("Successfully created an IoT Hub client.");

        try
        {
            client.open();

            System.out.println("Opened connection to IoT Hub.");

            client.subscribeToDeviceMethod(new SampleDeviceMethodCallback(), null, new DeviceMethodStatusCallBack(), null);

            System.out.println("Subscribed to device method");

            System.out.println("Waiting for method trigger");
        }
        catch (Exception e)
        {
            System.out.println("On exception, shutting down \n" + " Cause: " + e.getCause() + " \n" +  e.getMessage());
            client.close();
            System.out.println("Shutting down...");
        }

        System.out.println("Press any key to exit...");

        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        client.close();
        System.out.println("Shutting down...");

    }
}
