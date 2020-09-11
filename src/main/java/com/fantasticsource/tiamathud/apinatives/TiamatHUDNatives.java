package com.fantasticsource.tiamathud.apinatives;

import com.fantasticsource.tiamathud.CustomHUDData;
import com.fantasticsource.tiamathud.Network;
import com.fantasticsource.tiamathud.api.ITiamatHUDNatives;
import net.minecraft.entity.player.EntityPlayerMP;

public class TiamatHUDNatives implements ITiamatHUDNatives
{
    public static TiamatHUDNatives NATIVES = new TiamatHUDNatives();

    @Override
    public void sendCustomHUDData(EntityPlayerMP player, String key, double value)
    {
        Network.WRAPPER.sendTo(new Network.CustomHUDDataPacket(key, value), player);
    }

    @Override
    public void sendCustomHUDData(EntityPlayerMP player, String key, String value)
    {
        Network.WRAPPER.sendTo(new Network.CustomHUDDataPacket(key, value), player);
    }


    @Override
    public String getCustomHUDData(String key)
    {
        return CustomHUDData.DATA.get(key);
    }
}
