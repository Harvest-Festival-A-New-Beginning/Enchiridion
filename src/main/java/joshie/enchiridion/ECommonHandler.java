package joshie.enchiridion;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.library.LibraryConditionFactory;
import joshie.enchiridion.library.LibraryRegistry;
import joshie.enchiridion.library.handlers.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.crafting.CraftingHelper;

import static joshie.enchiridion.lib.EInfo.MODID;

public class ECommonHandler {

    public static void init() {
        EnchiridionAPI.instance = new EAPIHandler();
        EnchiridionAPI.library = LibraryRegistry.INSTANCE;
        EnchiridionAPI.library.registerBookHandler(new EnchiridionBookHandler()); //Enchiridion
        EnchiridionAPI.library.registerBookHandler(new WritableBookHandler()); //Writeable Books
        EnchiridionAPI.library.registerBookHandler(new WrittenBookHandler()); //Written Books
        EnchiridionAPI.library.registerBookHandler(new TemporarySwitchHandler()); //Default Handler
        EnchiridionAPI.library.registerBookHandler(new RightClickHandler()); //Kept for backwards compatibility
        EnchiridionAPI.library.registerBookHandler(new CopyNBTHandler()); //Copy NBT Handler
        // TODO: Condition system changed in 1.20.4 - CraftingHelper.register() may need to use RegisterEvent or be removed
        // CraftingHelper.register(new ResourceLocation(MODID, "library_writable_enabled"), new LibraryConditionFactory());
    }
}