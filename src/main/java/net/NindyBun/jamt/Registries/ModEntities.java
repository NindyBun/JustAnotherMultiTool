package net.NindyBun.jamt.Registries;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.entities.ModificationTableEntity;
import net.NindyBun.jamt.entities.projectiles.BoltCaster.BoltCasterEntity;
import net.NindyBun.jamt.entities.projectiles.PlasmaSpitter.PlasmaSpitterEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, JustAnotherMultiTool.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, JustAnotherMultiTool.MODID);

    public static final Supplier<BlockEntityType<ModificationTableEntity>> MODIFICATION_TABLE_ENTITY = BLOCK_ENTITY.register("modification_table",
            () -> BlockEntityType.Builder.of(ModificationTableEntity::new, ModBlocks.MODIFICATION_TABLE.get()).build(null));

    public static final Supplier<EntityType<BoltCasterEntity>> BOLT_CASTER_ENTITY = ENTITY.register("bolt_caster",
            () -> EntityType.Builder.<BoltCasterEntity>of(BoltCasterEntity::new, MobCategory.MISC)
                    .sized(5/32f, 5/32f).eyeHeight(5/64f).clientTrackingRange(4).setUpdateInterval(20).setShouldReceiveVelocityUpdates(true).build(JustAnotherMultiTool.MODID+":bolt_caster"));

    public static final Supplier<EntityType<PlasmaSpitterEntity>> PLASMA_SPITTER_ENTITY = ENTITY.register("plasma_spitter",
            () -> EntityType.Builder.<PlasmaSpitterEntity>of(PlasmaSpitterEntity::new, MobCategory.MISC)
                    .sized(5/32f, 5/32f).eyeHeight(5/64f).clientTrackingRange(4).setUpdateInterval(20).setShouldReceiveVelocityUpdates(true).build(JustAnotherMultiTool.MODID+":plasma_spitter"));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY.register(eventBus);
        ENTITY.register(eventBus);
    }
}
