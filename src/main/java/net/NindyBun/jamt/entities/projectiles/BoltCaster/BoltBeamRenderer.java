package net.NindyBun.jamt.entities.projectiles.BoltCaster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.NindyBun.jamt.JustAnotherMultiTool;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class BoltBeamRenderer extends EntityRenderer<BoltBeamEntity> {
    public BoltBeamRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(BoltBeamEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot())));

        pPoseStack.scale(1/32f, 1/32f, 1/32f);
        pPoseStack.translate(-5.5F, 2.5F, 0.0F);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(pEntity)));
        PoseStack.Pose pose = pPoseStack.last();

        for (int j = 0; j < 4; j++) {
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            this.vertex(pose, vertexconsumer, -8, -1.5f, 0, 0.0F, 0.0F, 0, 1, 0, pPackedLight);
            this.vertex(pose, vertexconsumer, 8, -1.5f, 0, 16/32F, 0.0F, 0, 1, 0, pPackedLight);
            this.vertex(pose, vertexconsumer, 8, 1.5f, 0, 16/32F, 3/32f, 0, 1, 0, pPackedLight);
            this.vertex(pose, vertexconsumer, -8, 1.5f, 0, 0.0F, 3/32f, 0, 1, 0, pPackedLight);
        }

        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    public void vertex(
            PoseStack.Pose pPose,
            VertexConsumer pConsumer,
            float pX,
            float pY,
            float pZ,
            float pU,
            float pV,
            int pNormalX,
            int pNormalY,
            int pNormalZ,
            int pPackedLight
    ) {
        pConsumer.vertex(pPose, pX, pY, pZ)
                .color(255, 255, 255, 255)
                .uv(pU, pV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(pPackedLight)
                .normal(pPose, (float)pNormalX, (float)pNormalZ, (float)pNormalY)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(BoltBeamEntity pEntity) {
        return pEntity.getTexture();
    }

}
