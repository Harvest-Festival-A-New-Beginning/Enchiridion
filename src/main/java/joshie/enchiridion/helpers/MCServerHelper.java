package joshie.enchiridion.helpers;


import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class MCServerHelper {

    public static String getHostName() {
        // In 1.20.4, getServerHostname() was removed. Use getLocalIp() instead
        String hostname = FMLEnvironment.dist.isDedicatedServer() ? ServerLifecycleHooks.getCurrentServer().getLocalIp() : "ssp";
        if (hostname == null || hostname.equals("")) hostname = "smp";
        return hostname;
    }
}