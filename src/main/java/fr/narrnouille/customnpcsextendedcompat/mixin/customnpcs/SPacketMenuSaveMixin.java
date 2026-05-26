package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.controllers.data.MarkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "noppes.npcs.packets.server.SPacketMenuSave")
public abstract class SPacketMenuSaveMixin {
    @Redirect(
            method = "handle",
            at = @At(
                    value = "INVOKE",
                    target = "Lnoppes/npcs/controllers/data/MarkData;setNBT(Lnet/minecraft/nbt/CompoundTag;)V"
            )
    )
    private void customnpcsExtendedCompat$saveMarkDataAfterMenuUpdate(MarkData data, CompoundTag tag) {
        data.setNBT(tag);
        data.save();
    }
}
