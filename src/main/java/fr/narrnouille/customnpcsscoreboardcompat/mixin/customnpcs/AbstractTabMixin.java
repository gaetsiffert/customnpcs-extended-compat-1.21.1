package fr.narrnouille.customnpcsscoreboardcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsscoreboardcompat.CustomNpcsScoreboardCompatClientConfig;
import fr.narrnouille.customnpcsscoreboardcompat.mixin.minecraft.AbstractContainerScreenAccessor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import noppes.npcs.client.gui.player.GuiFaction;
import noppes.npcs.client.gui.player.tabs.AbstractTab;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractTab.class)
public abstract class AbstractTabMixin extends AbstractButton {
    @Shadow
    public int id;

    private AbstractTabMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    /**
     * @author Narrnouille
     * @reason CustomNPCs hard-codes vanilla inventory dimensions, which misplaces tabs on wider CustomNPCs player screens.
     */
    @Overwrite
    public AbstractTab init(Screen screen) {
        this.visible = customnpcsScoreboardCompat$isVisible();
        this.active = this.visible;

        int index = customnpcsScoreboardCompat$visibleIndex();
        int left = customnpcsScoreboardCompat$left(screen);
        int top = customnpcsScoreboardCompat$top(screen);
        this.setX(left + index * 28);
        this.setY(top - 28);
        return (AbstractTab) (Object) this;
    }

    private boolean customnpcsScoreboardCompat$isVisible() {
        return switch (this.id) {
            case 0 -> CustomNpcsScoreboardCompatClientConfig.showVanillaInventoryTab();
            case 1 -> CustomNpcsScoreboardCompatClientConfig.showFactionsInventoryTab();
            case 2 -> CustomNpcsScoreboardCompatClientConfig.showQuestsInventoryTab();
            default -> true;
        };
    }

    private int customnpcsScoreboardCompat$visibleIndex() {
        int index = 0;
        if (this.id > 0 && CustomNpcsScoreboardCompatClientConfig.showVanillaInventoryTab()) {
            index++;
        }
        if (this.id > 1 && CustomNpcsScoreboardCompatClientConfig.showFactionsInventoryTab()) {
            index++;
        }
        return index;
    }

    private static int customnpcsScoreboardCompat$left(Screen screen) {
        if (screen instanceof GuiBasic gui) {
            return gui.guiLeft;
        }
        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            return ((AbstractContainerScreenAccessor) containerScreen).customnpcsScoreboardCompat$leftPos();
        }
        return (screen.width - 176) / 2;
    }

    private static int customnpcsScoreboardCompat$top(Screen screen) {
        if (screen instanceof GuiFaction faction) {
            return faction.guiTop + 8;
        }
        if (screen instanceof GuiBasic gui) {
            return gui.guiTop;
        }
        if (screen instanceof AbstractContainerScreen<?> containerScreen) {
            return ((AbstractContainerScreenAccessor) containerScreen).customnpcsScoreboardCompat$topPos();
        }
        return (screen.height - 166) / 2;
    }
}
