package net.NindyBun.jamt.entities.projectiles.BoltCaster;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
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

public class BoltBeamEntity extends Projectile {
    private double damage = 1;
    private int ticksSinceFired;
    private static final double STOP_TRESHOLD = 0.01;
    private UUID ownerUUID;
    private int ownerNetworkId;
    private ResourceLocation texture = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/entity/projectiles/bolt_beam_1.png");
    private boolean leftOwner;

    public BoltBeamEntity(EntityType<? extends BoltBeamEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    public ResourceLocation getTexture() {
        return this.texture;
    }

    public BoltBeamEntity(Level world, LivingEntity livingEntity, @Nullable ResourceLocation texture) {
        super(ModEntities.BOLT_BEAM_ENTITY.get(), world);
        this.setOwner(livingEntity);
        Player player = (Player)livingEntity;
        this.setPos(livingEntity.getX(), livingEntity.getEyeY()-0.1f, livingEntity.getZ());
        if (texture != null) this.texture = texture;
        this.damage = (double) Modules.BOLT_CASTER.getGroup().get(Modules.Group.BASE_DAMAGE);
    }

    @Override
    public void tick() {
        ticksSinceFired++;
        if (ticksSinceFired > 100 || this.getDeltaMovement().lengthSqr() < STOP_TRESHOLD) {
            this.discard();
        }
        if (!this.leftOwner) {
            this.leftOwner = this.checkLeftOwner();
        }

        super.tick();

        Vec3 vec3 = this.getDeltaMovement();
        Vec3 vec32 = this.position();
        Vec3 vec33 = vec32.add(vec3);

        HitResult hitresult = this.level().clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hitresult.getType() != HitResult.Type.MISS) {
            vec33 = hitresult.getLocation();
        }

        while(!this.isRemoved()) {
            EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
            if (entityhitresult != null) {
                hitresult = entityhitresult;
            }

            if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)hitresult).getEntity();
                Entity entity1 = this.getOwner();
                if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                    hitresult = null;
                    entityhitresult = null;
                }
            }

            if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitresult)) {
                this.onHit(hitresult);
            }

            if (hitresult != null && hitresult.getType() != HitResult.Type.MISS) {
                if (net.neoforged.neoforge.event.EventHooks.onProjectileImpact(this, hitresult))
                    break;
                ProjectileDeflection projectiledeflection = this.hitTargetOrDeflectSelf(hitresult);
                if (projectiledeflection != ProjectileDeflection.NONE) {
                    break;
                }
            }

            if (entityhitresult == null ) {
                break;
            }

            hitresult = null;
        }

        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * 180.0F / (float)Math.PI));
            this.setXRot((float)(Mth.atan2(vec3.y, d0) * 180.0F / (float)Math.PI));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        vec3 = this.getDeltaMovement();
        double d5 = vec3.x;
        double d6 = vec3.y;
        double d1 = vec3.z;

        double d7 = this.getX() + d5;
        double d2 = this.getY() + d6;
        double d3 = this.getZ() + d1;
        double d4 = vec3.horizontalDistance();

        this.setYRot((float)(Mth.atan2(d5, d1) * 180.0F / (float)Math.PI));

        this.setXRot((float)(Mth.atan2(d6, d4) * 180.0F / (float)Math.PI));
        this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
        this.setYRot(lerpRotation(this.yRotO, this.getYRot()));

        if (this.isInWater()) {
            for (int j = 0; j < 4; j++) {
                this.level().addParticle(ParticleTypes.BUBBLE, d7 - d5 * 0.25, d2 - d6 * 0.25, d3 - d1 * 0.25, d5, d6, d1);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().add(0.0, -this.getDefaultGravity(), 0.0));
        this.setPos(d7, d2, d3);
        this.checkInsideBlocks();
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 p_36758_, Vec3 p_36759_) {
        return ProjectileUtil.getEntityHitResult(this.level(), this, p_36758_, p_36759_, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), this::canHitEntity);
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        int i = Mth.ceil(Mth.clamp(this.damage, 0.0, 2.147483647E9));


        Entity owner = this.getOwner();
        DamageSource damagesource;
        if (owner == null) {
            damagesource = new DamageSource(this.level().damageSources().generic().typeHolder(), this, this);
        } else {
            damagesource = new DamageSource(this.level().damageSources().generic().typeHolder(), this, owner);
            if (owner instanceof LivingEntity) {
                ((LivingEntity)owner).setLastHurtMob(entity);
            }
        }

        boolean flag = entity.getType() == EntityType.ENDERMAN;
        if (entity.hurt(damagesource, (float)i)) {
            if (flag) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (!this.level().isClientSide && owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingEntity, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity)owner, livingEntity);
                }

                this.doPostHurtEffects(livingEntity);
            }

            //this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            entity.invulnerableTime = (int) Modules.BOLT_CASTER.getGroup().get(Modules.Group.FIRE_RATE) + 11;
            this.discard();
        } else {
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        BlockPos blockPos = pResult.getBlockPos();
        BlockState blockState = this.level().getBlockState(blockPos);
        SoundType soundType = blockState.getSoundType(this.level(), blockPos, null);
        this.playSound(soundType.getBreakSound(), 0.5F, random.nextFloat() * 0.1F + 0.9F);
        this.discard();
    }

    public void doPostHurtEffects(LivingEntity pTarget) {
    }

    @Override
    protected double getDefaultGravity() {
        return 0.01;
    }


    @Override
    public void shoot(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
        super.shoot(pX, pY, pZ, pVelocity, pInaccuracy);
        this.ticksSinceFired = 0;
    }

    @Override
    public void lerpTo(double pX, double pY, double pZ, float pYRot, float pXRot, int pSteps) {
        this.setPos(pX, pY, pZ);
        this.setRot(pYRot, pXRot);
    }

    @Override
    public void lerpMotion(double pX, double pY, double pZ) {
        super.lerpMotion(pX, pY, pZ);
        this.ticksSinceFired = 0;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = this.getBoundingBox().getSize() * 10.0;
        if (Double.isNaN(d0)) {
            d0 = 1.0;
        }

        d0 *= 64.0 * getViewScale();
        return pDistance < d0 * d0;
    }

    public void setOwner(@Nullable Entity p_212361_1_) {
        if (p_212361_1_ != null) {
            this.ownerUUID = p_212361_1_.getUUID();
            this.ownerNetworkId = p_212361_1_.getId();
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.ownerUUID != null && this.level() instanceof ServerLevel) {
            return ((ServerLevel)this.level()).getEntity(this.ownerUUID);
        } else {
            return this.ownerNetworkId != 0 ? this.level().getEntity(this.ownerNetworkId) : null;
        }
    }

    private boolean checkLeftOwner() {
        Entity entity = this.getOwner();
        if (entity != null) {
            for(Entity entity1 : this.level().getEntities(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (p_234613_0_) -> {
                return !p_234613_0_.isSpectator() && p_234613_0_.isPickable();
            })) {
                if (entity1.getRootVehicle() == entity.getRootVehicle()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("ticksSinceFired", this.ticksSinceFired);
        pCompound.putDouble("damage", this.damage);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.ticksSinceFired = pCompound.getInt("ticksSinceFired");
        this.damage = pCompound.getDouble("damage");
    }
}
