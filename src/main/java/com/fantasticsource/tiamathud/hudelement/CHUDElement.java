package com.fantasticsource.tiamathud.hudelement;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamathud.CHUD;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.tiamathud.TiamatHUD.MODID;
import static com.fantasticsource.tiamathud.hudelement.CBarElement.DIRECTION_BOTTOM_TO_TOP;
import static com.fantasticsource.tiamathud.hudelement.CBarElement.DIRECTION_RIGHT_TO_LEFT;

public abstract class CHUDElement extends Component
{
    public static final int
            X_ANCHOR_LEFT = 0,
            X_ANCHOR_CENTER = 1,
            X_ANCHOR_RIGHT = 2;

    public static final LinkedHashMap<String, Integer> X_ANCHORS_S_I = new LinkedHashMap<>();
    public static final LinkedHashMap<Integer, String> X_ANCHORS_I_S = new LinkedHashMap<>();

    static
    {
        X_ANCHORS_S_I.put("Left", X_ANCHOR_LEFT);
        X_ANCHORS_S_I.put("Center", X_ANCHOR_CENTER);
        X_ANCHORS_S_I.put("Right", X_ANCHOR_RIGHT);

        X_ANCHORS_I_S.put(X_ANCHOR_LEFT, "Left");
        X_ANCHORS_I_S.put(X_ANCHOR_CENTER, "Center");
        X_ANCHORS_I_S.put(X_ANCHOR_RIGHT, "Right");
    }

    public static final int
            Y_ANCHOR_TOP = 0,
            Y_ANCHOR_CENTER = 1,
            Y_ANCHOR_BOTTOM = 2;

    public static final LinkedHashMap<String, Integer> Y_ANCHORS_S_I = new LinkedHashMap<>();
    public static final LinkedHashMap<Integer, String> Y_ANCHORS_I_S = new LinkedHashMap<>();

    static
    {
        Y_ANCHORS_S_I.put("Top", Y_ANCHOR_TOP);
        Y_ANCHORS_S_I.put("Center", Y_ANCHOR_CENTER);
        Y_ANCHORS_S_I.put("Bottom", Y_ANCHOR_BOTTOM);

        Y_ANCHORS_I_S.put(Y_ANCHOR_TOP, "Top");
        Y_ANCHORS_I_S.put(Y_ANCHOR_CENTER, "Center");
        Y_ANCHORS_I_S.put(Y_ANCHOR_BOTTOM, "Bottom");
    }

    public static final File FILE = new File(MCTools.getConfigDir() + MODID + File.separator + "elements.dat");

    public static final LinkedHashMap<CHUDElement, String> HUD_ELEMENTS = new LinkedHashMap<>();

    public static final LinkedHashMap<String, Class<? extends CHUDElement>> TYPE_TO_CLASS = new LinkedHashMap<>();
    public static final LinkedHashMap<Class<? extends CHUDElement>, String> CLASS_TO_TYPE = new LinkedHashMap<>();

    static
    {
        TYPE_TO_CLASS.put("Bar", CBarElement.class);

        CLASS_TO_TYPE.put(CBarElement.class, "Bar");
    }


    public boolean enabled = true;
    public boolean useMCGUIScale = true;
    public int xOffset = 0, yOffset = 0;
    public int xAnchor = X_ANCHOR_CENTER, yAnchor = Y_ANCHOR_CENTER;


