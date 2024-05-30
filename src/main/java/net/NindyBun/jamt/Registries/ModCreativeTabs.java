package net.NindyBun.jamt.Registries;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JustAnotherMultiTool.MODID);

    public static final Supplier<CreativeModeTab> JAMT_TAB = CREATIVE_MODE_TAB.register("jamt_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab." + JustAnotherMultiTool.MODID + ".jamt_tab"))
                    .icon(() -> new ItemStack(ModItems.C_MULTITOOL.get()))
                    .displayItems(((pParameters, pOutput) -> {
                        ModItems.ITEMS.getEntries().forEach(item -> {
                            pOutput.accept(item.get());
                        });
                        ModItems.MODULES.getEntries().forEach(module -> {
                            if (module.get().getDefaultInstance().is(ModItems.EMPTY.get())) return;
                            pOutput.accept(module.get());
                        });
                    }))
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
