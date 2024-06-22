package net.NindyBun.jamt.Tools;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import java.util.List;
import java.util.stream.Collectors;

public class VectorFunctions {

    public static EntityHitResult getEntityLookingAt(Player player, double range) {
        Level world = player.level();
        Vec3 look = player.getLookAngle();
        Vec3 start = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
        EntityHitResult entityhitresult = null;

        for (double i = 0.5; i < range; i+= 0.5) {
            Vec3 end = new Vec3(player.getX() + look.x * i, player.getY() + player.getEyeHeight() + look.y * i, player.getZ() + look.z * i);
            EntityHitResult entityhitresult1 = ProjectileUtil.getEntityHitResult(
                    player.level(), player, start, end, new AABB(start, end).inflate(1.0), p_156765_ -> !p_156765_.isSpectator(), 0.0F
            );
            if (entityhitresult1 == null || entityhitresult1.getType() != HitResult.Type.ENTITY || !entityhitresult1.getEntity().isAlive()) continue;
            BlockHitResult blockHitResult = world.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            if (blockHitResult.getType() == HitResult.Type.BLOCK) continue;
            entityhitresult = entityhitresult1;
            break;
        }

        return entityhitresult;
    }

    public static BlockHitResult getLookingAt(Player player, double range) {
        return getLookingAt(player, ClipContext.Fluid.NONE, range);
    }

    public static BlockHitResult getLookingAt(Player player, ClipContext.Fluid rayTraceFluid, double range) {
        Level world = player.level();

        Vec3 look = player.getLookAngle();
        Vec3 start = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());

        Vec3 end = new Vec3(player.getX() + look.x * range, player.getY() + player.getEyeHeight() + look.y * range, player.getZ() + look.z * range);
        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, rayTraceFluid, player);
        return world.clip(context);
    }

    public static ImmutableList<BlockPos> getBreakableArea(ItemStack stack, BlockPos pos, Player player, int radius) {
        List<BlockPos> area;
        Level world = player.getCommandSenderWorld();

        BlockHitResult hitResult = VectorFunctions.getLookingAt(player, player.blockInteractionRange());
        if (hitResult.getType() == HitResult.Type.MISS || player.isSecondaryUseActive() || !ToolMethods.canToolAffect(stack, world, pos)) {
            return ImmutableList.of();
        }

        if (radius == 0) {
            return ToolMethods.canToolAffect(stack, world, pos) ? ImmutableList.of(hitResult.getBlockPos()) : ImmutableList.of();
        }

        int yMin = -1;
        int yMax = 2*radius-1;

        area = switch (hitResult.getDirection()) {
            case DOWN, UP ->
                    BlockPos.betweenClosedStream(pos.offset(-radius, 0, -radius), pos.offset(radius, 0, radius))
                            .filter(blockPos -> ToolMethods.canToolAffect(stack, world, blockPos))
                            .map(BlockPos::immutable)
                            .toList();
            case NORTH, SOUTH -> BlockPos.betweenClosedStream(pos.offset(-radius, yMin, 0), pos.offset(radius, yMax, 0))
                    .filter(blockPos -> ToolMethods.canToolAffect(stack, world, blockPos))
                    .map(BlockPos::immutable)
                    .toList();
            default -> BlockPos.betweenClosedStream(pos.offset(0, yMin, -radius), pos.offset(0, yMax, radius))
                    .filter(blockPos -> ToolMethods.canToolAffect(stack, world, blockPos))
                    .map(BlockPos::immutable)
                    .toList();
        };
        //area.remove(pos);
        return ImmutableList.copyOf(area);
    }
}
