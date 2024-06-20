package net.NindyBun.jamt.Tools;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.Enums.MultiToolClasses;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.NindyBun.jamt.Registries.ModItems;
import net.NindyBun.jamt.containers.MultiToolInventory;
import net.NindyBun.jamt.containers.MultiToolSlot;
import net.NindyBun.jamt.items.AbstractMultiTool;
import net.NindyBun.jamt.screens.ModificationTableScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
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

    public static List<MultiToolInventory.MultiToolInventoryCODEC> init_inventory(MultiToolClasses letter) {
        List<MultiToolInventory.MultiToolInventoryCODEC> inventory = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            inventory.add(new MultiToolInventory.MultiToolInventoryCODEC("empty", i, letter.default_slots[i], Modules.EMPTY.getItem()));
        }
        return inventory;
    }

    public static void set_inventory(ItemStack stack, MultiToolInventory inventory) {
        List<MultiToolInventory.MultiToolInventoryCODEC> data = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        List<MultiToolSlot> slots = inventory.get_inventory_map();
        for (MultiToolSlot slot : slots) {
            data.add(inventory.serialize_slot_content(slot.get_index()));
            if (slot.get_module().getType().equals("tool"))
                map.put(slot.get_module().getName(), map.getOrDefault(slot.get_module().getName(), 0) + (slot.get_module() != Modules.EMPTY ? 1 : 0));
        }
        stack.set(ModDataComponents.OVERLOADED.get(), false);
        int tools = 0;
        for (Map.Entry<String, Integer> m : map.entrySet()) {
            int count = m.getValue();
            tools += 1;
            if (count > 1 || tools > 4) {
                stack.set(ModDataComponents.OVERLOADED.get(), true);
            }
        }
        stack.set(ModDataComponents.MULTITOOL_INVENTORY.get(), data);

        String selected = stack.get(ModDataComponents.SELECTED_MODULE.get());
        List<Modules> modules = ToolMethods.get_module_tools(stack);
        if (modules.isEmpty()) {
            stack.set(ModDataComponents.SELECTED_MODULE.get(), Modules.EMPTY.getName());
        } else if (!modules.contains(Modules.valueOf(selected.toUpperCase()))) {
            stack.set(ModDataComponents.SELECTED_MODULE.get(), modules.getFirst().getName());
        }
    }

    public static MultiToolInventory get_inventory(ItemStack stack) {
        List<MultiToolInventory.MultiToolInventoryCODEC> data = stack.getOrDefault(ModDataComponents.MULTITOOL_INVENTORY.get(), ToolMethods.init_inventory(stack));
        MultiToolInventory inventory = new MultiToolInventory(((AbstractMultiTool) stack.getItem()).getLetter().default_slots);

        for (MultiToolInventory.MultiToolInventoryCODEC d : data) {
            inventory.deserialize_slot_content(d);
        }

        return inventory;
    }

    public static List<Modules> get_module_tools(ItemStack stack) {
        List<Modules> modules = new ArrayList<>();
        MultiToolInventory inventory = ToolMethods.get_inventory(stack);
        List<MultiToolSlot> slots = inventory.get_inventory_map();
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).get_module() == Modules.EMPTY) continue;
            if (!slots.get(i).get_module().getType().equals("tool")) continue;
            modules.add(slots.get(i).get_module());
        }
        return modules;
    }

    public static boolean canToolAffect(ItemStack toolStack, Level world, BlockPos pos) {
        Item toolItem = toolStack.getItem();
        BlockState state = world.getBlockState(pos);

        if (state.getDestroySpeed(world, pos) < 0) {
            return false;
        }

        return toolItem.isCorrectToolForDrops(toolStack, state) || !state.requiresCorrectToolForDrops() && toolItem.getDestroySpeed(toolStack, state) > 1.0F;
    }

    public static boolean isHoldingTool(Player player) {
        ItemStack stack = player.getMainHandItem().getItem() instanceof AbstractMultiTool ? player.getMainHandItem() : player.getOffhandItem();
        return !stack.isEmpty() && stack.getItem() instanceof AbstractMultiTool;
    }

    public static boolean isUsingTool(Player player) {
        if (!ToolMethods.isHoldingTool(player)) return false;
        //if ((player.getItemInHand(player.getUsedItemHand()).getItem() instanceof AbstractMultiTool) && player.isUsingItem()) return true;
        return ToolMethods.getTool(player).get(ModDataComponents.ACTIVE.get());
    }

    public static ItemStack getTool(Player player) {
        if (ToolMethods.isHoldingTool(player)) {
            return player.getMainHandItem().getItem() instanceof AbstractMultiTool ? player.getMainHandItem() : player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }

    public static int getModuleIndex(List<Modules> modules, String name) {
        if (modules.isEmpty()) return -1;

        for (int i = 0; i < modules.size(); i++) {
            if (modules.get(i).getName().equals(name)) return i;
        }

        return -1;
    }
}
