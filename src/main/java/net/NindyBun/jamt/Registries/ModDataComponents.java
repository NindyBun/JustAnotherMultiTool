package net.NindyBun.jamt.Registries;

import com.mojang.serialization.Codec;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, JustAnotherMultiTool.MODID);

    public static final Supplier<DataComponentType<Integer>> ENERGY = DATA_COMPONENT.register("energy",
            () -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final Supplier<DataComponentType<Integer>> ENERGY_MAX = DATA_COMPONENT.register("energy_max",
            () -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT.register(eventBus);
    }
}
