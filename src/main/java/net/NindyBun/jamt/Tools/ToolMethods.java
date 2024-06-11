package net.NindyBun.jamt.Tools;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.NindyBun.jamt.Registries.ModItems;
import net.NindyBun.jamt.containers.MultiToolInventory;
import net.NindyBun.jamt.containers.MultiToolSlot;
import net.NindyBun.jamt.items.AbstractMultiTool;
import net.NindyBun.jamt.screens.ModificationTableScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

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
        Map<String, Integer> map = new HashMap<>();
        for (MultiToolSlot slot : inventory.get_inventory_map()) {
            data.add(inventory.serialize_slot_content(slot.get_index()));
            map.put(slot.get_module().getName(), map.getOrDefault(slot.get_module().getName(), 0) + (slot.get_module() != Modules.EMPTY ? 1 : 0));
        }
        for (Map.Entry<String, Integer> m : map.entrySet()) {
            int count = m.getValue();
            if (count > 1)
                stack.set(ModDataComponents.OVERLOADED.get(), true);
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

    public static boolean canToolAffect(ItemStack toolStack, Level world, BlockPos pos) {
        Item toolItem = toolStack.getItem();
        BlockState state = world.getBlockState(pos);

        if (state.getDestroySpeed(world, pos) < 0) {
            return false;
        }

        return toolItem.isCorrectToolForDrops(toolStack, state) || !state.requiresCorrectToolForDrops() && toolItem.getDestroySpeed(toolStack, state) > 1.0F;
    }
}
