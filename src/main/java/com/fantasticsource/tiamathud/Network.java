package com.fantasticsource.tiamathud;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.fantasticsource.tiamathud.TiamatHUD.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(CustomHUDDataPacketHandler.class, CustomHUDDataPacket.class, discriminator++, Side.CLIENT);
    }


    public static class CustomHUDDataPacket implements IMessage
    {
        public String key;
        public double value;

        public CustomHUDDataPacket()
        {
            //Required
        }

        public CustomHUDDataPacket(String key, double value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, key);
            buf.writeDouble(value);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            key = ByteBufUtils.readUTF8String(buf);
            value = buf.readDouble();
        }
    }

    public static class CustomHUDDataPacketHandler implements IMessageHandler<CustomHUDDataPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(CustomHUDDataPacket packet, MessageContext ctx)
        {
            CustomHUDData.DATA.put(packet.key, packet.value);
            return null;
        }
    }
}
