package joshie.enchiridion.network.core;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.core.NonNullList;

public class PacketNBT {
    public CompoundTag nbt;

    public PacketNBT() {
    }

    public PacketNBT(NonNullList<ItemStack> inventory) {
        nbt = new CompoundTag();
        nbt.putInt("length", inventory.size());
        ListNBT itemList = new ListNBT();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                CompoundTag tag = new CompoundTag();
                tag.putByte("Slot", (byte) i);
                tag.putBoolean("NULLItemStack", false);
                stack.write(tag);
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

    public static void toBytes(PacketNBT packet, PacketBuffer buf) {
        try {
            new PacketBuffer(buf).writeCompoundTag(packet.nbt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fromBytes(PacketNBT packet, PacketBuffer buf) {
        try {
            packet.nbt = buf.readCompoundTag();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}