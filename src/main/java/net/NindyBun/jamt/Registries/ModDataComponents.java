package net.NindyBun.jamt.Registries;

import com.mojang.serialization.Codec;
import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Tools.Codecs;
import net.NindyBun.jamt.containers.MultiToolInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT = DeferredRegister.createDataComponents(JustAnotherMultiTool.MODID);

    public static final Supplier<DataComponentType<Integer>> ENERGY = DATA_COMPONENT.register("energy",
            () -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    public static final Supplier<DataComponentType<Integer>> ENERGY_MAX = DATA_COMPONENT.register("energy_max",
            () -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    public static final Supplier<DataComponentType<Integer>> HEAT = DATA_COMPONENT.register("heat",
            () -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    public static final Supplier<DataComponentType<Integer>> HEAT_MAX = DATA_COMPONENT.register("heat_max",
            () -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    public static final Supplier<DataComponentType<List<MultiToolInventory.MultiToolInventoryCODEC>>> MULTITOOL_INVENTORY = DATA_COMPONENT.register("multitool_inventory",
            () -> DataComponentType.<List<MultiToolInventory.MultiToolInventoryCODEC>>builder().persistent(MultiToolInventory.MultiToolInventoryCODEC.LIST_CODEC).networkSynchronized(MultiToolInventory.MultiToolInventoryCODEC.STREAM_CODEC.apply(ByteBufCodecs.list())).build());

    public static final Supplier<DataComponentType<Boolean>> OVERLOADED = DATA_COMPONENT.register("overloaded",
            () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final Supplier<DataComponentType<Float>> DESTROY_PROGRESS = DATA_COMPONENT.register("destroy_progress",
            () -> DataComponentType.<Float>builder().persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT).build());

    public static final Supplier<DataComponentType<Integer>> FIRE_RATE = DATA_COMPONENT.register("fire_rate",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final Supplier<DataComponentType<Integer>> COOLDOWN = DATA_COMPONENT.register("cooldown",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final Supplier<DataComponentType<Integer>> BURST_AMOUNT = DATA_COMPONENT.register("burst_amount",
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).build());

    public static final Supplier<DataComponentType<BlockPos>> LAST_BLOCKPOS = DATA_COMPONENT.register("last_blockpos",
            () -> DataComponentType.<BlockPos>builder().persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).build());

    public static final Supplier<DataComponentType<String>> SELECTED_MODULE = DATA_COMPONENT.register("selected_module",
            () -> DataComponentType.<String>builder().persistent(Codec.STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8).build());

    public static final Supplier<DataComponentType<Boolean>> ACTIVE = DATA_COMPONENT.register("active",
            () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static final Supplier<DataComponentType<Integer>> BOLT_CASTER_MAG = DATA_COMPONENT.register("bolt_caster_mag",
            () -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    public static final Supplier<DataComponentType<Integer>> BOLT_CASTER_MAG_MAX = DATA_COMPONENT.register("bolt_caster_mag_max",
            () -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    public static final Supplier<DataComponentType<Integer>> PLASMA_SPITTER_MAG = DATA_COMPONENT.register("plasma_spitter_mag",
            () -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    public static final Supplier<DataComponentType<Integer>> PLASMA_SPITTER_MAG_MAX = DATA_COMPONENT.register("plasma_spitter_mag_max",
            () -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, Integer.MAX_VALUE)).networkSynchronized(ByteBufCodecs.VAR_INT).build());

    public static final Supplier<DataComponentType<Boolean>> WAS_RELOADING = DATA_COMPONENT.register("was_reloading",
            () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT.register(eventBus);
    }
}
