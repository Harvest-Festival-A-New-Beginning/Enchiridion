package joshie.enchiridion.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.NonNullList;

import javax.annotation.Nonnull;

public abstract class InventoryStorage implements Container {
    protected NonNullList<ItemStack> inventory;

    public InventoryStorage(int size) {
        inventory = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getItem(int index) {
        return inventory.get(index);
    }

    @Override
    @Nonnull
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = ContainerHelper.removeItem(inventory, index, count);

        if (!stack.isEmpty()) {
            this.setChanged();
        }
        return stack;
    }

    @Override
    @Nonnull
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(inventory, index);
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        inventory.set(index, stack);

        this.inventory.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    // TODO: clear() method signature may have changed in 1.20.4
    // @Override
    public void clear() {
        inventory.clear();
    }

    public void readFromNBT(CompoundTag nbt) {
        //Save Inventory
        inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

        ContainerHelper.loadAllItems(nbt, inventory);
    }

    public void writeToNBT(CompoundTag nbt) {
        //Load Inventory
        ContainerHelper.saveAllItems(nbt, inventory);
    }
}