package net.NindyBun.jamt.entities.projectiles.BoltCaster;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModEntities;
import net.NindyBun.jamt.entities.projectiles.BasicProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.*;

public class BoltBeamEntity extends BasicProjectile {
    private ResourceLocation texture = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/entity/projectiles/bolt_beam_1.png");

    public BoltBeamEntity(EntityType<? extends BoltBeamEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public BoltBeamEntity(Level world, LivingEntity livingEntity, @Nullable ResourceLocation texture) {
        super(ModEntities.BOLT_BEAM_ENTITY.get(), world);
        this.setOwner(livingEntity);
        this.setPos(livingEntity.getX(), livingEntity.getEyeY()-0.1f, livingEntity.getZ());
        if (texture != null) this.texture = texture;
        this.damage = (float) Modules.BOLT_CASTER.getGroup().get(Modules.Group.BASE_DAMAGE);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
