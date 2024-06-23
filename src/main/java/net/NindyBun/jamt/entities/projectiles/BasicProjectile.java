package net.NindyBun.jamt.entities.projectiles;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModEntities;
import net.NindyBun.jamt.entities.projectiles.BoltCaster.BoltBeamEntity;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.UUID;

public class BasicProjectile extends Projectile {
    protected float damage = 0f;
    protected int ticksSinceFired;
    protected static final double STOP_TRESHOLD = 0.01;
    protected UUID ownerUUID;
    protected int ownerNetworkId;
    protected boolean leftOwner;

    public BasicProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        this.ticksSinceFired++;
        if (this.ticksSinceFired > 100 || this.getDeltaMovement().lengthSqr() < STOP_TRESHOLD) {
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

        EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
        if (entityhitresult != null) {
            hitresult = entityhitresult;
        }

        if (hitresult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult)hitresult).getEntity();
            Entity entity1 = this.getOwner();
            if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                hitresult = null;
            }
        }

        if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
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
        if (!this.level().isClientSide) super.onHit(pResult);
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        Entity entity = pResult.getEntity();

        entity.invulnerableTime = 0;

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
        entity.hurt(damagesource, this.damage);
        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        BlockPos blockPos = pResult.getBlockPos();
        BlockState blockState = this.level().getBlockState(blockPos);
        SoundType soundType = blockState.getSoundType(this.level(), blockPos, null);
        this.playSound(soundType.getHitSound(), 0.3F, random.nextFloat() * 0.1F + 0.9F);
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
        double d0 = Math.sqrt(pX*pX + pZ*pZ);
        this.setYRot((float)(Mth.atan2(pX, pZ) * 180.0F / (float)Math.PI));
        this.setXRot((float)(Mth.atan2(pY, d0) * 180.0F / (float)Math.PI));
        Vec3 vec3 = this.getMovementToShoot(pX, pY, pZ, pVelocity, pInaccuracy);
        this.setDeltaMovement(vec3);
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
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
                return !p_234613_0_.isSpectator();
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
        pCompound.putFloat("damage", this.damage);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.ticksSinceFired = pCompound.getInt("ticksSinceFired");
        this.damage = pCompound.getFloat("damage");
    }
}
