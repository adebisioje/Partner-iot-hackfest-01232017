using System;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Azure.Devices.Client;
using Microsoft.Azure.Devices.Shared;
using Newtonsoft.Json;

namespace DeviceTwins_dotNet
{
    class Program
    {

        static string connectionString = "HostName=adojeiothub.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=83Gfbj8wfjsErg+2Fihv5gLuEFHz4YX8pT0wsoZNGn4=";
        static string deviceName = "mySuperStar";
        static bool occupied = false;
        static DeviceClient client = null;


        //once the desired property changes, this method is invoked 
        private static async Task OnDesiredPropertyChanged(TwinCollection desiredProperties, object userContext)
        {
            Console.WriteLine("desired property change:");
            Console.WriteLine(JsonConvert.SerializeObject(desiredProperties));

            Console.WriteLine("Sending the bathroom light reported property");
            TwinCollection reportedProperties = new TwinCollection();
            
            //set the bathroom light to the desired bathroom light 
            reportedProperties["bathroom-light"] = desiredProperties["bathroom-light"];
            Console.WriteLine("This is the desired bathroom-light property: " + desiredProperties["bathroom-light"]);

            await client.UpdateReportedPropertiesAsync(reportedProperties);
        }

        static void Main(string[] args)
        {
            Console.WriteLine("Getting device from connection string");
            client = DeviceClient.CreateFromConnectionString(connectionString, deviceName, Microsoft.Azure.Devices.Client.TransportType.Mqtt);

            client.SetDesiredPropertyUpdateCallback(OnDesiredPropertyChanged, null).Wait();

            Console.WriteLine("Retrieving twin");
            var twinTask = client.GetTwinAsync();
            twinTask.Wait();
            var twin = twinTask.Result;

            Console.WriteLine("initial twin value received:");
            Console.WriteLine(JsonConvert.SerializeObject(twin));

            while (true)
            {
                UpdateLightState().Wait();
                System.Threading.Thread.Sleep(5000);
                //Wait(5000);
            }

           // Console.ReadLine();
        }


        static async Task UpdateLightState()
        {
            var twinTask = client.GetTwinAsync();
            twinTask.Wait();
            var twin = twinTask.Result;

            TwinCollection reportedProperties = new TwinCollection();
            var lightState = false;

            if (twin.Properties.Reported.Contains("bathroom-light"))
            {
                lightState = !(bool)twin.Properties.Reported["bathroom-light"];
            }

            reportedProperties["bathroom-light"] = lightState;

            Console.WriteLine("Setting Light State: {0}", reportedProperties["bathroom-light"]);
            await client.UpdateReportedPropertiesAsync(reportedProperties);
        }
    }
}

