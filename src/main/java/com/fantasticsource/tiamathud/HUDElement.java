package com.fantasticsource.tiamathud;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;

import java.util.LinkedHashMap;

public class HUDElement extends GUIElement
{
    public static final int TYPE_BAR = 0;

    public static final LinkedHashMap<String, Integer> TYPES = new LinkedHashMap<>();

    static
    {
        TYPES.put("Bar", TYPE_BAR);
    }


    public HUDElement(GUIScreen screen, double width, double height)
    {
        this(screen, width, height, AP_LEFT_TO_RIGHT_TOP_TO_BOTTOM);
    }

    public HUDElement(GUIScreen screen, double width, double height, byte subElementAutoplaceMethod)
    {
        super(screen, width, height, subElementAutoplaceMethod);
    }

    public HUDElement(GUIScreen screen, double x, double y, double width, double height)
    {
        this(screen, x, y, width, height, AP_LEFT_TO_RIGHT_TOP_TO_BOTTOM);
    }

    public HUDElement(GUIScreen screen, double x, double y, double width, double height, byte subElementAutoplaceMethod)
    {
        super(screen, x, y, width, height, subElementAutoplaceMethod);
    }
}
