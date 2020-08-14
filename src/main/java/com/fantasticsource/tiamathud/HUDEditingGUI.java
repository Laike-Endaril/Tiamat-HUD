package com.fantasticsource.tiamathud;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledBoolean;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.mctools.gui.element.view.GUITabView;
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
        showUnstacked(this);

        editButtonToHUDElement.clear();


        //Background
        root.add(new GUIDarkenedBackground(this));


        //Header
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //Tabview
        GUITabView tabView = new GUITabView(this, 1, 1 - (navbar.y + navbar.height), "Custom HUD", "Vanilla HUD");
        root.add(tabView);


        //Custom HUD Tab
        hudElements = new GUIList(this, true, 0.98, 1)
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
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(this, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, hudElements);
        tabView.tabViews.get(0).addAll(hudElements, scrollbar);

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


        //Vanilla HUD Tab
        GUILabeledBoolean vanillaHP = new GUILabeledBoolean(this, "Health: ", CHUD.renderVanillaHP);
        vanillaHP.input.addClickActions(() -> CHUD.renderVanillaHP = vanillaHP.getValue());

        GUILabeledBoolean vanillaFood = new GUILabeledBoolean(this, "Food: ", CHUD.renderVanillaFood);
        vanillaFood.input.addClickActions(() -> CHUD.renderVanillaFood = vanillaFood.getValue());

        GUILabeledBoolean vanillaBreath = new GUILabeledBoolean(this, "Breath: ", CHUD.renderVanillaBreath);
        vanillaBreath.input.addClickActions(() -> CHUD.renderVanillaBreath = vanillaBreath.getValue());

        GUILabeledBoolean vanillaExp = new GUILabeledBoolean(this, "Experience: ", CHUD.renderVanillaExp);
        vanillaExp.input.addClickActions(() -> CHUD.renderVanillaExp = vanillaExp.getValue());

        GUILabeledBoolean vanillaMountHP = new GUILabeledBoolean(this, "Mount Health: ", CHUD.renderVanillaMountHealth);
        vanillaMountHP.input.addClickActions(() -> CHUD.renderVanillaMountHealth = vanillaMountHP.getValue());

        GUILabeledBoolean vanillaMountCharge = new GUILabeledBoolean(this, "Mount Charge: ", CHUD.renderVanillaMountCharge);
        vanillaMountCharge.input.addClickActions(() -> CHUD.renderVanillaMountCharge = vanillaMountCharge.getValue());

        GUILabeledBoolean vanillaCrosshair = new GUILabeledBoolean(this, "Crosshair: ", CHUD.renderVanillaCrosshair);
        vanillaCrosshair.input.addClickActions(() -> CHUD.renderVanillaCrosshair = vanillaCrosshair.getValue());

        GUILabeledBoolean vanillaArmor = new GUILabeledBoolean(this, "Armor: ", CHUD.renderVanillaArmor);
        vanillaArmor.input.addClickActions(() -> CHUD.renderVanillaArmor = vanillaArmor.getValue());

        GUILabeledBoolean vanillaHotbar = new GUILabeledBoolean(this, "Hotbar: ", CHUD.renderVanillaHotbar);
        vanillaHotbar.input.addClickActions(() -> CHUD.renderVanillaHotbar = vanillaHotbar.getValue());

        GUILabeledBoolean vanillaPotionEffects = new GUILabeledBoolean(this, "Potion Effects: ", CHUD.renderVanillaPotionEffects);
        vanillaPotionEffects.input.addClickActions(() -> CHUD.renderVanillaPotionEffects = vanillaPotionEffects.getValue());

        GUILabeledBoolean vanillaNotificationText = new GUILabeledBoolean(this, "Notification Text: ", CHUD.renderVanillaNotificationText);
        vanillaNotificationText.input.addClickActions(() -> CHUD.renderVanillaNotificationText = vanillaNotificationText.getValue());

        GUILabeledBoolean vanillaChat = new GUILabeledBoolean(this, "Chat: ", CHUD.renderVanillaChat);
        vanillaChat.input.addClickActions(() -> CHUD.renderVanillaChat = vanillaChat.getValue());

        GUILabeledBoolean vanillaBossInfo = new GUILabeledBoolean(this, "Boss Info: ", CHUD.renderVanillaBossInfo);
        vanillaBossInfo.input.addClickActions(() -> CHUD.renderVanillaBossInfo = vanillaBossInfo.getValue());

        GUILabeledBoolean vanillaSubtitles = new GUILabeledBoolean(this, "Subtitles: ", CHUD.renderVanillaSubtitles);
        vanillaSubtitles.input.addClickActions(() -> CHUD.renderVanillaSubtitles = vanillaSubtitles.getValue());

        tabView.tabViews.get(1).addAll(
                vanillaHP,
                new GUIElement(this, 1, 0),
                vanillaFood,
                new GUIElement(this, 1, 0),
                vanillaBreath,
                new GUIElement(this, 1, 0),
                vanillaExp,
                new GUIElement(this, 1, 0),
                vanillaMountHP,
                new GUIElement(this, 1, 0),
                vanillaCrosshair,
                new GUIElement(this, 1, 0),
                vanillaArmor,
                new GUIElement(this, 1, 0),
                vanillaHotbar,
                new GUIElement(this, 1, 0),
                vanillaPotionEffects,
                new GUIElement(this, 1, 0),
                vanillaNotificationText,
                new GUIElement(this, 1, 0),
                vanillaChat,
                new GUIElement(this, 1, 0),
                vanillaBossInfo,
                new GUIElement(this, 1, 0),
                vanillaSubtitles
        );


        //Add GUI actions
        navbar.addRecalcActions(() -> tabView.height = 1 - (navbar.y + navbar.height));
        hudElements.addRemoveChildActions(element ->
        {
            if (element instanceof GUIList.Line)
            {
                CHUDElement.HUD_ELEMENTS.remove(editButtonToHUDElement.remove(((GUIList.Line) element).getLineElement(0)));
            }
            return true;
        });

        recalc();
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
