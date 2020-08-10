package com.fantasticsource.tiamathud.hudelement;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tiamathud.CHUD;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.Color;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.Display;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.tiamathud.TiamatHUD.MODID;

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


    public int xOffset = 0, yOffset = 50;
    public int xAnchor = X_ANCHOR_CENTER, yAnchor = Y_ANCHOR_TOP;


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
        //TODO Add all default settings here

        CBarElement bar = new CBarElement();
        bar.yAnchor = Y_ANCHOR_BOTTOM;
        bar.yOffset = -98;
        bar.hScale = 2;
        bar.vScale = 2;
        bar.textScale = 2;
        bar.fillColor = Color.RED;
        HUD_ELEMENTS.put(bar, "Health");

        bar = new CBarElement();
        bar.direction = CBarElement.DIRECTION_RIGHT_TO_LEFT;
        bar.yAnchor = Y_ANCHOR_BOTTOM;
        bar.xOffset = -71;
        bar.yOffset = -71;
        bar.current = "food";
        bar.max = "20";
        bar.fillColor = Color.ORANGE;
        HUD_ELEMENTS.put(bar, "Food");

        bar = new CBarElement();
        bar.direction = CBarElement.DIRECTION_RIGHT_TO_LEFT;
        bar.yAnchor = Y_ANCHOR_BOTTOM;
        bar.xOffset = -71;
        bar.yOffset = -53;
        bar.current = "saturation";
        bar.max = "20";
        bar.fillColor = Color.YELLOW;
        HUD_ELEMENTS.put(bar, "Saturation");

        bar = new CBarElement();
        bar.yAnchor = Y_ANCHOR_BOTTOM;
        bar.xOffset = 71;
        bar.yOffset = -71;
        bar.current = "breath";
        bar.max = "300";
        bar.fillColor = Color.AQUA;
        HUD_ELEMENTS.put(bar, "Breath");

        bar = new CBarElement();
        bar.yAnchor = Y_ANCHOR_BOTTOM;
        bar.xOffset = 71;
        bar.yOffset = -53;
        bar.current = "exp";
        bar.max = "1";
        bar.fillColor = Color.GREEN;
        bar.text = "Lvl @level (@percent)";
        HUD_ELEMENTS.put(bar, "Exp");
    }


    public boolean enabled = true;

    public final void tryDraw()
    {
        if (enabled)
        {
            GlStateManager.pushMatrix();
            int displayW = Display.getWidth(), displayH = Display.getHeight();
            int x = xAnchor == X_ANCHOR_LEFT ? 0 : xAnchor == X_ANCHOR_RIGHT ? displayW : displayW >>> 1;
            int y = yAnchor == Y_ANCHOR_TOP ? 0 : yAnchor == Y_ANCHOR_BOTTOM ? displayH : displayH >>> 1;
            GlStateManager.translate(x + xOffset, y + yOffset, 0);


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

        xAnchor = buf.readInt();
        yAnchor = buf.readInt();
        xOffset = buf.readInt();
        yOffset = buf.readInt();

        return this;
    }

    @Override
    public CHUDElement save(OutputStream stream)
    {
        new CBoolean().set(enabled).save(stream);

        new CInt().set(xAnchor).save(stream).set(yAnchor).save(stream).set(xOffset).save(stream).set(yOffset).save(stream);

        return this;
    }

    @Override
    public CHUDElement load(InputStream stream)
    {
        enabled = new CBoolean().load(stream).value;

        CInt ci = new CInt();
        xAnchor = ci.load(stream).value;
        yAnchor = ci.load(stream).value;
        xOffset = ci.load(stream).value;
        yOffset = ci.load(stream).value;

        return this;
    }
}
