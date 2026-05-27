package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.CustomGuiDefaultBackgroundAccess;
import net.minecraft.client.gui.GuiGraphics;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiCustom.class)
public abstract class GuiCustomDefaultBackgroundMixin {
    @Shadow
    public CustomGuiWrapper guiWrapper;

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnoppes/npcs/client/gui/custom/GuiCustom;renderBackground(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"
            )
    )
    private void customnpcsExtendedCompat$renderDefaultBackground(GuiCustom gui, GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!(this.guiWrapper instanceof CustomGuiDefaultBackgroundAccess access) || access.customnpcsExtendedCompat$drawDefaultBackground()) {
            gui.renderBackground(graphics, mouseX, mouseY, partialTicks);
        }
    }
}
