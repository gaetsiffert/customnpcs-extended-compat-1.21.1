package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomNpcNameTagBridge;
import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomNpcSizeBridge;
import net.minecraft.nbt.CompoundTag;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DataDisplay.class, remap = false)
public abstract class DataDisplaySizeMixin implements CustomNpcSizeBridge, CustomNpcNameTagBridge {
    @Unique
    private static final String CUSTOM_SIZE_KEY = "CustomNpcsExtendedCompatSize";
    @Unique
    private static final String CUSTOM_NAME_TAG_Y_OFFSET_KEY = "CustomNpcsExtendedCompatNameTagYOffset";
    @Unique
    private static final String CUSTOM_NAME_VISIBILITY_DISTANCE_KEY = "CustomNpcsExtendedCompatNameVisibilityDistance";
    @Unique
    private static final String CUSTOM_TITLE_VISIBILITY_DISTANCE_KEY = "CustomNpcsExtendedCompatTitleVisibilityDistance";
    @Unique
    private static final String CUSTOM_MARK_MOVEMENT_SECONDS_KEY = "CustomNpcsExtendedCompatMarkMovementSeconds";

    @Shadow
    private int modelSize;

    @Shadow
    private EntityNPCInterface npc;

    @Unique
    private float customnpcsExtendedCompat$sizeScale = CustomNpcSizeBridge.DEFAULT_SIZE_SCALE;
    @Unique
    private float customnpcsExtendedCompat$nameTagYOffset = CustomNpcNameTagBridge.DEFAULT_NAME_TAG_Y_OFFSET;
    @Unique
    private float customnpcsExtendedCompat$nameVisibilityDistance = CustomNpcNameTagBridge.DEFAULT_NAME_VISIBILITY_DISTANCE;
    @Unique
    private float customnpcsExtendedCompat$titleVisibilityDistance = CustomNpcNameTagBridge.DEFAULT_TITLE_VISIBILITY_DISTANCE;
    @Unique
    private float customnpcsExtendedCompat$markMovementSeconds = CustomNpcNameTagBridge.DEFAULT_MARK_MOVEMENT_SECONDS;

    @Override
    public float customnpcsExtendedCompat$getSizeScale() {
        return this.customnpcsExtendedCompat$sizeScale;
    }

    @Override
    public void customnpcsExtendedCompat$setSizeScale(float sizeScale) {
        float clamped = CustomNpcSizeBridge.clampSizeScale(sizeScale);
        if (CustomNpcSizeBridge.isSameSizeScale(this.customnpcsExtendedCompat$sizeScale, clamped)) {
            return;
        }

        this.customnpcsExtendedCompat$sizeScale = clamped;
        this.modelSize = CustomNpcSizeBridge.scaleToLegacySize(clamped);
        this.npc.updateClient = true;
        this.npc.refreshDimensions();
    }

    @Override
    public float customnpcsExtendedCompat$getNameTagYOffset() {
        return this.customnpcsExtendedCompat$nameTagYOffset;
    }

    @Override
    public void customnpcsExtendedCompat$setNameTagYOffset(float yOffset) {
        this.customnpcsExtendedCompat$nameTagYOffset = CustomNpcNameTagBridge.clampNameTagYOffset(yOffset);
        this.npc.updateClient = true;
    }

    @Override
    public float customnpcsExtendedCompat$getNameVisibilityDistance() {
        return this.customnpcsExtendedCompat$nameVisibilityDistance;
    }

    @Override
    public void customnpcsExtendedCompat$setNameVisibilityDistance(float distance) {
        this.customnpcsExtendedCompat$nameVisibilityDistance = CustomNpcNameTagBridge.clampNameVisibilityDistance(distance);
        this.npc.updateClient = true;
    }

    @Override
    public float customnpcsExtendedCompat$getTitleVisibilityDistance() {
        return this.customnpcsExtendedCompat$titleVisibilityDistance;
    }

    @Override
    public void customnpcsExtendedCompat$setTitleVisibilityDistance(float distance) {
        this.customnpcsExtendedCompat$titleVisibilityDistance = CustomNpcNameTagBridge.clampTitleVisibilityDistance(distance);
        this.npc.updateClient = true;
    }

