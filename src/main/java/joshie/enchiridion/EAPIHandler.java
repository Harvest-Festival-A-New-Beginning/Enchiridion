package joshie.enchiridion;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.IEnchiridionAPI;
import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.api.book.IButtonAction;
import joshie.enchiridion.api.book.ITemplate;
import joshie.enchiridion.api.gui.IToolbarButton;
import joshie.enchiridion.gui.book.AbstractGuiOverlay;
import joshie.enchiridion.api.recipe.IRecipeHandler;
import joshie.enchiridion.data.book.BookRegistry;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.gui.book.GuiSimpleEditorButton;
import joshie.enchiridion.gui.book.GuiSimpleEditorTemplate;
import joshie.enchiridion.gui.book.GuiToolbar;
import joshie.enchiridion.gui.book.features.FeatureRecipe;
import joshie.enchiridion.network.PacketHandler;
import joshie.enchiridion.network.packet.PacketOpenBook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EAPIHandler implements IEnchiridionAPI {
    // Lists to store registered items that GuiBook will read when constructed
    private final List<IToolbarButton> toolbarButtons = new ArrayList<>();
    private final List<IButtonAction> buttonActions = new ArrayList<>();
    private final List<ITemplate> templates = new ArrayList<>();
    @Override
    public void registerModWithBooks(String id) {
        /* Grab the modid and the assets path */
        String modid = id;
        String assetsPath = id.toLowerCase();
        if (id.contains(":")) {
            String[] split = id.split(":");
            modid = split[0];
            assetsPath = split[1].toLowerCase();
        }

        /* Find this mods container */
        IModInfo mod = null;
        for (IModInfo info : ModList.get().getMods()) {
            if (info.getModId().equals(modid)) {
                mod = info;
                break;
            }
        }

        /* Attempt to register in dev or in jar */
        if (mod == null) {
            Enchiridion.log(Level.ERROR, "When attempting to register books with Enchiridion a mod with the modid " + modid + " could not be found");
        } else {
            File jar = mod.getOwningFile().getFile().getFilePath().toFile(); //TODO Test
            BookRegistry.INSTANCE.registerMod(assetsPath, jar);
        }
    }

    @Override
    public void registerEditorOverlay(AbstractGuiOverlay overlay) {
        // Editor overlays are now created in GuiBook constructor
        // This method is kept for API compatibility but overlays are built-in
    }

    @Override
    public void registerToolbarButton(IToolbarButton button) {
        toolbarButtons.add(button);
    }

    public List<IToolbarButton> getToolbarButtons() {
        return toolbarButtons;
    }

    @Override
    public void registerRecipeHandler(IRecipeHandler handler) {
        FeatureRecipe.HANDLERS.add(handler);
        Enchiridion.log(Level.INFO, "Registered a new recipe handler: " + handler.getRecipeName());
    }

    @Override
    public void registerButtonAction(IButtonAction action) {
        buttonActions.add(action);
    }

    public List<IButtonAction> getButtonActions() {
        return buttonActions;
    }

    @Override
    public void registerTemplate(ITemplate template) {
        templates.add(template);
    }

    public List<ITemplate> getTemplates() {
        return templates;
    }

    @Override
    public void openBook(Player player, String bookID, int page) {
        // On server side, send packet to client to open the book
        // The packet handler will then use the menu system to open the book
        if (!player.level().isClientSide) {
            // TODO: PacketOpenBook no longer supports page parameter - page navigation will need to be added back
            PacketHandler.sendToClient(new PacketOpenBook(bookID), (ServerPlayer) player);
        }
        // Client-side handling is done via the menu system when packet is received
    }

    @Override
    public IBook getBook(String bookid) {
        return BookRegistry.INSTANCE.getBookByName(bookid);
    }
}