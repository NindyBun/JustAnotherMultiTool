package net.NindyBun.jamt.Tools;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.NindyBun.jamt.Registries.ModItems;
import net.NindyBun.jamt.containers.MultiToolInventory;
import net.NindyBun.jamt.containers.MultiToolSlot;
import net.NindyBun.jamt.items.AbstractMultiTool;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ToolMethods {
    public static List<MultiToolInventory.MultiToolInventoryCODEC> init_inventory(ItemStack stack) {
        List<MultiToolInventory.MultiToolInventoryCODEC> inventory = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            inventory.add(new MultiToolInventory.MultiToolInventoryCODEC("empty", i, ((AbstractMultiTool) stack.getItem()).getLetter().default_slots[i], Modules.EMPTY.getItem()));
        }
        return inventory;
    }

    public static void set_inventory(ItemStack stack, MultiToolInventory inventory) {
        List<MultiToolInventory.MultiToolInventoryCODEC> data = new ArrayList<>();
        for (MultiToolSlot slot : inventory.get_inventory_map()) {
            data.add(inventory.serialize_slot_content(slot.get_index()));
        }
        stack.set(ModDataComponents.MULTITOOL_INVENTORY.get(), data);
    }

    public static MultiToolInventory get_inventory(ItemStack stack) {
        List<MultiToolInventory.MultiToolInventoryCODEC> data = stack.getOrDefault(ModDataComponents.MULTITOOL_INVENTORY.get(), ToolMethods.init_inventory(stack));
        MultiToolInventory inventory = new MultiToolInventory(((AbstractMultiTool) stack.getItem()).getLetter().default_slots);

        for (MultiToolInventory.MultiToolInventoryCODEC d : data) {
            inventory.deserialize_slot_content(d);
        }

        return inventory;
    }
}
