package com.fantasticsource.tiamathud.hudelement;

import java.util.LinkedHashMap;

public abstract class HUDElement
{
    public static final int TYPE_BAR = 0;

    public static final LinkedHashMap<String, Integer> TYPES = new LinkedHashMap<>();

    static
    {
        TYPES.put("Bar", TYPE_BAR);
    }

    public boolean enabled = true;

    public final void tryDraw()
    {
        if (enabled) draw();
    }

    protected abstract void draw();
}
