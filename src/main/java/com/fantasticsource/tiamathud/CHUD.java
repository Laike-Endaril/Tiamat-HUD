package com.fantasticsource.tiamathud;

import com.fantasticsource.mctools.Render;
import com.fantasticsource.tiamathud.hudelement.CHUDElement;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStream;
import java.io.OutputStream;

@SideOnly(Side.CLIENT)
public class CHUD extends Component
{
    public static boolean
            renderVanillaHP = false,
            renderVanillaFood = false,
            renderVanillaBreath = false,
            renderVanillaExp = false,
            renderVanillaMountHealth = false,
            renderVanillaMountCharge = false,
            renderVanillaCrosshair = true,
            renderVanillaArmor = false,
            renderVanillaHotbar = false,
            renderVanillaPotionEffects = true,
            renderVanillaChat = true,
            renderVanillaBossInfo = true,
            renderVanillaSubtitles = true;


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void vanillaHUD(RenderGameOverlayEvent event)
    {
        if (!event.isCancelable()) return;

        switch (event.getType())
        {
            case HEALTH:
                if (!renderVanillaHP) event.setCanceled(true);
                return;

            case FOOD:
                if (!renderVanillaFood) event.setCanceled(true);
                return;

            case AIR:
                if (!renderVanillaBreath) event.setCanceled(true);
                return;

            case EXPERIENCE:
                if (!renderVanillaExp) event.setCanceled(true);
                return;

            case HEALTHMOUNT:
                if (!renderVanillaMountHealth) event.setCanceled(true);
                return;

            case JUMPBAR:
                if (!renderVanillaMountCharge) event.setCanceled(true);
                return;

            case CROSSHAIRS:
                if (!renderVanillaCrosshair) event.setCanceled(true);
                return;

            case ARMOR:
                if (!renderVanillaArmor) event.setCanceled(true);
                return;

            case HOTBAR:
                if (!renderVanillaHotbar) event.setCanceled(true);
                return;

            case POTION_ICONS:
                if (!renderVanillaPotionEffects) event.setCanceled(true);
                return;

            case CHAT:
                if (!renderVanillaChat) event.setCanceled(true);
                return;

            case BOSSINFO:
                if (!renderVanillaBossInfo) event.setCanceled(true);
                return;

            case SUBTITLES:
                if (!renderVanillaSubtitles) event.setCanceled(true);
                return;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void drawHUD(Render.RenderHUDEvent event)
    {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);


        try
        {
            event.setScalingMode(Render.SCALING_FULL);
            for (CHUDElement hudElement : CHUDElement.HUD_ELEMENTS.keySet())
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

    @Override
    public CHUD write(ByteBuf buf)
    {
        buf.writeBoolean(renderVanillaHP);
        buf.writeBoolean(renderVanillaFood);
        buf.writeBoolean(renderVanillaBreath);
        buf.writeBoolean(renderVanillaExp);
        buf.writeBoolean(renderVanillaMountHealth);
        buf.writeBoolean(renderVanillaMountCharge);
        buf.writeBoolean(renderVanillaCrosshair);
        buf.writeBoolean(renderVanillaArmor);
        buf.writeBoolean(renderVanillaHotbar);
        buf.writeBoolean(renderVanillaPotionEffects);
        buf.writeBoolean(renderVanillaChat);
        buf.writeBoolean(renderVanillaBossInfo);
        buf.writeBoolean(renderVanillaSubtitles);

        return this;
    }

    @Override
    public CHUD read(ByteBuf buf)
    {
        renderVanillaHP = buf.readBoolean();
        renderVanillaFood = buf.readBoolean();
        renderVanillaBreath = buf.readBoolean();
        renderVanillaExp = buf.readBoolean();
        renderVanillaMountHealth = buf.readBoolean();
        renderVanillaMountCharge = buf.readBoolean();
        renderVanillaCrosshair = buf.readBoolean();
        renderVanillaArmor = buf.readBoolean();
        renderVanillaHotbar = buf.readBoolean();
        renderVanillaPotionEffects = buf.readBoolean();
        renderVanillaChat = buf.readBoolean();
        renderVanillaBossInfo = buf.readBoolean();
        renderVanillaSubtitles = buf.readBoolean();

        return this;
    }

    @Override
    public CHUD save(OutputStream stream)
    {
        CBoolean cb = new CBoolean();

        cb.set(renderVanillaHP).save(stream);
        cb.set(renderVanillaFood).save(stream);
        cb.set(renderVanillaBreath).save(stream);
        cb.set(renderVanillaExp).save(stream);
        cb.set(renderVanillaMountHealth).save(stream);
        cb.set(renderVanillaMountCharge).save(stream);
        cb.set(renderVanillaCrosshair).save(stream);
        cb.set(renderVanillaArmor).save(stream);
        cb.set(renderVanillaHotbar).save(stream);
        cb.set(renderVanillaPotionEffects).save(stream);
        cb.set(renderVanillaChat).save(stream);
        cb.set(renderVanillaBossInfo).save(stream);
        cb.set(renderVanillaSubtitles).save(stream);

        return this;
    }

    @Override
    public CHUD load(InputStream stream)
    {
        CBoolean cb = new CBoolean();

        renderVanillaHP = cb.load(stream).value;
        renderVanillaFood = cb.load(stream).value;
        renderVanillaBreath = cb.load(stream).value;
        renderVanillaExp = cb.load(stream).value;
        renderVanillaMountHealth = cb.load(stream).value;
        renderVanillaMountCharge = cb.load(stream).value;
        renderVanillaCrosshair = cb.load(stream).value;
        renderVanillaArmor = cb.load(stream).value;
        renderVanillaHotbar = cb.load(stream).value;
        renderVanillaPotionEffects = cb.load(stream).value;
        renderVanillaChat = cb.load(stream).value;
        renderVanillaBossInfo = cb.load(stream).value;
        renderVanillaSubtitles = cb.load(stream).value;

        return this;
    }
}
