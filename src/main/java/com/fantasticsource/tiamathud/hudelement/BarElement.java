package com.fantasticsource.tiamathud.hudelement;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.PNG;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.Display;

import java.util.LinkedHashMap;

import static com.fantasticsource.tiamathud.TiamatHUD.MODID;
import static org.lwjgl.opengl.GL11.GL_QUADS;

public class BarElement extends HUDElement
{
    public static final ResourceLocation
            DEFAULT_BACK_RL = new ResourceLocation(MODID, "image/default_bar_back.png"),
            DEFAULT_FILL_RL = new ResourceLocation(MODID, "image/default_bar_fill.png"),
            DEFAULT_FORE_RL = new ResourceLocation(MODID, "image/default_bar_fore.png");

    public static final PNG
            DEFAULT_BACK_PNG = MCTools.getPNG(DEFAULT_BACK_RL),
            DEFAULT_FILL_PNG = MCTools.getPNG(DEFAULT_FILL_RL),
            DEFAULT_FORE_PNG = MCTools.getPNG(DEFAULT_FORE_RL);

    public static final int
            DIRECTION_LEFT_TO_RIGHT = 0,
            DIRECTION_BOTTOM_TO_TOP = 1,
            DIRECTION_RIGHT_TO_LEFT = 2,
            DIRECTION_TOP_TO_BOTTOM = 3;

    public static final LinkedHashMap<String, Integer> DIRECTIONS = new LinkedHashMap<>();

    static
    {
        DIRECTIONS.put("Left to right", DIRECTION_LEFT_TO_RIGHT);
        DIRECTIONS.put("Bottom to top", DIRECTION_BOTTOM_TO_TOP);
        DIRECTIONS.put("Right to left", DIRECTION_RIGHT_TO_LEFT);
        DIRECTIONS.put("Top to bottom", DIRECTION_TOP_TO_BOTTOM);
    }


    protected boolean error = false;

    public int x = 0, y = 0;
    public double hScale = 1, vScale = 1;
    public int direction = DIRECTION_LEFT_TO_RIGHT;

    protected ResourceLocation backRL = DEFAULT_BACK_RL, fillRL = DEFAULT_FILL_RL, foreRL = DEFAULT_FORE_RL;
    protected PNG backPNG = DEFAULT_BACK_PNG, fillPNG = DEFAULT_FILL_PNG, forePNG = DEFAULT_FORE_PNG;
    public Color backColor = Color.WHITE, fillColor = Color.WHITE, foreColor = Color.WHITE;

    public String centerText = "current", lowEndText = "", highEndText = "";
    public Color textColor = Color.BLACK, textOutlineColor = Color.WHITE;
    public double textScale = 1;

    public String min = "0", current = "health", max = "attribute:generic.maxHealth";


    public ResourceLocation getBackRL()
    {
        return backRL;
    }

    public ResourceLocation getFillRL()
    {
        return fillRL;
    }

    public ResourceLocation getForeRL()
    {
        return foreRL;
    }

    public void setBackRL(ResourceLocation backRL)
    {
        this.backRL = backRL;
        backPNG = MCTools.getPNG(backRL);
    }

    public void setFillRL(ResourceLocation fillRL)
    {
        this.fillRL = fillRL;
        fillPNG = MCTools.getPNG(fillRL);

        error = false;
    }

    public void setForeRL(ResourceLocation foreRL)
    {
        this.foreRL = foreRL;
        forePNG = MCTools.getPNG(foreRL);
    }


    @Override
    protected void draw()
    {
        if (fillPNG == null)
        {
            if (!error)
            {
                System.err.println(TextFormatting.RED + "Bar fill image not found: " + fillRL);
                error = true;
            }

            return;
        }


        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();


        Minecraft mc = Minecraft.getMinecraft();
        TextureManager textureManager = mc.getTextureManager();


        GlStateManager.pushMatrix();
        GlStateManager.translate(x >= 0 ? x : Display.getWidth() + x, y >= 0 ? y : Display.getHeight() + y, 0);
        GlStateManager.scale(hScale, vScale, 1);
        if (direction == DIRECTION_TOP_TO_BOTTOM || direction == DIRECTION_BOTTOM_TO_TOP)
        {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90, 0, 0, 1);
        }


        //Background
        if (backPNG != null && backColor.af() > 0)
        {
            textureManager.bindTexture(backRL);
            GlStateManager.color(backColor.rf(), backColor.gf(), backColor.bf(), backColor.af());

            int halfW = backPNG.getWidth() >>> 1, halfH = backPNG.getHeight() >>> 1;
            GlStateManager.glBegin(GL_QUADS);
            //TODO check this and similar rendering for any issues along borders.  If any show, may need half-pixel UV offsets
            GlStateManager.glTexCoord2f(0, 0);
            GlStateManager.glVertex3f(-halfW, -halfH, 0);
            GlStateManager.glTexCoord2f(0, 1);
            GlStateManager.glVertex3f(-halfW, halfH, 0);
            GlStateManager.glTexCoord2f(1, 1);
            GlStateManager.glVertex3f(halfW, halfH, 0);
            GlStateManager.glTexCoord2f(1, 0);
            GlStateManager.glVertex3f(halfW, -halfH, 0);
            GlStateManager.glEnd();
        }

        //Fill
        if (fillPNG != null && fillColor.af() > 0)
        {
            textureManager.bindTexture(fillRL);
            GlStateManager.color(fillColor.rf(), fillColor.gf(), fillColor.bf(), fillColor.af());

            int halfW = fillPNG.getWidth() >>> 1, halfH = fillPNG.getHeight() >>> 1;
            GlStateManager.glBegin(GL_QUADS);
            GlStateManager.glTexCoord2f(0, 0);
            GlStateManager.glVertex3f(-halfW, -halfH, 0);
            GlStateManager.glTexCoord2f(0, 1);
            GlStateManager.glVertex3f(-halfW, halfH, 0);
            GlStateManager.glTexCoord2f(1, 1);
            GlStateManager.glVertex3f(halfW, halfH, 0);
            GlStateManager.glTexCoord2f(1, 0);
            GlStateManager.glVertex3f(halfW, -halfH, 0);
            GlStateManager.glEnd();
        }

        //Foreground
        if (forePNG != null && foreColor.af() > 0)
        {
            textureManager.bindTexture(foreRL);
            GlStateManager.color(foreColor.rf(), foreColor.gf(), foreColor.bf(), foreColor.af());

            int halfW = forePNG.getWidth() >>> 1, halfH = forePNG.getHeight() >>> 1;
            GlStateManager.glBegin(GL_QUADS);
            GlStateManager.glTexCoord2f(0, 0);
            GlStateManager.glVertex3f(-halfW, -halfH, 0);
            GlStateManager.glTexCoord2f(0, 1);
            GlStateManager.glVertex3f(-halfW, halfH, 0);
            GlStateManager.glTexCoord2f(1, 1);
            GlStateManager.glVertex3f(halfW, halfH, 0);
            GlStateManager.glTexCoord2f(1, 0);
            GlStateManager.glVertex3f(halfW, -halfH, 0);
            GlStateManager.glEnd();
        }

        //Reset rotation if necessary
        if (direction == DIRECTION_TOP_TO_BOTTOM || direction == DIRECTION_BOTTOM_TO_TOP)
        {
            GlStateManager.popMatrix();
        }

        //Text Outline
        if (textOutlineColor.af() > 0)
        {
            //TODO
        }

        //Text
        if (textColor.af() > 0)
        {
            //TODO
        }


        GlStateManager.popMatrix();
    }
}
