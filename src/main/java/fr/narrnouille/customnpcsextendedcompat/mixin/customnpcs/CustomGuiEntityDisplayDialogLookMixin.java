package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.client.DialogLookOverrideClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import noppes.npcs.client.gui.custom.components.CustomGuiEntityDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CustomGuiEntityDisplay.class, remap = false)
public abstract class CustomGuiEntityDisplayDialogLookMixin {
    @Inject(method = "drawEntity(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/Entity;IIFIIIFFZ)V",
            at = @At("HEAD"))
    private static void customnpcsExtendedCompat$beginDialogLookPreviewRender(
            GuiGraphics graphics,
            Entity entity,
            int x,
            int y,
            float scale,
            int rotation,
            int mouseX,
            int mouseY,
            float offsetX,
            float offsetY,
            boolean followingCursor,
            CallbackInfo callbackInfo
    ) {
        DialogLookOverrideClient.beginGuiEntityDisplayRender();
    }

    @Inject(method = "drawEntity(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/Entity;IIFIIIFFZ)V",
            at = @At("RETURN"))
    private static void customnpcsExtendedCompat$endDialogLookPreviewRender(
            GuiGraphics graphics,
            Entity entity,
            int x,
            int y,
            float scale,
            int rotation,
            int mouseX,
            int mouseY,
            float offsetX,
            float offsetY,
            boolean followingCursor,
            CallbackInfo callbackInfo
    ) {
        DialogLookOverrideClient.endGuiEntityDisplayRender();
    }
}
