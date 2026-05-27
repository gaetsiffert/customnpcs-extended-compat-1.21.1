package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.CustomGuiDefaultBackgroundAccess;
import net.minecraft.nbt.CompoundTag;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CustomGuiWrapper.class)
public abstract class CustomGuiWrapperDefaultBackgroundMixin implements CustomGuiDefaultBackgroundAccess {
    @Unique
    private static final String customnpcsExtendedCompat$DRAW_DEFAULT_BACKGROUND_TAG = "drawDefaultBackground";
    @Unique
    private boolean customnpcsExtendedCompat$drawDefaultBackground = true;

    @Override
    public boolean customnpcsExtendedCompat$drawDefaultBackground() {
        return this.customnpcsExtendedCompat$drawDefaultBackground;
    }

    @Override
    public void customnpcsExtendedCompat$setDrawDefaultBackground(boolean drawDefaultBackground) {
        this.customnpcsExtendedCompat$drawDefaultBackground = drawDefaultBackground;
    }

    public boolean getDrawDefaultBackground() {
        return this.customnpcsExtendedCompat$drawDefaultBackground;
    }

    public void setDrawDefaultBackground(boolean drawDefaultBackground) {
        this.customnpcsExtendedCompat$drawDefaultBackground = drawDefaultBackground;
    }

    @Inject(method = "fromNBT", at = @At("RETURN"))
    private void customnpcsExtendedCompat$readDrawDefaultBackground(CompoundTag compound, CallbackInfoReturnable<ICustomGui> cir) {
        if (compound.contains(customnpcsExtendedCompat$DRAW_DEFAULT_BACKGROUND_TAG)) {
            this.customnpcsExtendedCompat$drawDefaultBackground = compound.getBoolean(customnpcsExtendedCompat$DRAW_DEFAULT_BACKGROUND_TAG);
        }
    }

    @Inject(method = "toNBT", at = @At("RETURN"))
    private void customnpcsExtendedCompat$writeDrawDefaultBackground(CallbackInfoReturnable<CompoundTag> cir) {
        cir.getReturnValue().putBoolean(
                customnpcsExtendedCompat$DRAW_DEFAULT_BACKGROUND_TAG,
                this.customnpcsExtendedCompat$drawDefaultBackground
        );
    }
}
