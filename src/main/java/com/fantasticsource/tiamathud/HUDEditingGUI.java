package com.fantasticsource.tiamathud;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.mctools.gui.screen.TextSelectionGUI;
import com.fantasticsource.tiamathud.hudelement.CHUDElement;
import com.fantasticsource.tools.datastructures.Color;

import java.util.LinkedHashMap;
import java.util.Map;

public class HUDEditingGUI extends GUIScreen
{
    protected GUIList hudElements;
    protected LinkedHashMap<GUIButton, CHUDElement> editButtonToHUDElement = new LinkedHashMap<>();

    public HUDEditingGUI()
    {
        editButtonToHUDElement.clear();


        //Background
        root.add(new GUIDarkenedBackground(this));


        //Header
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //List of HUD elements
        hudElements = new GUIList(this, true, 0.98, 1 - (navbar.y + navbar.height))
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                GUIButton editButton = GUIButton.newEditButton(screen);
                GUILabeledTextInput name = new GUILabeledTextInput(screen, "Name: ", "HUD Element", FilterNone.INSTANCE);
                GUIText typeLabel = new GUIText(screen, "Type: ", getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
                GUIText type = new GUIText(screen, CHUDElement.TYPE_TO_CLASS.keySet().iterator().next(), getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
                typeLabel.linkMouseActivity(type);
                type.linkMouseActivity(typeLabel);

                CHUDElement hudElement = null;
                try
                {
                    hudElement = CHUDElement.TYPE_TO_CLASS.values().iterator().next().newInstance();
                }
                catch (InstantiationException | IllegalAccessException e)
                {
                    e.printStackTrace();
                    hudElement.toString(); //Purposely crash
                }
                editButtonToHUDElement.put(editButton, hudElement);
                CHUDElement.HUD_ELEMENTS.put(hudElement, name.getText());

                return new GUIElement[]{
                        editButton.addClickActions(() -> editButtonToHUDElement.get(editButton).showEditingGUI(name.getText())),
                        new GUIElement(screen, 1, 0),
                        name.addEditActions(() ->
                        {
                            if (name.valid()) CHUDElement.HUD_ELEMENTS.put(editButtonToHUDElement.get(editButton), name.getText());
                        }),
                        new GUIElement(screen, 1, 0),
                        typeLabel.addClickActions(type::click),
                        type.addClickActions(() ->
                        {
                            String typeName = type.getText();
                            new TextSelectionGUI(type, "Select Type", CHUDElement.TYPE_TO_CLASS.keySet().toArray(new String[0])).addOnClosedActions(() ->
                            {
                                if (!typeName.equals(type.getText()))
                                {
                                    CHUDElement element = editButtonToHUDElement.get(editButton);
                                    CHUDElement.HUD_ELEMENTS.remove(element);
                                    element = null;

                                    try
                                    {
                                        element = CHUDElement.TYPE_TO_CLASS.get(type.getText()).newInstance();
                                    }
                                    catch (InstantiationException | IllegalAccessException e)
                                    {
                                        e.printStackTrace();
                                        element.toString(); //Purposely crash
                                    }
                                    editButtonToHUDElement.put(editButton, element);
                                    CHUDElement.HUD_ELEMENTS.put(element, name.getText());
                                }
                            });
                        })
                };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(this, 0.02, 1 - (navbar.y + navbar.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, hudElements);
        root.addAll(hudElements, scrollbar);

        //Add existing HUD elements
        for (Map.Entry<CHUDElement, String> entry : CHUDElement.HUD_ELEMENTS.entrySet().toArray(new Map.Entry[0]))
        {
            GUIList.Line line = hudElements.addLine();

            CHUDElement.HUD_ELEMENTS.remove(editButtonToHUDElement.remove(line.getLineElement(0)));

            CHUDElement element = entry.getKey();
            editButtonToHUDElement.put((GUIButton) line.getLineElement(0), element);
            String name = entry.getValue();
            CHUDElement.HUD_ELEMENTS.put(element, name);
            ((GUILabeledTextInput) line.getLineElement(2)).setText(name);
            ((GUIText) line.getLineElement(5)).setText(CHUDElement.CLASS_TO_TYPE.get(element.getClass()));
        }

        //Add GUI actions
        navbar.addRecalcActions(() ->
        {
            hudElements.height = 1 - (navbar.y + navbar.height);
            scrollbar.height = 1 - (navbar.y + navbar.height);
        });
        hudElements.addRemoveChildActions(element ->
        {
            if (element instanceof GUIList.Line)
            {
                CHUDElement.HUD_ELEMENTS.remove(editButtonToHUDElement.remove(((GUIList.Line) element).getLineElement(0)));
            }
            return true;
        });
    }

    @Override
    public String title()
    {
        return "HUD Elements";
    }

    @Override
    public void onClosed()
    {
        CHUDElement.saveAll();

        super.onClosed();
    }
}
