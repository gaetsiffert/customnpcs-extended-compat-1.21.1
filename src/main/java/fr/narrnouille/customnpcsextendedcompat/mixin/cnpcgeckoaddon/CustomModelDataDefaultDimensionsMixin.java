package fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon;

import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.goodbird.cnpcgeckoaddon.data.CustomModelData", remap = false)
public abstract class CustomModelDataDefaultDimensionsMixin {
    private static final float CNPC_GECKO_DEFAULT_WIDTH = 0.7F;
    private static final float CNPC_GECKO_DEFAULT_HEIGHT = 2.0F;
    private static final float CUSTOMNPCS_DEFAULT_WIDTH = 0.6F;
    private static final float CUSTOMNPCS_DEFAULT_HEIGHT = 1.91F;
    private static final float EPSILON = 1.0E-5F;
    @Unique
    private static final String CUSTOMNPCS_EXTENDED_COMPAT_EXPLICIT_HITBOX =
            "CustomNpcsExtendedCompatExplicitGeckoHitbox";

    @Unique
    private boolean customnpcsExtendedCompat$explicitHitboxDimensions;

    @Shadow
    private float width;

    @Shadow
    private float height;

    @Inject(method = "getWidth()F", at = @At("HEAD"), cancellable = true)
    private void customnpcsExtendedCompat$matchCustomNpcDefaultWidth(CallbackInfoReturnable<Float> callbackInfo) {
        if (!this.customnpcsExtendedCompat$explicitHitboxDimensions && isDefaultDimension(this.width, CNPC_GECKO_DEFAULT_WIDTH)) {
            callbackInfo.setReturnValue(CUSTOMNPCS_DEFAULT_WIDTH);
        }
    }

    @Inject(method = "getHeight()F", at = @At("HEAD"), cancellable = true)
    private void customnpcsExtendedCompat$matchCustomNpcDefaultHeight(CallbackInfoReturnable<Float> callbackInfo) {
        if (!this.customnpcsExtendedCompat$explicitHitboxDimensions && isDefaultDimension(this.height, CNPC_GECKO_DEFAULT_HEIGHT)) {
            callbackInfo.setReturnValue(CUSTOMNPCS_DEFAULT_HEIGHT);
        }
    }

    @Inject(method = "setWidth(F)V", at = @At("HEAD"))
    private void customnpcsExtendedCompat$markExplicitWidth(float width, CallbackInfo callbackInfo) {
        this.customnpcsExtendedCompat$explicitHitboxDimensions = true;
    }

    @Inject(method = "setHeight(F)V", at = @At("HEAD"))
    private void customnpcsExtendedCompat$markExplicitHeight(float height, CallbackInfo callbackInfo) {
        this.customnpcsExtendedCompat$explicitHitboxDimensions = true;
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    private void customnpcsExtendedCompat$readExplicitHitboxMarker(CompoundTag compound, CallbackInfo callbackInfo) {
        this.customnpcsExtendedCompat$explicitHitboxDimensions =
                compound.getBoolean(CUSTOMNPCS_EXTENDED_COMPAT_EXPLICIT_HITBOX);
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void customnpcsExtendedCompat$writeExplicitHitboxMarker(
            CompoundTag compound,
            CallbackInfoReturnable<CompoundTag> callbackInfo
    ) {
        if (this.customnpcsExtendedCompat$explicitHitboxDimensions) {
            callbackInfo.getReturnValue().putBoolean(CUSTOMNPCS_EXTENDED_COMPAT_EXPLICIT_HITBOX, true);
        }
    }

    private static boolean isDefaultDimension(float value, float defaultValue) {
        return Math.abs(value - defaultValue) < EPSILON;
    }
}
