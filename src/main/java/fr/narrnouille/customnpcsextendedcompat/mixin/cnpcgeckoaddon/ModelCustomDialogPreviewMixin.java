package fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon;

import fr.narrnouille.customnpcsextendedcompat.client.GeckoDialogPreviewClient;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import software.bernie.geckolib.cache.object.GeoBone;

@Mixin(targets = "com.goodbird.cnpcgeckoaddon.client.model.ModelCustom", remap = false)
public abstract class ModelCustomDialogPreviewMixin {
    private static final float ZERO_ROTATION_EPSILON = 1.0E-5F;

    private GeoBone customnpcsExtendedCompat$capturedHeadBone;
    private float customnpcsExtendedCompat$capturedBaseRotX;
    private float customnpcsExtendedCompat$capturedHeadPitch;

    @Redirect(
            method = "setCustomAnimations(Lcom/goodbird/cnpcgeckoaddon/entity/EntityCustomModel;JLsoftware/bernie/geckolib/animation/AnimationState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lsoftware/bernie/geckolib/cache/object/GeoBone;setRotX(F)V"
            )
    )
    private void customnpcsExtendedCompat$captureHeadPitch(GeoBone headBone, float headPitch) {
        this.customnpcsExtendedCompat$capturedHeadBone = headBone;
        this.customnpcsExtendedCompat$capturedBaseRotX = headBone.getRotX();
        this.customnpcsExtendedCompat$capturedHeadPitch = headPitch;
    }

    @Redirect(
            method = "setCustomAnimations(Lcom/goodbird/cnpcgeckoaddon/entity/EntityCustomModel;JLsoftware/bernie/geckolib/animation/AnimationState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lsoftware/bernie/geckolib/cache/object/GeoBone;setRotY(F)V"
            )
    )
    private void customnpcsExtendedCompat$applyGlobalHeadYaw(GeoBone headBone, float headYaw) {
        if (GeckoDialogPreviewClient.isRendering()) {
            headYaw = 0.0F;
        }

        float baseRotX = headBone == this.customnpcsExtendedCompat$capturedHeadBone
                ? this.customnpcsExtendedCompat$capturedBaseRotX
                : headBone.getRotX();
        float headPitch = headBone == this.customnpcsExtendedCompat$capturedHeadBone
                ? this.customnpcsExtendedCompat$capturedHeadPitch
                : 0.0F;

        if (customnpcsExtendedCompat$isRestingHeadLook(headPitch, headYaw)) {
            return;
        }

        Quaternionf localRotation = customnpcsExtendedCompat$toHeadParentLocalRotation(
                customnpcsExtendedCompat$getParentChainRotX(headBone, baseRotX),
                headPitch,
                headYaw
        );
        Vector3f localEuler = customnpcsExtendedCompat$extractGeckoEuler(localRotation);
        headBone.updateRotation(localEuler.x, localEuler.y, localEuler.z);
    }

    private static boolean customnpcsExtendedCompat$isRestingHeadLook(float headPitch, float headYaw) {
        return java.lang.Math.abs(headPitch) < ZERO_ROTATION_EPSILON
                && java.lang.Math.abs(headYaw) < ZERO_ROTATION_EPSILON;
    }

    private static float customnpcsExtendedCompat$getParentChainRotX(GeoBone headBone, float baseHeadRotX) {
        GeoBone parentBone = headBone.getParent();
        if (parentBone == null) {
            return -baseHeadRotX;
        }

        float rotX = 0.0F;
        for (; parentBone != null; parentBone = parentBone.getParent()) {
            rotX += parentBone.getRotX();
        }
        return rotX;
    }

    private static Quaternionf customnpcsExtendedCompat$toHeadParentLocalRotation(
            float bodyRotX,
            float headPitch,
            float headYaw
    ) {
        Quaternionf bodyRotation = new Quaternionf().rotateX(bodyRotX);
        Quaternionf targetGlobalRotation = new Quaternionf()
                .rotateY(headYaw)
                .rotateX(headPitch);
        return bodyRotation
                .invert(new Quaternionf())
                .mul(targetGlobalRotation)
                .normalize();
    }

    private static Vector3f customnpcsExtendedCompat$extractGeckoEuler(Quaternionf rotation) {
        Matrix4f matrix = rotation.get(new Matrix4f());
        float rotY = (float) java.lang.Math.asin(customnpcsExtendedCompat$clamp(-matrix.m02(), -1.0F, 1.0F));
        float rotX = (float) java.lang.Math.atan2(matrix.m12(), matrix.m22());
        float rotZ = (float) java.lang.Math.atan2(matrix.m01(), matrix.m00());
        return new Vector3f(rotX, rotY, rotZ);
    }

    private static float customnpcsExtendedCompat$clamp(float value, float min, float max) {
        return java.lang.Math.max(min, java.lang.Math.min(max, value));
    }
}
