package com.fantasticsource.tiamathud;

import com.fantasticsource.tiamathud.hudelement.CHUDElement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = TiamatHUD.MODID, name = TiamatHUD.NAME, version = TiamatHUD.VERSION, dependencies = "required-after:fantasticlib@[1.12.2.036n,)", acceptableRemoteVersions = "*")
public class TiamatHUD
{
    public static final String MODID = "tiamathud";
    public static final String NAME = "Tiamat HUD";
    public static final String VERSION = "1.12.2.000e";

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        Network.init();

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            MinecraftForge.EVENT_BUS.register(CHUD.class);
            MinecraftForge.EVENT_BUS.register(CustomHUDData.class);
            Keys.init(event);
            CHUDElement.loadAll();
        }
    }
}
