package joshie.enchiridion.helpers;


import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.server.ServerLifecycleHooks;

public class MCServerHelper {

    public static String getHostName() {
        String hostname = FMLEnvironment.dist.isDedicatedServer() ? ServerLifecycleHooks.getCurrentServer().getServerHostname() : "ssp";
        if (hostname.equals("")) hostname = "smp";
        return hostname;
    }
}