    public static void saveAll()
    {
        FILE.mkdirs();
        while (FILE.exists()) FILE.delete();

        try
        {
            FileOutputStream stream = new FileOutputStream(FILE);


            new CHUD().save(stream);


            new CInt().set(HUD_ELEMENTS.size()).save(stream);
            CStringUTF8 cs = new CStringUTF8();
            for (Map.Entry<CHUDElement, String> entry : HUD_ELEMENTS.entrySet())
            {
                saveMarked(stream, entry.getKey());
                cs.set(entry.getValue()).save(stream);
            }

            stream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void loadAll()
    {
        HUD_ELEMENTS.clear();
        if (!FILE.exists())
        {
            initDefaults();
            return;
        }


        try
        {
            FileInputStream stream = new FileInputStream(FILE);


            new CHUD().load(stream);


            CStringUTF8 cs = new CStringUTF8();
            for (int i = new CInt().load(stream).value; i > 0; i--)
            {
                HUD_ELEMENTS.put((CHUDElement) loadMarked(stream), cs.load(stream).value);
            }

            stream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void initDefaults()
    {
        CBarElement bar;

        for (int i = 0; i < 30; i++)
        {
            bar = new CBarElement();
            bar.xAnchor = X_ANCHOR_RIGHT;
            bar.yAnchor = Y_ANCHOR_TOP;
            bar.direction = DIRECTION_BOTTOM_TO_TOP;
            bar.setBackRL(new ResourceLocation(MODID, "image/hotbar_slot.png"));
            bar.backColor = new Color(40, 40, 40, 255);
            bar.setFillRL(new ResourceLocation(MODID, "image/hotbar_slot.png"));
            bar.fillColor = Color.GRAY;
            bar.xOffset = -9 - (i % 10) * 18;
            bar.yOffset = 9 + (i / 10) * 18;
            bar.potionEffect = i + 1;
            bar.text = "";
            bar.textScale = 0.5;
            HUD_ELEMENTS.put(bar, "Potion Effect Slot " + (i + 1));
        }

        for (int i = 0; i < 9; i++)
        {
            bar = new CBarElement();
            bar.yAnchor = Y_ANCHOR_BOTTOM;
            bar.direction = DIRECTION_BOTTOM_TO_TOP;
            bar.setBackRL(new ResourceLocation(MODID, "image/hotbar_slot.png"));
            bar.backColor = new Color(40, 40, 40, 255);
            bar.setFillRL(new ResourceLocation(MODID, "image/hotbar_slot.png"));
            bar.fillColor = Color.GRAY;
            bar.xOffset = -72 + i * 18;
            bar.yOffset = -9;
            bar.hotbarItem = i + 1;
            bar.text = "";
            HUD_ELEMENTS.put(bar, "Hotbar Slot " + (i + 1));
        }

        bar = new CBarElement();
        bar.hideIfEmpty = true;
        bar.yAnchor = Y_ANCHOR_BOTTOM;
        bar.setBackRL(new ResourceLocation(MODID, "image/2x2.png"));
        bar.setFillRL(new ResourceLocation(MODID, "image/2x2.png"));
        bar.yOffset = -35;
        bar.hScale = 45;
        bar.current = "mountcharge";
        bar.max = "1";
        bar.backColor = Color.BLACK.copy().setA(100);
        bar.fillColor = Color.WHITE;
        bar.text = "";
        HUD_ELEMENTS.put(bar, "Mount Charge");

        bar = new CBarElement();
        bar.setBackRL(new ResourceLocation(MODID, "image/bar_back_alt.png"));
        bar.yAnchor = Y_ANCHOR_BOTTOM;
        bar.textScale = 0.5;
        bar.xOffset = -24;
        bar.yOffset = -30;
        bar.fillColor = Color.RED;
        HUD_ELEMENTS.put(bar, "Health");

        bar = new CBarElement();
        bar.setBackRL(new ResourceLocation(MODID, "image/bar_back_alt.png"));
        bar.angle = 180;
        bar.direction = DIRECTION_RIGHT_TO_LEFT;
        bar.yAnchor = Y_ANCHOR_BOTTOM;
        bar.textScale = 0.5;
        bar.xOffset = 24;
        bar.yOffset = -30;
        bar.current = "mounthealth";
        bar.max = "mountmaxhealth";
        bar.fillColor = new Color(255, 100, 100);
        HUD_ELEMENTS.put(bar, "Mount Health");

        bar = new CBarElement();
        bar.setBackRL(new ResourceLocation(MODID, "image/bar_back_alt.png"));
        bar.yAnchor = Y_ANCHOR_BOTTOM;
        bar.textScale = 0.5;
        bar.xOffset = -24;
        bar.yOffset = -22;
        bar.current = "food";
        bar.max = "20";
        bar.fillColor = Color.ORANGE;
        HUD_ELEMENTS.put(bar, "Food");

        bar = new CBarElement();
        bar.setBackRL(new ResourceLocation(MODID, "image/bar_back_alt.png"));
        bar.angle = 180;
        bar.direction = DIRECTION_RIGHT_TO_LEFT;
        bar.yAnchor = Y_ANCHOR_BOTTOM;
        bar.textScale = 0.5;
        bar.xOffset = 24;
        bar.yOffset = -22;
        bar.current = "saturation";
        bar.max = "20";
        bar.fillColor = Color.YELLOW;
        HUD_ELEMENTS.put(bar, "Saturation");

        bar = new CBarElement();
        bar.hideIfFull = true;
        bar.yAnchor = Y_ANCHOR_BOTTOM;
        bar.textScale = 0.5;
        bar.xOffset = -69;
        bar.yOffset = -26;
        bar.current = "breath";
        bar.max = "300";
        bar.fillColor = Color.AQUA;
        HUD_ELEMENTS.put(bar, "Breath");

        bar = new CBarElement();
        bar.yAnchor = Y_ANCHOR_BOTTOM;
        bar.textScale = 0.5;
        bar.xOffset = 69;
        bar.yOffset = -26;
        bar.current = "exp";
        bar.max = "1";
        bar.fillColor = Color.GREEN;
        bar.text = "Lvl @level (@percent)";
        HUD_ELEMENTS.put(bar, "Exp");
    }


    public final void tryDraw()
    {
        if (enabled)
        {
            GlStateManager.pushMatrix();
            int displayW = Display.getWidth(), displayH = Display.getHeight();
            int x = xAnchor == X_ANCHOR_LEFT ? 0 : xAnchor == X_ANCHOR_RIGHT ? displayW : displayW >>> 1;
            int y = yAnchor == Y_ANCHOR_TOP ? 0 : yAnchor == Y_ANCHOR_BOTTOM ? displayH : displayH >>> 1;
            int scale = useMCGUIScale ? new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor() : 1;
            GlStateManager.translate(x + xOffset * scale, y + yOffset * scale, 0);
            GlStateManager.scale(scale, scale, 1);


            draw();


            GlStateManager.popMatrix();
        }
    }

    protected abstract void draw();


    public abstract void showEditingGUI(String name);


    @Override
    public CHUDElement write(ByteBuf buf)
    {
        buf.writeBoolean(enabled);
        buf.writeBoolean(useMCGUIScale);

        buf.writeInt(xAnchor);
        buf.writeInt(yAnchor);
        buf.writeInt(xOffset);
        buf.writeInt(yOffset);

        return this;
    }

    @Override
    public CHUDElement read(ByteBuf buf)
    {
        enabled = buf.readBoolean();
        useMCGUIScale = buf.readBoolean();

        xAnchor = buf.readInt();
        yAnchor = buf.readInt();
        xOffset = buf.readInt();
        yOffset = buf.readInt();

        return this;
    }

    @Override
    public CHUDElement save(OutputStream stream)
    {
        new CBoolean().set(enabled).save(stream).set(useMCGUIScale).save(stream);

        new CInt().set(xAnchor).save(stream).set(yAnchor).save(stream).set(xOffset).save(stream).set(yOffset).save(stream);

        return this;
    }

    @Override
    public CHUDElement load(InputStream stream)
    {
        CBoolean cb = new CBoolean();
        enabled = cb.load(stream).value;
        useMCGUIScale = cb.load(stream).value;

        CInt ci = new CInt();
        xAnchor = ci.load(stream).value;
        yAnchor = ci.load(stream).value;
        xOffset = ci.load(stream).value;
        yOffset = ci.load(stream).value;

        return this;
    }
}