    @Override
    public float customnpcsExtendedCompat$getMarkMovementSeconds() {
        return this.customnpcsExtendedCompat$markMovementSeconds;
    }

    @Override
    public void customnpcsExtendedCompat$setMarkMovementSeconds(float seconds) {
        this.customnpcsExtendedCompat$markMovementSeconds = CustomNpcNameTagBridge.clampMarkMovementSeconds(seconds);
        this.npc.updateClient = true;
    }

    @Inject(method = "save", at = @At("RETURN"))
    private void customnpcsExtendedCompat$saveFloatSize(
            CompoundTag compound,
            CallbackInfoReturnable<CompoundTag> callbackInfo
    ) {
        compound.putFloat(CUSTOM_SIZE_KEY, this.customnpcsExtendedCompat$sizeScale);
        compound.putFloat(CUSTOM_NAME_TAG_Y_OFFSET_KEY, this.customnpcsExtendedCompat$nameTagYOffset);
        compound.putFloat(CUSTOM_NAME_VISIBILITY_DISTANCE_KEY, this.customnpcsExtendedCompat$nameVisibilityDistance);
        compound.putFloat(CUSTOM_TITLE_VISIBILITY_DISTANCE_KEY, this.customnpcsExtendedCompat$titleVisibilityDistance);
        compound.putFloat(CUSTOM_MARK_MOVEMENT_SECONDS_KEY, this.customnpcsExtendedCompat$markMovementSeconds);
        compound.putInt("Size", CustomNpcSizeBridge.scaleToLegacySize(this.customnpcsExtendedCompat$sizeScale));
    }

    @Inject(method = "readToNBT", at = @At("RETURN"))
    private void customnpcsExtendedCompat$readFloatSize(CompoundTag compound, CallbackInfo callbackInfo) {
        float sizeScale = compound.contains(CUSTOM_SIZE_KEY)
                ? compound.getFloat(CUSTOM_SIZE_KEY)
                : CustomNpcSizeBridge.legacySizeToScale(compound.getInt("Size"));
        this.customnpcsExtendedCompat$setSizeScale(sizeScale);

        float nameTagYOffset = compound.contains(CUSTOM_NAME_TAG_Y_OFFSET_KEY)
                ? compound.getFloat(CUSTOM_NAME_TAG_Y_OFFSET_KEY)
                : CustomNpcNameTagBridge.DEFAULT_NAME_TAG_Y_OFFSET;
        this.customnpcsExtendedCompat$setNameTagYOffset(nameTagYOffset);

        float nameVisibilityDistance = compound.contains(CUSTOM_NAME_VISIBILITY_DISTANCE_KEY)
                ? compound.getFloat(CUSTOM_NAME_VISIBILITY_DISTANCE_KEY)
                : CustomNpcNameTagBridge.DEFAULT_NAME_VISIBILITY_DISTANCE;
        this.customnpcsExtendedCompat$setNameVisibilityDistance(nameVisibilityDistance);

        float titleVisibilityDistance = compound.contains(CUSTOM_TITLE_VISIBILITY_DISTANCE_KEY)
                ? compound.getFloat(CUSTOM_TITLE_VISIBILITY_DISTANCE_KEY)
                : CustomNpcNameTagBridge.DEFAULT_TITLE_VISIBILITY_DISTANCE;
        this.customnpcsExtendedCompat$setTitleVisibilityDistance(titleVisibilityDistance);

        float markMovementSeconds = compound.contains(CUSTOM_MARK_MOVEMENT_SECONDS_KEY)
                ? compound.getFloat(CUSTOM_MARK_MOVEMENT_SECONDS_KEY)
                : CustomNpcNameTagBridge.DEFAULT_MARK_MOVEMENT_SECONDS;
        this.customnpcsExtendedCompat$setMarkMovementSeconds(markMovementSeconds);
    }

    @Inject(method = "setSize", at = @At("RETURN"))
    private void customnpcsExtendedCompat$syncLegacySize(int size, CallbackInfo callbackInfo) {
        this.customnpcsExtendedCompat$sizeScale = CustomNpcSizeBridge.legacySizeToScale(this.modelSize);
    }
}
