package fr.narrnouille.customnpcsextendedcompat.client;

import fr.narrnouille.customnpcsextendedcompat.GeckoManualAnimationController;
import fr.narrnouille.customnpcsextendedcompat.network.ClientboundStopManualAnimationPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import noppes.npcs.entity.EntityNPCInterface;

public final class GeckoManualAnimationClient {
    private GeckoManualAnimationClient() {
    }

    public static void handle(ClientboundStopManualAnimationPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        Entity entity = minecraft.level.getEntity(payload.npcId());
        if (entity instanceof EntityNPCInterface npc) {
            GeckoManualAnimationController.clearLocal(npc);
        }
    }
}
