package net.NindyBun.jamt.entities.projectiles.BoltCaster;

import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModEntities;
import net.NindyBun.jamt.entities.projectiles.BasicProjectile;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class BoltCasterEntity extends BasicProjectile {
    private ResourceLocation texture = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/entity/projectiles/bolt_beam_1.png");

    public BoltCasterEntity(EntityType<? extends BoltCasterEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public BoltCasterEntity(Level world, LivingEntity livingEntity, @Nullable ResourceLocation texture) {
        super(ModEntities.BOLT_CASTER_ENTITY.get(), world);
        this.setOwner(livingEntity);
        this.setPos(livingEntity.getX(), livingEntity.getEyeY()-0.1f, livingEntity.getZ());
        if (texture != null) this.texture = texture;
        this.damage = (float) Modules.BOLT_CASTER.getGroup().get(Modules.Group.BASE_DAMAGE);
        this.ignoreInvulnerablility = true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
