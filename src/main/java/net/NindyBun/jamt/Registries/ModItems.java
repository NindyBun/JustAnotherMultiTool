package net.NindyBun.jamt.Registries;

import net.NindyBun.jamt.Enums.MultiToolClasses;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.items.AbstractMultiTool;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, JustAnotherMultiTool.MODID);

    public static final Supplier<Item> C_MULTITOOL = ITEMS.register("c_multitool", () -> new AbstractMultiTool(MultiToolClasses.C));
    public static final Supplier<BlockItem> MODIFICATION_TABLE_ITEM = ITEMS.register("modification_table", () -> new BlockItem(ModBlocks.MODIFICATION_TABLE.get(), new Item.Properties().stacksTo(64)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
