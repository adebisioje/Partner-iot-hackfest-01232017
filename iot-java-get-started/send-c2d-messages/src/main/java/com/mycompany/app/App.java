package com.mycompany.app;

 import com.microsoft.azure.iot.service.sdk.*;
 import java.io.IOException;
 import java.net.URISyntaxException;


/**
 * Hello world!
 *
 */
public class App 
{
 

 private static final String connectionString = "HostName=iothubevolve272f2.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=E0jHdIVmdq8xgAOJkk6ezhf/dNXWhiQHh378/wF+x6M=";
 private static final String deviceId = "ROCM";
 private static final IotHubServiceClientProtocol protocol = IotHubServiceClientProtocol.AMQPS;

     public static void main(String[] args) throws IOException,
     URISyntaxException, Exception {
   ServiceClient serviceClient = ServiceClient.createFromConnectionString(
     connectionString, protocol);

   if (serviceClient != null) {
     serviceClient.open();
     FeedbackReceiver feedbackReceiver = serviceClient
       .getFeedbackReceiver(deviceId);
     if (feedbackReceiver != null) feedbackReceiver.open();

     Message messageToSend = new Message("Ping");
     messageToSend.setDeliveryAcknowledgement(DeliveryAcknowledgement.Full);

     serviceClient.send(deviceId, messageToSend);
     System.out.println("Message sent to device");

     FeedbackBatch feedbackBatch = feedbackReceiver.receive();
     //System.out.println("I am now here");
     /** 
     if (feedbackBatch != null) {
       System.out.println("Message feedback received, feedback time: "
         + feedbackBatch.getEnqueuedTimeUtc().toString());
     }else{
       System.out.println("Oppppps");
     }

     if (feedbackReceiver != null){
        feedbackReceiver.close();
        
     }else{
       System.out.println("No feedback sorry");
  }
  **/
     serviceClient.close();
   }
 }

}
