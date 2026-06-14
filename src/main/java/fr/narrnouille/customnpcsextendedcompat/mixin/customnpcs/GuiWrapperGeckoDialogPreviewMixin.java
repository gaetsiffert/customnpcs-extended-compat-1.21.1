package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.client.GeckoDialogPreviewClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.client.gui.custom.components.CustomGuiEntityDisplay;
import noppes.npcs.client.gui.player.GuiDialogInteract;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.client.gui.components.GuiWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiWrapper.class, remap = false)
public abstract class GuiWrapperGeckoDialogPreviewMixin {
    private static final String GECKO_MODEL_CLASS = "com.goodbird.cnpcgeckoaddon.entity.EntityCustomModel";
    private static final float SLIGHT_PROFILE_PREVIEW_YAW = -25.0F;

    @Redirect(
            method = "drawNpc(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/LivingEntity;IIFIII)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnoppes/npcs/client/gui/custom/components/CustomGuiEntityDisplay;drawEntity(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/Entity;IIFIIIFF)V"
            )
    )
    private void customnpcsExtendedCompat$drawGeckoDialogPreviewSlightProfile(
            GuiGraphics graphics,
            Entity entity,
            int x,
            int y,
            float scale,
            int rotation,
            int mouseX,
            int mouseY,
            float offsetX,
            float offsetY
    ) {
        if (!isDialogGeckoNpc(entity)) {
            CustomGuiEntityDisplay.drawEntity(graphics, entity, x, y, scale, rotation, mouseX, mouseY, offsetX, offsetY);
            return;
        }

        GeckoDialogPreviewClient.begin(SLIGHT_PROFILE_PREVIEW_YAW);
        try {
            CustomGuiEntityDisplay.drawEntity(graphics, entity, x, y, scale, 0, mouseX, mouseY, offsetX, offsetY, false);
        } finally {
            GeckoDialogPreviewClient.end();
        }
    }

    private static boolean isDialogGeckoNpc(Entity entity) {
        if (!(Minecraft.getInstance().screen instanceof GuiDialogInteract)) {
            return false;
        }
        if (!(entity instanceof EntityCustomNpc npc) || npc.modelData == null) {
            return false;
        }

        LivingEntity modelEntity = npc.modelData.getEntity(npc);
        return modelEntity != null && GECKO_MODEL_CLASS.equals(modelEntity.getClass().getName());
    }
}
