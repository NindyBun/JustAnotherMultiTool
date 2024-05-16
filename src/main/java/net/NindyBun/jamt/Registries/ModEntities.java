package net.NindyBun.jamt.Registries;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.entities.ModificationTableEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, JustAnotherMultiTool.MODID);

    public static final Supplier<BlockEntityType<ModificationTableEntity>> MODIFICATION_TABLE_ENTITY = BLOCK_ENTITY.register("modification_table",
            () -> BlockEntityType.Builder.of(ModificationTableEntity::new, ModBlocks.MODIFICATION_TABLE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY.register(eventBus);
    }
}
