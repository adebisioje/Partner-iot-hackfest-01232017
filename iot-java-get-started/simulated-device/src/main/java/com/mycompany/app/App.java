package com.mycompany.app;
 import com.microsoft.azure.iothub.DeviceClient;
 import com.microsoft.azure.iothub.IotHubClientProtocol;
 import com.microsoft.azure.iothub.Message;
 import com.microsoft.azure.iothub.IotHubStatusCode;
 import com.microsoft.azure.iothub.IotHubEventCallback;
 import com.microsoft.azure.iothub.IotHubMessageResult;
 import com.google.gson.Gson;
 import java.io.IOException;
 import java.net.URISyntaxException;
 import java.util.Random;
 import java.util.concurrent.Executors;
 import java.util.concurrent.ExecutorService;


/**
 * Hello world!
 *
 */
public class App 
{
 private static String connString = "HostName=iothubevolve272f2.azure-devices.net;DeviceId=ROCM;SharedAccessKey=h419/zQX9uIQuAnMSMBY/g==";
 private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
 private static String deviceId = "ROCM";
 private static DeviceClient client;

 private static class TelemetryDataPoint {
   public String deviceId;
   public double windSpeed;

   public String serialize() {
     Gson gson = new Gson();
     return gson.toJson(this);
   }
 }

 private static class EventCallback implements IotHubEventCallback
 {
   public void execute(IotHubStatusCode status, Object context) {
     System.out.println("IoT Hub responded to message with status: " + status.name());

     if (context != null) {
       synchronized (context) {
         context.notify();
       }
     }
   }
 }
private static class MessageCallback implements
 com.microsoft.azure.iothub.MessageCallback {
   public IotHubMessageResult execute(Message msg, Object context) {
     System.out.println("Received message from hub: "
       + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));

     return IotHubMessageResult.COMPLETE;
   }
 }


 private static class MessageSender implements Runnable {
     public volatile boolean stopThread = false;

     public void run()  {
         try {
             double avgWindSpeed = 10; // m/s
             Random rand = new Random();

             while (!stopThread) {
                 double currentWindSpeed = avgWindSpeed + rand.nextDouble() * 4 - 2;
                 TelemetryDataPoint telemetryDataPoint = new TelemetryDataPoint();
                 telemetryDataPoint.deviceId = deviceId;
                 telemetryDataPoint.windSpeed = currentWindSpeed;

                 String msgStr = telemetryDataPoint.serialize();
                 double random = new Random().nextDouble();

                 Message msg; 
                 if (random > 0.7) {
                     msg = new Message("This is a critical message.");
                     msg.setProperty("level", "critical");
                 } else {
                     msg = new Message(msgStr);
                 }

                 System.out.println("Sending: " + msgStr);

                 Object lockobj = new Object();
                 EventCallback callback = new EventCallback();
                 client.sendEventAsync(msg, callback, lockobj);

                 synchronized (lockobj) {
                     lockobj.wait();
                 }
                 Thread.sleep(1000);
             }
         } catch (InterruptedException e) {
             System.out.println("Finished.");
         }
     }
 }


public static void main( String[] args ) throws IOException, URISyntaxException {
  client = new DeviceClient(connString, protocol);
  MessageCallback callback = new MessageCallback();
 client.setMessageCallback(callback, null);
 client.open();
  
  MessageSender sender = new MessageSender();

  ExecutorService executor = Executors.newFixedThreadPool(1);
  executor.execute(sender);


  System.out.println("Press ENTER to exit.");
  System.in.read();
  executor.shutdownNow();

  client.close();




}

}
