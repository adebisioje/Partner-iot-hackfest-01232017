using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Azure.Devices.Client;
using Microsoft.Azure.Devices;
using Newtonsoft.Json;
using System.Collections;
using System.Timers;
using System.Net.WebSockets;
using Microsoft.Azure.Devices.Shared;


namespace Device2CloudMessaging
{
    public class Program
    {
        static string connectionString = "HostName=Evolve-IOT-HUB-POC.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=ciKPBNtS30j3C17/spYpp2Kt1D3RqNxddGOLY/w4SWA=";
        static string deviceName = "Device2";
       
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
    }
}
