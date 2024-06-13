package net.NindyBun.jamt.events;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Tools.ToolMethods;
import net.NindyBun.jamt.items.AbstractMultiTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@EventBusSubscriber(modid = JustAnotherMultiTool.MODID, value = Dist.CLIENT)
public class MiningLaserRender {
    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Player player = Minecraft.getInstance().player;

        if (!ToolMethods.isHoldingTool(player) || !ToolMethods.isUsingTool(player)) {
            return;
        }

        double range = player.blockInteractionRange();
        float ticks = Minecraft.getInstance().getFrameTime();
        Vec3 pos = player.getEyePosition(ticks);
        HitResult trace = player.pick(range, 0F, false);

        drawLaser(event, pos, trace, player, ticks);
    }

    private static void drawLaser(RenderLevelStageEvent event, Vec3 from, HitResult trace, Player player, float ticks) {
        InteractionHand activeHand;
        if (player.getMainHandItem().getItem() instanceof AbstractMultiTool) {
            activeHand = InteractionHand.MAIN_HAND;
        } else if (player.getOffhandItem().getItem() instanceof AbstractMultiTool) {
            activeHand = InteractionHand.OFF_HAND;
        } else {
            return;
        }

        double distance = Math.max(1, from.subtract(trace.getLocation()).length());
        long gameTime = player.level().getGameTime();

        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        PoseStack matrix = event.getPoseStack();

        matrix.pushPose();

        matrix.translate(-view.x(), -view.y(), -view.z());
        matrix.translate(from.x, from.y, from.z);
        matrix.mulPose(Axis.YP.rotationDegrees(Mth.lerp(ticks, -player.getYRot(), -player.yRotO)));
        matrix.mulPose(Axis.XP.rotationDegrees(Mth.lerp(ticks, player.getXRot(), player.xRotO)));

        PoseStack.Pose matrixstack$entry = matrix.last();
        Matrix3f matrixNormal = matrixstack$entry.normal();
        Matrix4f positionMatrix = matrixstack$entry.pose();

        drawBeam(0, 0, 0, buffer.getBuffer(LaserRenderTypes.LASER_GLOW), positionMatrix, matrixNormal, 0.02f * 3.5f, activeHand, distance, 0.5, 1, ticks, 1f, 0.3f, 0.3f,0.7f);

        drawBeam(0, 0, 0, buffer.getBuffer(LaserRenderTypes.LASER), positionMatrix, matrixNormal, 0.02f, activeHand, distance, gameTime, gameTime + distance * 1.5, ticks, 1f, 0.8f, 0.5f,1f);
        matrix.popPose();
        buffer.endBatch();
    }

    private static void drawBeam(double xOff, double yOff, double zOff, VertexConsumer builder, Matrix4f positionMatrix, Matrix3f matrixNormalIn, float thickness, InteractionHand hand, double distance, double v1, double v2, float ticks, float r, float g, float b, float alpha) {
        Vector3f vector3f = new Vector3f(0.0f, 1.0f, 0.0f);
        vector3f.mul(matrixNormalIn);
        LocalPlayer player = Minecraft.getInstance().player;
        // Support for hand sides remembering to take into account of Skin options
        if (Minecraft.getInstance().options.mainHand().get() != HumanoidArm.RIGHT)
            hand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;

        float startXOffset = hand == InteractionHand.MAIN_HAND ? -0.20f : 0.2f;
        float startYOffset = -.108f*1.25f;
        float startZOffset = 0.60f;

        // Adjust for fov changing
        startZOffset += (1 - player.getFieldOfViewModifier());

        float f = (Mth.lerp(ticks, player.xRotO, player.getXRot()) - Mth.lerp(ticks, player.xBobO, player.xBob));
        float f1 = (Mth.lerp(ticks, player.yRotO, player.getYRot()) - Mth.lerp(ticks, player.yBobO, player.yBob));
        startXOffset = startXOffset + (f1 / 750);
        startYOffset = startYOffset + (f / 750);

        Vector4f vec1 = new Vector4f(startXOffset, -thickness + startYOffset, startZOffset, 1.0F);
        vec1.mul(positionMatrix);
        Vector4f vec2 = new Vector4f((float) xOff, -thickness + (float) yOff, (float) distance + (float) zOff, 1.0F);
        vec2.mul(positionMatrix);
        Vector4f vec3 = new Vector4f((float) xOff, thickness + (float) yOff, (float) distance + (float) zOff, 1.0F);
        vec3.mul(positionMatrix);
        Vector4f vec4 = new Vector4f(startXOffset, thickness + startYOffset, startZOffset, 1.0F);
        vec4.mul(positionMatrix);

        if (hand == InteractionHand.MAIN_HAND) {
            builder.vertex(vec4.x(), vec4.y(), vec4.z(), r, g, b, alpha, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec3.x(), vec3.y(), vec3.z(), r, g, b, alpha, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec2.x(), vec2.y(), vec2.z(), r, g, b, alpha, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec1.x(), vec1.y(), vec1.z(), r, g, b, alpha, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            //Rendering a 2nd time to allow you to see both sides in multiplayer, shouldn't be necessary with culling disabled but here we are....
            builder.vertex(vec1.x(), vec1.y(), vec1.z(), r, g, b, alpha, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec2.x(), vec2.y(), vec2.z(), r, g, b, alpha, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec3.x(), vec3.y(), vec3.z(), r, g, b, alpha, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec4.x(), vec4.y(), vec4.z(), r, g, b, alpha, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
        } else {
            builder.vertex(vec1.x(), vec1.y(), vec1.z(), r, g, b, alpha, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec2.x(), vec2.y(), vec2.z(), r, g, b, alpha, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec3.x(), vec3.y(), vec3.z(), r, g, b, alpha, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec4.x(), vec4.y(), vec4.z(), r, g, b, alpha, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            //Rendering a 2nd time to allow you to see both sides in multiplayer, shouldn't be necessary with culling disabled but here we are....
            builder.vertex(vec4.x(), vec4.y(), vec4.z(), r, g, b, alpha, 0, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec3.x(), vec3.y(), vec3.z(), r, g, b, alpha, 0, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec2.x(), vec2.y(), vec2.z(), r, g, b, alpha, 1, (float) v2, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
            builder.vertex(vec1.x(), vec1.y(), vec1.z(), r, g, b, alpha, 1, (float) v1, OverlayTexture.NO_OVERLAY, 15728880, vector3f.x(), vector3f.y(), vector3f.z());
        }
    }

    public static class LaserRenderTypes extends RenderType {
        private static final ResourceLocation LASER_BEAM = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/lasers/laser.png");
        private static final ResourceLocation LASER_GLOW_BEAM = new ResourceLocation(JustAnotherMultiTool.MODID, "textures/lasers/laser_glow.png");

        public LaserRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
            super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
        }

        public static final RenderType LASER = create("laser",
                DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256,false, false,
                RenderType.CompositeState.builder().setTextureState(new TextureStateShard(LASER_BEAM, false, false))
                        .setShaderState(ShaderStateShard.POSITION_COLOR_TEX_SHADER)
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(NO_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setLightmapState(NO_LIGHTMAP)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(false));

        public static final RenderType LASER_GLOW = create("laser",
                DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256,false, false,
                RenderType.CompositeState.builder().setTextureState(new TextureStateShard(LASER_GLOW_BEAM, false, false))
                        .setShaderState(ShaderStateShard.POSITION_COLOR_TEX_SHADER)
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(NO_DEPTH_TEST)
                        .setCullState(NO_CULL)
                        .setLightmapState(NO_LIGHTMAP)
                        .setWriteMaskState(COLOR_WRITE)
                        .createCompositeState(false));
    }
}
