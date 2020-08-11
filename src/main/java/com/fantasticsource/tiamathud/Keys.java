package com.fantasticsource.tiamathud;

import com.fantasticsource.mctools.gui.GUIScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import static com.fantasticsource.tiamathud.TiamatHUD.MODID;

@SideOnly(Side.CLIENT)
public class Keys
{
    public static final KeyBinding EDIT_HUD = new KeyBinding(MODID + ".key.editHUD", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NONE, MODID + ".keyCategory");

    public static void init(FMLPreInitializationEvent event)
    {
        ClientRegistry.registerKeyBinding(EDIT_HUD);

        MinecraftForge.EVENT_BUS.register(Keys.class);
    }

    @SubscribeEvent
    public static void keyPress(InputEvent event)
    {
        if (EDIT_HUD.isKeyDown()) new HUDEditingGUI();
    }
}
