package com.mycompany.app;

import java.util.*;
import com.microsoft.azure.sdk.iot.service.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.io.IOException;
import java.net.URISyntaxException;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceMethod;
import com.microsoft.azure.sdk.iot.service.devicetwin.Pair;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Hello world!
 *
 */
public class App 
{
 

 private static final String connectionString = "HostName=adojeiothub.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=83Gfbj8wfjsErg+2Fihv5gLuEFHz4YX8pT0wsoZNGn4=";
 private static final String deviceId = "mySuperStar";
 private static final IotHubServiceClientProtocol protocol = IotHubServiceClientProtocol.AMQPS;

     public static void main(String[] args) throws Exception

    {
        System.out.println("Starting sample...");
        DeviceTwin twinClient = DeviceTwin.createFromConnectionString(connectionString);
        DeviceTwinDevice device = new DeviceTwinDevice(deviceId);
        DeviceMethod deviceMethod = DeviceMethod.createFromConnectionString(connectionString);
        try
        {
            // Manage complete twin
            System.out.println("Getting the state of device");
            twinClient.getTwin(device);
            System.out.println(device);

            
            //set the desired state of the bathroom light 
            Set<Pair> desiredProperties = new HashSet<Pair>();
            desiredProperties.add(new Pair("bathroom-light", "ON"));
            device.setDesiredProperties(desiredProperties);


              for (Pair desired_state : device.getDesiredProperties()) {
                    for(Pair reported_state: device.getReportedProperties()){
                        if(desired_state.getKey().equals(reported_state.getKey())){
                            System.out.println("This is the property: " + desired_state.getKey());
                            // check to see if desired state is the reported state 
                            if(!(desired_state.getValue().equals(reported_state.getValue()))){
                                 System.out.println("The desired state is " + desired_state.getValue() + ", but the reported state is " +  reported_state.getValue());
                                 //turn the light on or off based off the desired state
                                 if(desired_state.getValue().equals("ON"))
                                 {
                                     System.out.println("Attempting to turn bathroom light on");
                                     deviceMethod.invoke(deviceId,"bathroom-light_on",30L,30L,"null");
                                }else{
                                     System.out.println("Attempting to turn bathroom light off");
                                     deviceMethod.invoke(deviceId,"bathroom-light_off",30L,30L,"null");
                                }
                            }else{
                                System.out.println("The desired and reported state are the same.");
                            }
                        }
                            
                    }

              }

        }
        catch (IotHubException e)
        {
            System.out.println(e.getMessage());
        }
        System.out.println("Shutting down...");
    }
}
