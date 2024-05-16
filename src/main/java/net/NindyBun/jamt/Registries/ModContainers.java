package net.NindyBun.jamt.Registries;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.containers.ModificationTableContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModContainers {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, JustAnotherMultiTool.MODID);

    public static final Supplier<MenuType<ModificationTableContainer>> MODIFICATION_TABLE_CONTAINER = CONTAINERS.register("modification_table",
            () -> IMenuTypeExtension.create(ModificationTableContainer::new));

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}
