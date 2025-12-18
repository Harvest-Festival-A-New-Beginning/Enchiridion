package joshie.enchiridion;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.data.book.Page;
import joshie.enchiridion.data.book.Template;
import joshie.enchiridion.gui.book.*;
import joshie.enchiridion.gui.book.buttons.*;
import joshie.enchiridion.gui.book.buttons.actions.*;
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
                // Set EnchiridionAPI.book first before calling setBook()
                EnchiridionAPI.book = gui;
                EnchiridionAPI.draw = gui;

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

        // Note: Overlays and buttons are now registered in GuiBook constructor
        // The toolbar buttons will be registered via the API to a list that GuiBook reads

        //Left aligned buttons
        EnchiridionAPI.instance.registerToolbarButton(new ButtonInsertText());
        EnchiridionAPI.instance.registerToolbarButton(new ButtonInsertImage());
        EnchiridionAPI.instance.registerToolbarButton(new ButtonInsertButton());
        EnchiridionAPI.instance.registerToolbarButton(new ButtonInsertBox());
        EnchiridionAPI.instance.registerToolbarButton(new ButtonInsertItem());
        EnchiridionAPI.instance.registerToolbarButton(new ButtonInsertRecipe());
        EnchiridionAPI.instance.registerToolbarButton(new ButtonInsertPreviewWindow());

        //Right aligned
        EnchiridionAPI.instance.registerToolbarButton(new ButtonDeletePage());
        EnchiridionAPI.instance.registerToolbarButton(new ButtonChangeBackground());
        EnchiridionAPI.instance.registerToolbarButton(new ButtonChangeIcon());
        EnchiridionAPI.instance.registerToolbarButton(new ButtonToggleGrid());
        EnchiridionAPI.instance.registerToolbarButton(new ButtonToggleScrollable());
        EnchiridionAPI.instance.registerToolbarButton(new ButtonSaveTemplate());
        EnchiridionAPI.instance.registerToolbarButton(new ButtonInsertTemplate());

        //Register button actions
        EnchiridionAPI.instance.registerButtonAction(new ActionJumpPage());
        EnchiridionAPI.instance.registerButtonAction(new ActionNextPage());
        EnchiridionAPI.instance.registerButtonAction(new ActionPreviousPage());
        EnchiridionAPI.instance.registerButtonAction(new ActionOpenWebpage());
        EnchiridionAPI.instance.registerButtonAction(new ActionToggleLayer());
        EnchiridionAPI.instance.registerButtonAction(new ActionExecuteCommand());

        //Register Recipe Handlers
        EnchiridionAPI.instance.registerRecipeHandler(new RecipeHandlerShapedVanilla());
        //EnchiridionAPI.instance.registerRecipeHandler(new RecipeHandlerShapedOre());
        EnchiridionAPI.instance.registerRecipeHandler(new RecipeHandlerShapelessVanilla());
        //EnchiridionAPI.instance.registerRecipeHandler(new RecipeHandlerShapelessOre());
        EnchiridionAPI.instance.registerRecipeHandler(new RecipeHandlerFurnace());
        //attemptToRegisterRecipeHandler(RecipeHandlerMTAdvancedShaped.class, "crafttweaker");
        //attemptToRegisterRecipeHandler(RecipeHandlerMTAdvancedShapeless.class, "crafttweaker");

        //Register Button Template
        Template template = new Template(new ResourceLocation(EInfo.MODID, "enchiridion_default_buttons"), "Turn Page Arrows", new ELocation("default_buttons_thumbnail"), DefaultHelper.addArrows(new Page(0)));
        EnchiridionAPI.instance.registerTemplate(template);

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