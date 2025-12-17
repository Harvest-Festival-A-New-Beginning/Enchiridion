package joshie.enchiridion.util;

import joshie.enchiridion.lib.EInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

// TODO: CreativeModeTab registration changed in 1.20.4
// This is a stub for compilation - needs to be registered via CreativeModeTabEvent.Register
public class EItemGroup {
    public static final ResourceKey<CreativeModeTab> ENCHIRIDION_KEY =
        ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(EInfo.MODID, "main"));

    // Stub instance for compatibility - actual tab should be registered in mod init
    public static final EItemGroup ENCHIRIDION = new EItemGroup();

    public ItemStack stack = ItemStack.EMPTY;

    public EItemGroup() {
    }

    public void setItemStack(@Nonnull ItemStack stack) {
        this.stack = stack;
    }

    @Nonnull
    public ItemStack getIconItem() {
        if (this.stack.isEmpty()) return new ItemStack(Items.WRITABLE_BOOK);
        else return this.stack;
    }
}