package com.fantasticsource.tiamathud;

import com.fantasticsource.mctools.Render;
import com.fantasticsource.tiamathud.hudelement.CHUDElement;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class HUD
{
    public static final ArrayList<CHUDElement> HUD_ELEMENTS = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void drawHUD(Render.RenderHUDEvent event)
    {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);


        try
        {
            event.setScalingMode(Render.SCALING_FULL);
            for (CHUDElement hudElement : HUD_ELEMENTS)
            {
                hudElement.tryDraw();
            }
            event.setScalingMode(Render.SCALING_MC_GUI);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }


        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
    }
}
