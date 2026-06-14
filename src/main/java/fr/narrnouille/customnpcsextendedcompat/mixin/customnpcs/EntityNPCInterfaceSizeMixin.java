package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomNpcSizeBridge;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityNPCInterface.class, remap = false)
public abstract class EntityNPCInterfaceSizeMixin {
    private static final float HIDDEN_HITBOX_WIDTH = 1.0E-5F;

    @Shadow
    public DataDisplay display;

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void customnpcsExtendedCompat$useFloatSizeForDimensions(
            Pose pose,
            CallbackInfoReturnable<EntityDimensions> callbackInfo
    ) {
        EntityDimensions dimensions = callbackInfo.getReturnValue();
        if (dimensions == null) {
            return;
        }

        float ratio = customnpcsExtendedCompat$floatToLegacyRatio();
        if (CustomNpcSizeBridge.isSameSizeScale(ratio, 1.0F)) {
            return;
        }

        float width = dimensions.width() <= HIDDEN_HITBOX_WIDTH * 10.0F
                ? HIDDEN_HITBOX_WIDTH
                : dimensions.width() * ratio;
        callbackInfo.setReturnValue(EntityDimensions.scalable(width, dimensions.height() * ratio));
    }

    private float customnpcsExtendedCompat$floatToLegacyRatio() {
        float floatScale = CustomNpcSizeBridge.getSizeScale(this.display);
        float legacyScale = CustomNpcSizeBridge.legacySizeToScale(this.display.getSize());
        return legacyScale <= 0.0F ? 1.0F : floatScale / legacyScale;
    }
}
