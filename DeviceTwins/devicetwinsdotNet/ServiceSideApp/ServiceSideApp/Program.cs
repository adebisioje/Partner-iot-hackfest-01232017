using System;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Azure.Devices;

namespace ServiceSideApp
{
    class Program
    {
        static RegistryManager registryManager;
        static string connectionString = "HostName=Evolve-IOT-HUB-POC.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=ciKPBNtS30j3C17/spYpp2Kt1D3RqNxddGOLY/w4SWA=";

        public static async Task AddTagsAndQuery()
        {
            var twin = await registryManager.GetTwinAsync("MySuperCoolDevice");

            var query = registryManager.CreateQuery("SELECT * FROM devices WHERE properties.reported.bathroom-light = true", 100);
            var twinsDevices43 = await query.GetNextAsTwinAsync();
            Console.WriteLine("Devices Bathroom Properties that are true: {0}", string.Join(", ", twinsDevices43.Select(t => t.DeviceId)));

        }

        static void Main(string[] args)
        {
            registryManager = RegistryManager.CreateFromConnectionString(connectionString);
            AddTagsAndQuery().Wait();
            Console.WriteLine("Press Enter to exit.");
            Console.ReadLine();

        }
    }
}

