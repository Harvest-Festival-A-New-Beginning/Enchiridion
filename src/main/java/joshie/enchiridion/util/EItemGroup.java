package joshie.enchiridion.util;

import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.lib.EInfo;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class EItemGroup extends CreativeModeTab {
    public static final EItemGroup ENCHIRIDION = new EItemGroup(EInfo.MODID);
    public ItemStack stack = ItemStack.EMPTY;

    public EItemGroup(String label) {
        super(label);
    }

    @Override
    @Nonnull
    public ItemStack makeIcon() {
        return new ItemStack(Items.WRITABLE_BOOK);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    public ItemStack getIconItem() {
        if (this.stack.isEmpty()) return super.getIconItem();
        else return this.stack;
    }

    public void setItemStack(@Nonnull ItemStack stack) {
        this.stack = stack;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    public String getRecipeFolderName() {
        return Enchiridion.format("creative");
    }
}