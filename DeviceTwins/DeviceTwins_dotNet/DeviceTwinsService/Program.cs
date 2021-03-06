﻿using System;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.Azure.Devices;
using System.Configuration;



namespace DeviceTwinsService
{
    class Program
    {
        static RegistryManager registryManager;
        static string connectionString = ConfigurationManager.AppSettings["connectionString"];
        static string deviceName = ConfigurationManager.AppSettings["deviceName"];

        public static async Task AddTagsAndQuery()
        {
            var twin = await registryManager.GetTwinAsync(deviceName);

            var query = registryManager.CreateQuery("SELECT * FROM devices WHERE properties.reported.bathroom-light = true", 100);
            var twinsDevices43 = await query.GetNextAsTwinAsync();
            Console.WriteLine("Devices Bathroom Properties that are true: {0}", string.Join(", ", twinsDevices43.Select(t => t.DeviceId)));

        }

        

        static void Main(string[] args)
        {

            registryManager = RegistryManager.CreateFromConnectionString(connectionString);
            while (true)
            {
               AddTagsAndQuery().Wait();
               System.Threading.Thread.Sleep(50000);

            }
           Console.WriteLine("Press Enter to exit.");
           Console.ReadLine();

        }
    }
}

