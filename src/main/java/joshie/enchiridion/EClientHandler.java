package joshie.enchiridion;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.gui.book.*;
import joshie.enchiridion.gui.book.buttons.*;
import joshie.enchiridion.gui.book.features.recipe.RecipeHandlerFurnace;
import joshie.enchiridion.gui.book.features.recipe.RecipeHandlerShapedVanilla;
import joshie.enchiridion.gui.book.features.recipe.RecipeHandlerShapelessVanilla;
import joshie.enchiridion.gui.library.GuiLibrary;
import joshie.enchiridion.helpers.DefaultHelper;
import joshie.enchiridion.helpers.EditHelper;
import joshie.enchiridion.items.EItems;
import joshie.enchiridion.items.ItemBook;
import joshie.enchiridion.items.SmartLibrary;
import joshie.enchiridion.lib.EGuis;
import joshie.enchiridion.lib.EInfo;
import joshie.enchiridion.library.LibraryHelper;
import joshie.enchiridion.library.handlers.WritableBookHandler;
import joshie.enchiridion.util.EItemGroup;
import joshie.enchiridion.util.ELocation;
import joshie.enchiridion.util.EResourcePack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class EClientHandler {
    public static KeyMapping libraryKeyBinding;

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        // Register book screen using event system (NeoForge pattern)
        event.register(EGuis.BOOK_CONTAINER.get(),
            (uk.joshiejack.penguinlib.world.inventory.AbstractBookMenu container, Inventory inv, Component text) -> {
                GuiBook gui = new GuiBook(container, inv);

                // Get book and editing state from player's held item
                Player player = inv.player;
                ItemStack held = player.getMainHandItem();
                if (held.getItem() instanceof ItemBook) {
                    IBook book = joshie.enchiridion.data.book.BookRegistry.INSTANCE.getBook(held);
                    if (book != null) {
                        gui.setBook(book, player.isShiftKeyDown());
                    }
                }
                return gui;
            }
        );

        // Register library screen using traditional method
        event.register(EGuis.LIBRARY_CONTAINER.get(), GuiLibrary::new);
    }

    public static void setupClient() {
        // TODO: Resource pack system changed in 1.20.4 - addResourcePack() was removed
        // Minecraft.getInstance().getResourceManager().addResourcePack(EResourcePack.INSTANCE);
        LibraryHelper.resetClient();
        //NeoForge.EVENT_BUS.register(new SmartLibrary());
        EnchiridionAPI.editor = new EditHelper();

        // Register toolbar buttons internally (not via API)
        //Left aligned buttons - auto-generated from ToolbarRegistry
        for (joshie.enchiridion.gui.book.element.ToolbarMetadata metadata : joshie.enchiridion.gui.book.element.ToolbarRegistry.getAll()) {
            ToolbarButtonRegistry.register(new ToolbarButton(metadata));
        }

        //Right aligned
        ToolbarButtonRegistry.register(new ButtonDeletePage());
        ToolbarButtonRegistry.register(new ButtonChangeBackground());
        ToolbarButtonRegistry.register(new ButtonChangeIcon());
        ToolbarButtonRegistry.register(new ButtonToggleGrid());
        ToolbarButtonRegistry.register(new ButtonToggleScrollable());
        ToolbarButtonRegistry.register(new ButtonSaveTemplate());
        ToolbarButtonRegistry.register(new ButtonInsertTemplate());

        //Register Recipe Handlers
        EnchiridionAPI.instance.registerRecipeHandler(new RecipeHandlerShapedVanilla());
        //EnchiridionAPI.instance.registerRecipeHandler(new RecipeHandlerShapedOre());
        EnchiridionAPI.instance.registerRecipeHandler(new RecipeHandlerShapelessVanilla());
        //EnchiridionAPI.instance.registerRecipeHandler(new RecipeHandlerShapelessOre());
        EnchiridionAPI.instance.registerRecipeHandler(new RecipeHandlerFurnace());
        //attemptToRegisterRecipeHandler(RecipeHandlerMTAdvancedShaped.class, "crafttweaker");
        //attemptToRegisterRecipeHandler(RecipeHandlerMTAdvancedShapeless.class, "crafttweaker");

        //Register the Enchiridion Book
        EnchiridionAPI.instance.registerModWithBooks(EInfo.MODID);

        //Register the keybinding
        if (EConfig.SETTINGS.libraryAsHotkey.get()) {
            libraryKeyBinding = new KeyMapping("enchiridion.key.library", GLFW.GLFW_KEY_I, "key.categories.misc");
            // Note: Key mapping registration now happens via RegisterKeyMappingsEvent
        }

        /* Colorize the books */
        /*Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> { //TODO
                ItemStack current = LibraryHelper.getLibraryContents(MCClientHelper.getPlayer()).getCurrentBookItem();
                if (!current.isEmpty()) {
                    return Minecraft.getInstance().getItemColors().getColor(current, tintIndex);
                }
            return -1;
        }, ECommonHandler.LIBRARY);*/
    }

    public static void openGuiBookCreate() {
        Minecraft.getInstance().setScreen(new GuiBookCreate(null, null));
    }

    public static void openWriteableBook(Player player, int slot, InteractionHand hand) {
        if (player instanceof ServerPlayer) {
            Minecraft.getInstance().setScreen(new WritableBookHandler.GuiScreenWritable((ServerPlayer) player, slot, hand));
        }
    }
}