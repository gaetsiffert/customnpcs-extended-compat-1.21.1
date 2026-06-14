package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.CustomNpcDialogSlots;
import fr.narrnouille.customnpcsextendedcompat.DialogInteractionTracker;
import fr.narrnouille.customnpcsextendedcompat.GeckoManualAnimationController;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IDialog;
import noppes.npcs.api.wrapper.EntityLivingWrapper;
import noppes.npcs.api.wrapper.NPCWrapper;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(NPCWrapper.class)
public abstract class NPCWrapperDialogSlotMixin<T extends EntityNPCInterface> extends EntityLivingWrapper<T> {
    public NPCWrapperDialogSlotMixin(T entity) {
        super(entity);
    }

    /**
     * @author Narrnouille
     * @reason Match the expanded NPC dialog editor limit of 64 slots.
     */
    @Overwrite
    public void setDialog(int slot, IDialog dialog) {
        customnpcsExtendedCompat$validateDialogSlot(slot);
        if (dialog == null) {
            ((EntityNPCInterface) this.entity).dialogs.remove(slot);
        } else {
            DialogOption option = new DialogOption();
            option.dialogId = dialog.getId();
            option.title = dialog.getName();
            ((EntityNPCInterface) this.entity).dialogs.put(slot, option);
        }
        ((EntityNPCInterface) this.entity).updateClient = true;
    }

    /**
     * @author Narrnouille
     * @reason Match the expanded NPC dialog editor limit of 64 slots.
     */
    @Overwrite
    public IDialog getDialog(int slot) {
        customnpcsExtendedCompat$validateDialogSlot(slot);
        DialogOption option = ((EntityNPCInterface) this.entity).dialogs.get(slot);
        if (option == null || !option.hasDialog()) {
            return null;
        }
        return option.getDialog();
    }

    public void setDialogLookAt(IPlayer<?> player, double x, double y, double z) {
        DialogInteractionTracker.setManualLookAt((EntityNPCInterface) this.entity, player, x, y, z);
    }

    public void setDialogLookAt(IPlayer<?> player, double x, double y, double z, float speed) {
        DialogInteractionTracker.setManualLookAt((EntityNPCInterface) this.entity, player, x, y, z, speed);
    }

    public void setDialogLookAt(IPlayer<?> player, double x, double y, double z, double speed) {
        DialogInteractionTracker.setManualLookAt((EntityNPCInterface) this.entity, player, x, y, z, (float) speed);
    }

    public void clearDialogLookAt(IPlayer<?> player) {
        DialogInteractionTracker.clearManualLookAt((EntityNPCInterface) this.entity, player);
    }

    public void stopManualAnimation() {
        GeckoManualAnimationController.stopManualAnimation((EntityNPCInterface) this.entity);
    }

    public void stopManualAnimation(IPlayer<?> player) {
        GeckoManualAnimationController.stopManualAnimation((EntityNPCInterface) this.entity, player);
    }

    @Unique
    private static void customnpcsExtendedCompat$validateDialogSlot(int slot) {
        if (slot < 0 || slot >= CustomNpcDialogSlots.MAX_DIALOGS_PER_NPC) {
            throw new CustomNPCsException("Slot needs to be between 0 and " + (CustomNpcDialogSlots.MAX_DIALOGS_PER_NPC - 1), new Object[0]);
        }
    }
}
