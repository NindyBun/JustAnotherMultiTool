package net.NindyBun.jamt.Tools;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.NindyBun.jamt.containers.MultiToolInventory;
import net.NindyBun.jamt.containers.MultiToolSlot;
import net.NindyBun.jamt.items.AbstractMultiTool;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ToolMethods {
    public static List<MultiToolInventory.MultiToolInventoryCODEC> init_inventory(ItemStack stack) {
        List<MultiToolInventory.MultiToolInventoryCODEC> inventory = new ArrayList<>();
        if (stack.isEmpty() || !(stack.getItem() instanceof AbstractMultiTool))
            return inventory;
        for (int i = 0; i < 60; i++) {
            //int x = i%10;
            //int y = i/10;
            inventory.add(new MultiToolInventory.MultiToolInventoryCODEC("", i, ((AbstractMultiTool) stack.getItem()).getLetter().default_slots[i]));
        }
        stack.getOrDefault(ModDataComponents.MULTITOOL_INVENTORY.get(), inventory);
        return inventory;
    }

    public static MultiToolInventory get_inventory(ItemStack stack) {
        List<MultiToolInventory.MultiToolInventoryCODEC> data = stack.getOrDefault(ModDataComponents.MULTITOOL_INVENTORY.get(), ToolMethods.init_inventory(stack));
        int[] inventory = new int[60];

        for (MultiToolInventory.MultiToolInventoryCODEC d : data) {
            inventory[d.slotPosition()] = d.slotState();
            //JustAnotherMultiTool.LOGGER.info("{ " + d.slotPosition().get(0) + ", " + d.slotPosition().get(1) + " } >> " + d.slotState());
        }

        return new MultiToolInventory(inventory);
    }
}
