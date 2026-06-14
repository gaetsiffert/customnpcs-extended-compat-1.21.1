package fr.narrnouille.customnpcsextendedcompat;

import fr.narrnouille.customnpcsextendedcompat.compat.cnpcgeckoaddon.EntityCustomModelTransitionBridge;
import fr.narrnouille.customnpcsextendedcompat.network.ClientboundStopManualAnimationPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

public final class GeckoManualAnimationController {
    private static final String GECKO_MODEL_CLASS = "com.goodbird.cnpcgeckoaddon.entity.EntityCustomModel";

    private GeckoManualAnimationController() {
    }

    public static void stopManualAnimation(EntityNPCInterface npc) {
        if (npc == null || npc.isRemoved()) {
            return;
        }

        clearLocal(npc);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                npc,
                new ClientboundStopManualAnimationPayload(npc.getId())
        );
    }

    public static void stopManualAnimation(EntityNPCInterface npc, IPlayer<?> player) {
        if (npc == null || npc.isRemoved() || player == null
                || !(player.getMCEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        PacketDistributor.sendToPlayer(serverPlayer, new ClientboundStopManualAnimationPayload(npc.getId()));
    }

    public static void clearLocal(EntityNPCInterface npc) {
        EntityCustomModelTransitionBridge bridge = getGeckoModelBridge(npc);
        if (bridge != null) {
            bridge.customnpcsExtendedCompat$stopManualAnimation();
        }
    }

    private static EntityCustomModelTransitionBridge getGeckoModelBridge(EntityNPCInterface npc) {
        if (!(npc instanceof EntityCustomNpc customNpc) || customNpc.modelData == null) {
            return null;
        }

        LivingEntity modelEntity = customNpc.modelData.getEntity(npc);
        if (modelEntity instanceof EntityCustomModelTransitionBridge bridge
                && GECKO_MODEL_CLASS.equals(modelEntity.getClass().getName())) {
            return bridge;
        }
        return null;
    }
}
