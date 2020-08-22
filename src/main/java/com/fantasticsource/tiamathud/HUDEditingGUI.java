package com.fantasticsource.tiamathud;

import com.fantasticsource.mctools.MCTools;
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
import net.minecraft.client.Minecraft;
import net.minecraft.world.GameType;

import java.util.LinkedHashMap;
import java.util.Map;

public class HUDEditingGUI extends GUIScreen
{
    protected GameType gameType;
    protected GUIList hudElementsList;
    protected LinkedHashMap<GUIButton, CHUDElement> editButtonToHUDElement = new LinkedHashMap<>();

    public HUDEditingGUI()
    {
        showUnstacked(this);

        editButtonToHUDElement.clear();


        gameType = MCTools.getGameType(Minecraft.getMinecraft().player);
        CHUD hudGlobals = CHUD.GAMEMODE_HUD_GLOBALS.get(gameType);
        LinkedHashMap<CHUDElement, String> hudElements = CHUDElement.GAMEMODE_HUD_ELEMENTS.get(gameType);


        //Background
        root.add(new GUIDarkenedBackground(this));


        //Header
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //Tabview
        GUITabView tabView = new GUITabView(this, 1, 1 - (navbar.y + navbar.height), "Custom HUD", "Vanilla HUD");
        root.add(tabView);


        //Custom HUD Tab
        hudElementsList = new GUIList(this, true, 0.98, 1)
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
                hudElements.put(hudElement, name.getText());

                return new GUIElement[]{
                        editButton.addClickActions(() -> editButtonToHUDElement.get(editButton).showEditingGUI(name.getText())),
                        new GUIElement(screen, 1, 0),
                        name.addEditActions(() ->
                        {
                            if (name.valid()) hudElements.put(editButtonToHUDElement.get(editButton), name.getText());
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
                                    hudElements.remove(element);
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
                                    hudElements.put(element, name.getText());
                                }
                            });
                        })
                };
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(this, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, hudElementsList);
        tabView.tabViews.get(0).addAll(hudElementsList, scrollbar);

        //Add existing HUD elements
        for (Map.Entry<CHUDElement, String> entry : hudElements.entrySet().toArray(new Map.Entry[0]))
        {
            GUIList.Line line = hudElementsList.addLine();

            hudElements.remove(editButtonToHUDElement.remove(line.getLineElement(0)));

            CHUDElement element = entry.getKey();
            editButtonToHUDElement.put((GUIButton) line.getLineElement(0), element);
            String name = entry.getValue();
            hudElements.put(element, name);
            ((GUILabeledTextInput) line.getLineElement(2)).setText(name);
            ((GUIText) line.getLineElement(5)).setText(CHUDElement.CLASS_TO_TYPE.get(element.getClass()));
        }


        //Vanilla HUD Tab
        GUILabeledBoolean vanillaHP = new GUILabeledBoolean(this, "Health: ", hudGlobals.renderVanillaHP);
        vanillaHP.input.addClickActions(() -> hudGlobals.renderVanillaHP = vanillaHP.getValue());

        GUILabeledBoolean vanillaFood = new GUILabeledBoolean(this, "Food: ", hudGlobals.renderVanillaFood);
        vanillaFood.input.addClickActions(() -> hudGlobals.renderVanillaFood = vanillaFood.getValue());

        GUILabeledBoolean vanillaBreath = new GUILabeledBoolean(this, "Breath: ", hudGlobals.renderVanillaBreath);
        vanillaBreath.input.addClickActions(() -> hudGlobals.renderVanillaBreath = vanillaBreath.getValue());

        GUILabeledBoolean vanillaExp = new GUILabeledBoolean(this, "Experience: ", hudGlobals.renderVanillaExp);
        vanillaExp.input.addClickActions(() -> hudGlobals.renderVanillaExp = vanillaExp.getValue());

        GUILabeledBoolean vanillaMountHP = new GUILabeledBoolean(this, "Mount Health: ", hudGlobals.renderVanillaMountHealth);
        vanillaMountHP.input.addClickActions(() -> hudGlobals.renderVanillaMountHealth = vanillaMountHP.getValue());

        GUILabeledBoolean vanillaMountCharge = new GUILabeledBoolean(this, "Mount Charge: ", hudGlobals.renderVanillaMountCharge);
        vanillaMountCharge.input.addClickActions(() -> hudGlobals.renderVanillaMountCharge = vanillaMountCharge.getValue());

        GUILabeledBoolean vanillaCrosshair = new GUILabeledBoolean(this, "Crosshair: ", hudGlobals.renderVanillaCrosshair);
        vanillaCrosshair.input.addClickActions(() -> hudGlobals.renderVanillaCrosshair = vanillaCrosshair.getValue());

        GUILabeledBoolean vanillaArmor = new GUILabeledBoolean(this, "Armor: ", hudGlobals.renderVanillaArmor);
        vanillaArmor.input.addClickActions(() -> hudGlobals.renderVanillaArmor = vanillaArmor.getValue());

        GUILabeledBoolean vanillaHotbar = new GUILabeledBoolean(this, "Hotbar: ", hudGlobals.renderVanillaHotbar);
        vanillaHotbar.input.addClickActions(() -> hudGlobals.renderVanillaHotbar = vanillaHotbar.getValue());

        GUILabeledBoolean vanillaPotionEffects = new GUILabeledBoolean(this, "Potion Effects: ", hudGlobals.renderVanillaPotionEffects);
        vanillaPotionEffects.input.addClickActions(() -> hudGlobals.renderVanillaPotionEffects = vanillaPotionEffects.getValue());

        GUILabeledBoolean vanillaChat = new GUILabeledBoolean(this, "Chat: ", hudGlobals.renderVanillaChat);
        vanillaChat.input.addClickActions(() -> hudGlobals.renderVanillaChat = vanillaChat.getValue());

        GUILabeledBoolean vanillaBossInfo = new GUILabeledBoolean(this, "Boss Info: ", hudGlobals.renderVanillaBossInfo);
        vanillaBossInfo.input.addClickActions(() -> hudGlobals.renderVanillaBossInfo = vanillaBossInfo.getValue());

        GUILabeledBoolean vanillaSubtitles = new GUILabeledBoolean(this, "Subtitles: ", hudGlobals.renderVanillaSubtitles);
        vanillaSubtitles.input.addClickActions(() -> hudGlobals.renderVanillaSubtitles = vanillaSubtitles.getValue());

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
                vanillaChat,
                new GUIElement(this, 1, 0),
                vanillaBossInfo,
                new GUIElement(this, 1, 0),
                vanillaSubtitles
        );


        //Add GUI actions
        navbar.addRecalcActions(() -> tabView.height = 1 - (navbar.y + navbar.height));
        hudElementsList.addRemoveChildActions(element ->
        {
            if (element instanceof GUIList.Line)
            {
                hudElements.remove(editButtonToHUDElement.remove(((GUIList.Line) element).getLineElement(0)));
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
        CHUDElement.saveGametype(gameType);

        super.onClosed();
    }
}
