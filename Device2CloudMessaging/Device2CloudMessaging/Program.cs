using System;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Azure.Devices.Client;
using Newtonsoft.Json;
using System.Net.WebSockets;

namespace Device2CloudMessaging
{
    public class Program
    {
        static string connectionString = "HostName=adojeiothub.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=83Gfbj8wfjsErg+2Fihv5gLuEFHz4YX8pT0wsoZNGn4=";
        static string deviceName = "mySuperStar";

        static void Main(string[] args)
        {
            MainAsync().Wait();
        }

        static async Task MainAsync()
        {
            Console.WriteLine("Getting device from connection string");
            var client = DeviceClient.CreateFromConnectionString(connectionString, deviceName, Microsoft.Azure.Devices.Client.TransportType.Mqtt);

            while (true)
            {
                CheckMessages(client);
                await Task.Delay(5000);
            }
        }

        // To solve the internet connection issue: We created a CheckMessages Method that continues to check for messages to keep the thread
        // open even after a network connection is lost. You only send a message after you have recieved a message from the cloud to know that
        // the connection is alive and working 
        static async void CheckMessages(DeviceClient client)
        {
            try
            {
                Console.WriteLine("Checking for messages");
                await client.OpenAsync();
                var message = await client.ReceiveAsync(TimeSpan.FromSeconds(1.0));
                if (message == null)
                {
                    Console.WriteLine("No messages");
                }
                else
                {
                    Console.WriteLine("Received Message: {0}", Encoding.ASCII.GetString(message.GetBytes()));
                    SendDeviceToCloudMessagesAsync(client); //only send once you recieve a message from the cloud
                    await client.CompleteAsync(message);

                }
            }
            catch (WebSocketException)
            {
                Console.WriteLine("Disconnected!");
            }
            catch (TimeoutException)
            {
                Console.WriteLine("Message Timeout");
            }

        }

        //Sending sample telemry data 
        private static async void SendDeviceToCloudMessagesAsync(DeviceClient client)
        {
            try
            {

                double avgWindSpeed = 10; // m/s
                Random rand = new Random();
                double currentWindSpeed = avgWindSpeed + rand.NextDouble() * 4 - 2;

                var telemetryDataPoint = new
                {
                    deviceId = "mySuperStar",
                    windSpeed = currentWindSpeed
                };
                var messageString = JsonConvert.SerializeObject(telemetryDataPoint);
                var message = new Message(Encoding.ASCII.GetBytes(messageString));

                await client.SendEventAsync(message);

                Console.WriteLine("{0} > Sending message: {1}", DateTime.Now, messageString);

                Task.Delay(1000).Wait();
                //}
            }
            catch (WebSocketException)
            {
                Console.WriteLine("Disconnected!");
            }
            catch (TimeoutException)
            {
                Console.WriteLine("Message Timeout");
            }
        }

    }
}
