package joshie.enchiridion.network.core;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.NonNullList;

public class PacketNBT {
    public CompoundTag nbt;

    public PacketNBT() {
    }

    public PacketNBT(NonNullList<ItemStack> inventory) {
        nbt = new CompoundTag();
        nbt.putInt("length", inventory.size());
        ListTag itemList = new ListTag();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                CompoundTag tag = new CompoundTag();
                tag.putByte("Slot", (byte) i);
                tag.putBoolean("NULLItemStack", false);
                stack.save(tag);
                itemList.add(tag);
            } else {
                CompoundTag tag = new CompoundTag();
                tag.putByte("Slot", (byte) i);
                tag.putBoolean("NULLItemStack", true);
                itemList.add(tag);
            }
        }
        nbt.put("Inventory", itemList);
    }

    public static void toBytes(PacketNBT packet, FriendlyByteBuf buf) {
        try {
            buf.writeNbt(packet.nbt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fromBytes(PacketNBT packet, FriendlyByteBuf buf) {
        try {
            packet.nbt = buf.readNbt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}