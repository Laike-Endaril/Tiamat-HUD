package com.fantasticsource.tiamathud.hudelement;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.tiamathud.TiamatHUD.MODID;

public abstract class CHUDElement extends Component
{
    public static final File FILE = new File(MCTools.getConfigDir() + MODID + File.separator + "elements.dat");

    public static final LinkedHashMap<CHUDElement, String> HUD_ELEMENTS = new LinkedHashMap<>();

    public static final LinkedHashMap<String, Class<? extends CHUDElement>> TYPES = new LinkedHashMap<>();

    static
    {
        TYPES.put("Bar", CBarElement.class);
    }


    public static void saveAll()
    {
        FILE.mkdirs();
        while (FILE.exists()) FILE.delete();

        try
        {
            FileOutputStream stream = new FileOutputStream(FILE);

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
        if (!FILE.exists()) return;


        try
        {
            FileInputStream stream = new FileInputStream(FILE);

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


    public boolean enabled = true;

    public final void tryDraw()
    {
        if (enabled) draw();
    }

    protected abstract void draw();


    @Override
    public CHUDElement write(ByteBuf buf)
    {
        buf.writeBoolean(enabled);
        return this;
    }

    @Override
    public CHUDElement read(ByteBuf buf)
    {
        enabled = buf.readBoolean();
        return this;
    }

    @Override
    public CHUDElement save(OutputStream stream)
    {
        new CBoolean().set(enabled).save(stream);
        return this;
    }

    @Override
    public CHUDElement load(InputStream stream)
    {
        enabled = new CBoolean().load(stream).value;
        return this;
    }
}
