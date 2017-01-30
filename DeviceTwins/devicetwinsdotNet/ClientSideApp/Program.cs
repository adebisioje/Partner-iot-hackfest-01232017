using System;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Azure.Devices.Client;
using Microsoft.Azure.Devices.Shared;

namespace POC
{
    public class Program
    {
        //static string connectionString = "HostName=Evolve-IOT-HUB-POC.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=ciKPBNtS30j3C17/spYpp2Kt1D3RqNxddGOLY/w4SWA=";
        static string connectionString = "HostName=iothub-sample-s1.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=07GwWrM/iEySrGwNrMsIlxsXfVowf14EHYGo60V5ekA=";
        static string deviceName = "MySuperCoolDevice";
        static bool occupied = false;
        static DeviceClient client = null;
        static bool skipLight = false;

        static void Main(string[] args)
        {
            MainAsync().Wait();
        }

        static async Task MainAsync()
        {
            Console.WriteLine("Getting device from connection string");
            client = DeviceClient.CreateFromConnectionString(connectionString, deviceName);

            await client.OpenAsync();
            await client.SetDesiredPropertyUpdateCallback(DesiredStateUpdated, null);

            while (true)
            {
                UpdateOccupiedState();
                //UpdateLightState();
                CheckMessages();
                //UpdateTwin();
                await Task.Delay(5000);
            }
        }

        static async Task DesiredStateUpdated(TwinCollection desired, object context)
        {
            if (desired.Contains("bathroom-light"))
            {
                var twin = await client.GetTwinAsync();
                Console.WriteLine("Setting Light State: {0}", desired["bathroom-light"]);
                // Code to set light state
                var props = new TwinCollection();
                props["bathroom-light"] = desired["bathroom-light"];
                await client.UpdateReportedPropertiesAsync(props);
            }
        }

        static async void UpdateOccupiedState()
        {
            occupied = !occupied;
            Console.WriteLine("Setting Occupied State: {0}", occupied);
            var message = new Message(Encoding.ASCII.GetBytes("{\"type\": \"occupiedStatus\", \"value\": " + occupied.ToString().ToLower() + ", \"created\": \"" + DateTime.Now.ToString("yyyy-MM-ddTHH\\:mm\\:ss.fffffffzzz") + "\"}"));
            await client.SendEventAsync(message);
        }

        static async void UpdateLightState()
        {
            skipLight = !skipLight;
            if(skipLight)
            {
                return;
            }

            var twin = await client.GetTwinAsync();

            var props = new TwinCollection();
            var lightState = false;

            if (twin.Properties.Reported.Contains("bathroom-light"))
            {
                lightState = !(bool)twin.Properties.Reported["bathroom-light"];
            }

            props["bathroom-light"] = lightState;

            Console.WriteLine("Setting Light State: {0}", props["bathroom-light"]);
            await client.UpdateReportedPropertiesAsync(props);
        }

        static async void CheckMessages()
        {
            try
            {
                await client.OpenAsync();
                var message = await client.ReceiveAsync(TimeSpan.FromSeconds(1.0));
                if(message != null)
                {
                    Console.WriteLine("Received Message: {0}", Encoding.ASCII.GetString(message.GetBytes()));
                    await client.CompleteAsync(message);
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Message Exception: {0}", e.Message);
            }
        }
    }
}
