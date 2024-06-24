package net.NindyBun.jamt.entities.projectiles.PlasmaSpitter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PlasmaSpitterRenderer extends EntityRenderer<PlasmaSpitterEntity> {
    public PlasmaSpitterRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(PlasmaSpitterEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(pPartialTick, pEntity.xRotO, pEntity.getXRot())));

        pPoseStack.scale(1/96f, 1/96f, 1/96f);
        pPoseStack.translate(0.0F, 7.0F, 0.0F);
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(pEntity)));
        PoseStack.Pose pose = pPoseStack.last();

        for (int j = 0; j < 2; j++) {
            pPoseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            this.vertex(pose, vertexconsumer, 0, -8, -8, 0.0F, 0.0F, 0, 1, 0, pPackedLight);
            this.vertex(pose, vertexconsumer, 0, -8, 8, 16/32F, 0.0F, 0, 1, 0, pPackedLight);
            this.vertex(pose, vertexconsumer, 0, 8, 8, 16/32F, 16/32f, 0, 1, 0, pPackedLight);
            this.vertex(pose, vertexconsumer, 0, 8, -8, 0.0F, 16/32f, 0, 1, 0, pPackedLight);
        }

        for (int j = 0; j < 4; j++) {
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            this.vertex(pose, vertexconsumer, -8, -8, 0, 0.0F, 0.0F, 0, 1, 0, pPackedLight);
            this.vertex(pose, vertexconsumer, 8, -8, 0, 16/32F, 0.0F, 0, 1, 0, pPackedLight);
            this.vertex(pose, vertexconsumer, 8, 8, 0, 16/32F, 16/32f, 0, 1, 0, pPackedLight);
            this.vertex(pose, vertexconsumer, -8, 8, 0, 0.0F, 16/32f, 0, 1, 0, pPackedLight);
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
    public ResourceLocation getTextureLocation(PlasmaSpitterEntity pEntity) {
        return pEntity.getTexture();
    }

}
