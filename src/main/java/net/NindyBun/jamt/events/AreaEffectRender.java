package net.NindyBun.jamt.events;


import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.NindyBun.jamt.Enums.Modules;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Registries.ModDataComponents;
import net.NindyBun.jamt.Tools.ToolMethods;
import net.NindyBun.jamt.Tools.VectorFunctions;
import net.NindyBun.jamt.items.AbstractMultiTool;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;

@EventBusSubscriber(modid = JustAnotherMultiTool.MODID, value = Dist.CLIENT)
public class AreaEffectRender {

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Player player = Minecraft.getInstance().player;
        ItemStack stack = ToolMethods.getTool(player);

        if (!ToolMethods.isHoldingTool(player) || !stack.getOrDefault(ModDataComponents.SELECTED_MODULE.get(), Modules.EMPTY.getName()).equals(Modules.MINING_LASER.getName())) {
            return;
        }

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        BlockHitResult hit = VectorFunctions.getLookingAt(player, player.blockInteractionRange());
        if (hit.getType() == HitResult.Type.MISS) {
            return;
        }
        BlockPos target = hit.getBlockPos();
        ImmutableList<BlockPos> area = player.isCrouching() ? ImmutableList.of(target) : VectorFunctions.getBreakableArea(stack, target, player, 1);

        PoseStack poseStack = event.getPoseStack();
        VertexConsumer vertexBuilder = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES);
        Entity viewEntity = camera.getEntity();
        Level world = player.level();

        Vec3 pos = camera.getPosition();
        double x = pos.x();
        double y = pos.y();
        double z = pos.z();

        poseStack.pushPose();
        for (BlockPos blockPos : area) {
            if (world.getWorldBorder().isWithinBounds(blockPos)) {
                renderHitOutline(poseStack, vertexBuilder, viewEntity, x, y, z, blockPos, world.getBlockState(blockPos));
            }
        }
        poseStack.popPose();
        drawBlockDestroyProgress(event.getPoseStack(), Minecraft.getInstance().gameRenderer.getMainCamera(), player.getCommandSenderWorld(), area, stack);
    }

    @SubscribeEvent
    public static void renderBlockHighlights(RenderHighlightEvent.Block event) {
        Player player = Minecraft.getInstance().player;
        if (ToolMethods.isHoldingTool(player)) {
            event.setCanceled(true);
        }
    }

    private static void drawBlockDestroyProgress(PoseStack posestack, Camera camera, Level level, List<BlockPos> area, ItemStack stack) {
        double d0 = camera.getPosition().x();
        double d1 = camera.getPosition().y();
        double d2 = camera.getPosition().z();
        float destroyProgress = stack.getOrDefault(ModDataComponents.DESTROY_PROGRESS.get(), 0.0F);
        int progress = Math.min(destroyProgress > 0.0F ? (int)(destroyProgress * 10.0F) : -1, 9);
        if (progress < 0 || progress > 9) {
            return;
        }

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        VertexConsumer vertexBuilder = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(progress));

        for (BlockPos pos : area) {
            posestack.pushPose();
            posestack.translate((double) pos.getX() - d0, (double) pos.getY() - d1, (double) pos.getZ() - d2);
            VertexConsumer matrixBuilder = new SheetedDecalTextureGenerator(vertexBuilder, posestack.last(), 1F);
            dispatcher.renderBreakingTexture(level.getBlockState(pos), pos, level, posestack, matrixBuilder);
            posestack.popPose();
        }
    }

    private static void renderHitOutline(
            PoseStack poseStack,
            VertexConsumer consumer,
            Entity entity,
            double camX,
            double camY,
            double camZ,
            BlockPos pos,
            BlockState state
    ) {
        renderShape(
                poseStack,
                consumer,
                state.getShape(entity.level(), pos, CollisionContext.of(entity)),
                (double)pos.getX() - camX,
                (double)pos.getY() - camY,
                (double)pos.getZ() - camZ,
                0.0F,
                0.0F,
                0.0F,
                0.4F
        );
    }

    private static void renderShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha) {
        PoseStack.Pose posestack$pose = poseStack.last();
        shape.forAllEdges(
                (x0, y0, z0, x1, y1, z1) -> {
                    float f = (float)(x1 - x0);
                    float f1 = (float)(y1 - y0);
                    float f2 = (float)(z1 - z0);
                    float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
                    f /= f3;
                    f1 /= f3;
                    f2 /= f3;
                    consumer.vertex(posestack$pose, (float)(x0 + x), (float)(y0 + y), (float)(z0 + z))
                            .color(red, green, blue, alpha)
                            .normal(posestack$pose, f, f1, f2)
                            .endVertex();
                    consumer.vertex(posestack$pose, (float)(x1 + x), (float)(y1 + y), (float)(z1 + z))
                            .color(red, green, blue, alpha)
                            .normal(posestack$pose, f, f1, f2)
                            .endVertex();
                }
        );
    }
}
