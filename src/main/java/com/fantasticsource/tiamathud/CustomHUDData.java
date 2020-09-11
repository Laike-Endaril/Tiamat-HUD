package com.fantasticsource.tiamathud;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.LinkedHashMap;

public class CustomHUDData
{
    public static LinkedHashMap<String, String> DATA = new LinkedHashMap<>();

    @SubscribeEvent
    public static void clear(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        DATA = new LinkedHashMap<>();
    }
}
