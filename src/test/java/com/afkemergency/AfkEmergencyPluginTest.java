package com.afkemergency;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class AfkEmergencyPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(AfkEmergencyPlugin.class);
        RuneLite.main(args);
    }
}
