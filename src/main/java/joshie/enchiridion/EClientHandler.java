package joshie.enchiridion;

import joshie.enchiridion.api.EnchiridionAPI;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class EClientHandler {
    public static KeyMapping libraryKeyBinding;

    public static void setupClient() {
        Minecraft.getInstance().getResourceManager().addResourcePack(EResourcePack.INSTANCE);
        MenuScreens.register(EGuis.LIBRARY_CONTAINER, GuiLibrary::new);
        LibraryHelper.resetClient();
        NeoForge.EVENT_BUS.register(new SmartLibrary());
        EnchiridionAPI.book = GuiBook.INSTANCE;
        EnchiridionAPI.draw = GuiBook.INSTANCE;
        EnchiridionAPI.editor = new EditHelper();
        //Register editor overlays
        EnchiridionAPI.instance.registerEditorOverlay(GuiGrid.INSTANCE);
        EnchiridionAPI.instance.registerEditorOverlay(GuiTimeLine.INSTANCE);
        EnchiridionAPI.instance.registerEditorOverlay(GuiToolbar.INSTANCE);
        EnchiridionAPI.instance.registerEditorOverlay(GuiLayers.INSTANCE);
        EnchiridionAPI.instance.registerEditorOverlay(GuiSimpleEditor.INSTANCE);

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
        Template template = new Template("enchiridion_default_buttons", "Turn Page Arrows", new ELocation("default_buttons_thumbnail"), DefaultHelper.addArrows(new Page(0)));
        EnchiridionAPI.instance.registerTemplate(template);

        //Register the Enchiridion Book
        EnchiridionAPI.instance.registerModWithBooks(EInfo.MODID);

        //Register the keybinding
        if (EConfig.SETTINGS.libraryAsHotkey.get()) {
            libraryKeyBinding = new KeyMapping("enchiridion.key.library", GLFW.GLFW_KEY_I, "key.categories.misc");
            // Note: Key mapping registration now happens via RegisterKeyMappingsEvent
        }

        ItemStack book = new ItemStack(EItems.BOOK);
        book.setTag(new CompoundTag());
        if (book.getTag() != null) {
            book.getTag().putString("identifier", "enchiridion");
        }
        EItemGroup.ENCHIRIDION.setItemStack(book);

        /* Colorize the books */
        /*Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> { //TODO
                ItemStack current = LibraryHelper.getLibraryContents(MCClientHelper.getPlayer()).getCurrentBookItem();
                if (!current.isEmpty()) {
                    return Minecraft.getInstance().getItemColors().getColor(current, tintIndex);
                }
            return -1;
        }, ECommonHandler.LIBRARY);*/
    }

    public static void openGuiBook() {
        Minecraft.getInstance().setScreen(GuiBook.INSTANCE);
    }

    public static void openGuiBookCreate() {
        Minecraft.getInstance().setScreen(GuiBookCreate.INSTANCE);
    }

    public static void openWriteableBook(Player player, int slot, InteractionHand hand) {
        if (player instanceof ServerPlayer) {
            Minecraft.getInstance().setScreen(new WritableBookHandler.GuiScreenWritable((ServerPlayer) player, slot, hand));
        }
    }
}