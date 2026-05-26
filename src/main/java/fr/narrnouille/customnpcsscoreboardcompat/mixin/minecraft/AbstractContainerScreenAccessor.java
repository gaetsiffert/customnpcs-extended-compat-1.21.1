package fr.narrnouille.customnpcsscoreboardcompat.mixin.minecraft;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor("leftPos")
    int customnpcsScoreboardCompat$leftPos();

    @Accessor("topPos")
    int customnpcsScoreboardCompat$topPos();
}
