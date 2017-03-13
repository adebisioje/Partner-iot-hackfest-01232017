using System;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.Azure.Devices;



namespace DeviceTwinsService
{
    class Program
    {
        static RegistryManager registryManager;
        static string connectionString = "HostName=adojeiothub.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=83Gfbj8wfjsErg+2Fihv5gLuEFHz4YX8pT0wsoZNGn4=";
        static string deviceName = "mySuperStar";

        public static async Task AddTagsAndQuery()
        {
            var twin = await registryManager.GetTwinAsync(deviceName);

            var query = registryManager.CreateQuery("SELECT * FROM devices WHERE properties.reported.bathroom-light = true", 100);
            var twinsDevices43 = await query.GetNextAsTwinAsync();
           //  foreach (var x in twinsDevices43)
           // {
             //do work on twin object
            // Console.WriteLine("Devices Bathroom reported Properties :" + x.Properties.Reported.GetLastUpdated());
             //}
            Console.WriteLine("Devices Bathroom Properties that are true: {0}", string.Join(", ", twinsDevices43.Select(t => t.DeviceId)));

        }

        

        static void Main(string[] args)
        {

            registryManager = RegistryManager.CreateFromConnectionString(connectionString);
            while (true)
            {
               AddTagsAndQuery().Wait();
               System.Threading.Thread.Sleep(5000);

            }
           Console.WriteLine("Press Enter to exit.");
           Console.ReadLine();

        }
    }
}

