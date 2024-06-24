package net.NindyBun.jamt.entities.projectiles.PlasmaSpitter;

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

public class PlasmaSpitterEntity extends BasicProjectile {

    public PlasmaSpitterEntity(EntityType<? extends PlasmaSpitterEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ResourceLocation getTexture() {
        return new ResourceLocation(JustAnotherMultiTool.MODID, "textures/entity/projectiles/plasma_ball.png");
    }

    public PlasmaSpitterEntity(Level world, LivingEntity livingEntity) {
        super(ModEntities.PLASMA_SPITTER_ENTITY.get(), world);
        this.setOwner(livingEntity);
        this.setPos(livingEntity.getX(), livingEntity.getEyeY()-0.1f, livingEntity.getZ());
        this.damage = (float) Modules.PLASMA_SPITTER.getGroup().get(Modules.Group.BASE_DAMAGE);
        this.ignoreInvulnerablility = true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}
