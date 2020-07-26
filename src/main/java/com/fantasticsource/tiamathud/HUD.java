package com.fantasticsource.tiamathud;

import com.fantasticsource.mctools.Render;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HUD
{
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void drawHUD(Render.RenderHUDEvent event)
    {
//        if (mode == 0) return;
//
//        float alpha = (float) clientSettings.hudSettings.mainStyle.stealthGaugeAlpha;
//        if (alpha <= 0) return;
//
//        if (ClientData.stealthLevel == Byte.MIN_VALUE) return;
//
//        float partialTick = mc.getRenderPartialTicks();
//        float stealth = partialTick * (ClientData.stealthLevel - ClientData.prevStealthLevel) + ClientData.prevStealthLevel;
//
//
//        GlStateManager.pushMatrix();
//        ScaledResolution sr = new ScaledResolution(mc);
//        int halfSize = clientSettings.hudSettings.mainStyle.stealthGaugeSize / 2;
//        double x = halfSize + (sr.getScaledWidth() - halfSize * 2) * clientSettings.hudSettings.mainStyle.stealthGaugeX;
//        double y = halfSize + (sr.getScaledHeight() - halfSize * 2) * clientSettings.hudSettings.mainStyle.stealthGaugeY;
//        GlStateManager.translate(x, y, 0);
//
//        if (mode == 1)
//        {
//            float theta = 0.9f * stealth;
//            GlStateManager.rotate(theta, 0, 0, 1);
//
//            //Fill
//            textureManager.bindTexture(STEALTH_GAUGE_TEXTURE);
//            Color c = new Color(Integer.parseInt(clientSettings.hudSettings.mainStyle.stealthGaugeColor, 16), true);
//            GlStateManager.color(c.rf(), c.gf(), c.bf(), alpha);
//
//            GlStateManager.glBegin(GL_QUADS);
//            GlStateManager.glTexCoord2f(STEALTH_GAUGE_UV_HALF_PIXEL, STEALTH_GAUGE_UV_HALF_PIXEL);
//            GlStateManager.glVertex3f(-halfSize, -halfSize, 0);
//            GlStateManager.glTexCoord2f(STEALTH_GAUGE_UV_HALF_PIXEL, 1f - STEALTH_GAUGE_UV_HALF_PIXEL);
//            GlStateManager.glVertex3f(-halfSize, halfSize, 0);
//            GlStateManager.glTexCoord2f(1f - STEALTH_GAUGE_UV_HALF_PIXEL, 1f - STEALTH_GAUGE_UV_HALF_PIXEL);
//            GlStateManager.glVertex3f(halfSize, halfSize, 0);
//            GlStateManager.glTexCoord2f(1f - STEALTH_GAUGE_UV_HALF_PIXEL, STEALTH_GAUGE_UV_HALF_PIXEL);
//            GlStateManager.glVertex3f(halfSize, -halfSize, 0);
//            GlStateManager.glEnd();
//
//            //Rim
//            GlStateManager.rotate(-theta, 0, 0, 1);
//
//            textureManager.bindTexture(STEALTH_GAUGE_RIM_TEXTURE);
//            c = new Color(Integer.parseInt(clientSettings.hudSettings.mainStyle.stealthGaugeRimColor, 16), true);
//            GlStateManager.color(c.rf(), c.gf(), c.bf(), 1);
//
//            GlStateManager.glBegin(GL_QUADS);
//            GlStateManager.glTexCoord2f(STEALTH_GAUGE_RIM_UV_HALF_PIXEL, STEALTH_GAUGE_RIM_UV_HALF_PIXEL);
//            GlStateManager.glVertex3f(-halfSize, -halfSize, 0);
//            GlStateManager.glTexCoord2f(STEALTH_GAUGE_RIM_UV_HALF_PIXEL, 1f - STEALTH_GAUGE_RIM_UV_HALF_PIXEL);
//            GlStateManager.glVertex3f(-halfSize, halfSize, 0);
//            GlStateManager.glTexCoord2f(1f - STEALTH_GAUGE_RIM_UV_HALF_PIXEL, 1f - STEALTH_GAUGE_RIM_UV_HALF_PIXEL);
//            GlStateManager.glVertex3f(halfSize, halfSize, 0);
//            GlStateManager.glTexCoord2f(1f - STEALTH_GAUGE_RIM_UV_HALF_PIXEL, STEALTH_GAUGE_RIM_UV_HALF_PIXEL);
//            GlStateManager.glVertex3f(halfSize, -halfSize, 0);
//            GlStateManager.glEnd();
//
//            //Arrow
//            drawArrow(0, -halfSize, -90, 0.06f, true);
//        }
//        else if (mode == 2)
//        {
//            textureManager.bindTexture(STEALTH_GAUGE_TEXTURE_2);
//            Color c = new Color(Integer.parseInt(clientSettings.hudSettings.mainStyle.stealthGaugeColor, 16), true);
//            GlStateManager.color(c.rf(), c.gf(), c.bf(), alpha);
//
//            int index = Tools.min((100 - (int) stealth) >> 2, 49);
//            int gridX = index % 10;
//            int gridY = index / 10;
//
//            float uvleft = gridX * STEALTH_GAUGE_2_UV_W + STEALTH_GAUGE_2_UV_HALF_PIXEL_W;
//            float uvright = (gridX + 1) * STEALTH_GAUGE_2_UV_W - STEALTH_GAUGE_2_UV_HALF_PIXEL_W;
//            float uvtop = gridY * STEALTH_GAUGE_2_UV_H + STEALTH_GAUGE_2_UV_HALF_PIXEL_H;
//            float uvbottom = (gridY + 1) * STEALTH_GAUGE_2_UV_H - STEALTH_GAUGE_2_UV_HALF_PIXEL_H;
//
//            GlStateManager.glBegin(GL_QUADS);
//            GlStateManager.glTexCoord2f(uvleft, uvtop);
//            GlStateManager.glVertex3f(-halfSize, -halfSize, 0);
//            GlStateManager.glTexCoord2f(uvleft, uvbottom);
//            GlStateManager.glVertex3f(-halfSize, halfSize, 0);
//            GlStateManager.glTexCoord2f(uvright, uvbottom);
//            GlStateManager.glVertex3f(halfSize, halfSize, 0);
//            GlStateManager.glTexCoord2f(uvright, uvtop);
//            GlStateManager.glVertex3f(halfSize, -halfSize, 0);
//            GlStateManager.glEnd();
//        }
//
//        GlStateManager.popMatrix();
    }
}
