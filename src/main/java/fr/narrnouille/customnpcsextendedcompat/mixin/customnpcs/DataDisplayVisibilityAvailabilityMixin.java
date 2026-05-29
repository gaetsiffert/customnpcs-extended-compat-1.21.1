package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import fr.narrnouille.customnpcsextendedcompat.CustomNpcVisibilityAvailabilityAccess;
import noppes.npcs.api.handler.data.IAvailability;
import noppes.npcs.controllers.VisibilityController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DataDisplay.class)
public abstract class DataDisplayVisibilityAvailabilityMixin implements CustomNpcVisibilityAvailabilityAccess {
    @Shadow
    public Availability availability;

    @Shadow
    private EntityNPCInterface npc;

    @Override
    public IAvailability getVisibilityAvailability() {
        return this.availability;
    }

    @Override
    public void refreshVisibility() {
        if (this.npc.isClientSide()) {
            return;
        }

        VisibilityController.instance.trackNpc(this.npc);
        this.npc.updateClient();
    }
}
