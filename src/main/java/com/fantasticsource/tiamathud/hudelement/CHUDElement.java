package com.fantasticsource.tiamathud.hudelement;

import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;

public abstract class CHUDElement extends Component
{
    public static final LinkedHashMap<String, Class<? extends CHUDElement>> TYPES = new LinkedHashMap<>();

    static
    {
        TYPES.put("Bar", CBarElement.class);
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
