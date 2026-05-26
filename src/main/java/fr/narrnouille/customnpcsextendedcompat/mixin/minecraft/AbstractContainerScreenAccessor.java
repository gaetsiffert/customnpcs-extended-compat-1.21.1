package fr.narrnouille.customnpcsextendedcompat.mixin.minecraft;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor("leftPos")
    int customnpcsExtendedCompat$leftPos();

    @Accessor("topPos")
    int customnpcsExtendedCompat$topPos();
}
