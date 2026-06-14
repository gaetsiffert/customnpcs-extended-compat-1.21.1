package fr.narrnouille.customnpcsextendedcompat;

import fr.narrnouille.customnpcsextendedcompat.network.ClientboundDialogLookPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import noppes.npcs.EventHooks;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public final class DialogInteractionTracker {
    private static final Map<EntityNPCInterface, DialogQueue> DIALOG_QUEUES = new WeakHashMap<>();
    private static final float SERVER_FOCUS_SPEED = 10.0F;
    private static final float CLIENT_PLAYER_FOCUS_SPEED = 10.0F;
    private static final float DEFAULT_MANUAL_FOCUS_SPEED = 10.0F;

    private DialogInteractionTracker() {
    }

    public static void keepNpcFacingDialogPlayer(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        PlayerData data = PlayerData.get(serverPlayer);
        if (data.dialogId < 0) {
            return;
        }

        EntityNPCInterface npc = data.editingNpc;
        if (!isValidDialogInteraction(serverPlayer, npc) || !shouldStopAndInteract(npc)) {
            removePlayer(serverPlayer, npc);
            return;
        }

        DialogQueue queue = DIALOG_QUEUES.computeIfAbsent(npc, ignored -> new DialogQueue());
        cleanupQueue(npc, queue);
        if (queue.players.putIfAbsent(serverPlayer.getUUID(), serverPlayer) == null) {
            sendLookAtPlayer(serverPlayer, npc);
        }

        refreshNpcInteraction(npc, queue);
    }

    public static boolean applyServerDialogLook(EntityNPCInterface npc) {
        DialogQueue queue = DIALOG_QUEUES.get(npc);
        if (queue == null) {
            return false;
        }

        refreshNpcInteraction(npc, queue);
        return DIALOG_QUEUES.containsKey(npc);
    }

    public static void closeDialog(ServerPlayer player, EntityNPCInterface npc) {
        if (player == null || npc == null) {
            return;
        }
        removePlayer(player, npc);
    }

    public static void closeActiveDialog(ServerPlayer player) {
        if (player == null) {
            return;
        }

        PlayerData data = PlayerData.get(player);
        if (data.dialogId < 0) {
            return;
        }

        EntityNPCInterface npc = data.editingNpc;
        if (npc != null && !npc.isRemoved()) {
            Dialog dialog = DialogController.instance == null ? null : DialogController.instance.dialogs.get(data.dialogId);
            EventHooks.onNPCDialogClose(npc, player, dialog);
            removePlayer(player, npc);
        }

        data.dialogId = -1;
    }

    public static void setManualLookAt(EntityNPCInterface npc, IPlayer<?> player, double x, double y, double z) {
        setManualLookAt(npc, player, x, y, z, DEFAULT_MANUAL_FOCUS_SPEED);
    }

    public static void setManualLookAt(EntityNPCInterface npc, IPlayer<?> player, double x, double y, double z, float speed) {
        ServerPlayer serverPlayer = getServerPlayer(player);
        if (npc == null || npc.isRemoved() || serverPlayer == null) {
            return;
        }

        DialogQueue queue = getOrCreateQueueForDialogPlayer(npc, serverPlayer);
        if (queue == null) {
            return;
        }

        ManualLook manualLook = new ManualLook(new Vec3(x, y, z), Math.max(0.0F, speed));
        queue.manualLooks.put(serverPlayer.getUUID(), manualLook);
        sendLookAtPosition(serverPlayer, npc, manualLook);
    }

    public static void clearManualLookAt(EntityNPCInterface npc, IPlayer<?> player) {
        ServerPlayer serverPlayer = getServerPlayer(player);
        if (npc == null || serverPlayer == null) {
            return;
        }

        DialogQueue queue = DIALOG_QUEUES.get(npc);
        if (queue == null) {
            sendClear(serverPlayer, npc);
            return;
        }

        queue.manualLooks.remove(serverPlayer.getUUID());
        if (queue.players.containsKey(serverPlayer.getUUID())) {
            sendLookAtPlayer(serverPlayer, npc);
        } else {
            sendClear(serverPlayer, npc);
        }
    }

    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            closeActiveDialog(player);
        }
    }

    private static boolean isValidDialogInteraction(Player player, EntityNPCInterface npc) {
        return npc != null
                && !npc.isRemoved()
                && npc.isAlive()
                && player.isAlive()
                && npc.level() == player.level();
    }

    private static boolean shouldStopAndInteract(EntityNPCInterface npc) {
        return npc.ais.stopAndInteract
                && !npc.isAttacking()
                && !npc.isNoAi();
    }

    private static void cleanupQueue(EntityNPCInterface npc, DialogQueue queue) {
        Iterator<ServerPlayer> iterator = queue.players.values().iterator();
        while (iterator.hasNext()) {
            ServerPlayer player = iterator.next();
            PlayerData data = PlayerData.get(player);
            if (!isValidDialogInteraction(player, npc)
                    || data.dialogId < 0
                    || data.editingNpc != npc
                    || !shouldStopAndInteract(npc)) {
                npc.interactingEntities.remove(player);
                queue.manualLooks.remove(player.getUUID());
                sendClear(player, npc);
                iterator.remove();
            }
        }
    }

    private static void refreshNpcInteraction(EntityNPCInterface npc, DialogQueue queue) {
        cleanupQueue(npc, queue);

        ServerPlayer activePlayer = queue.players.values().isEmpty() ? null : queue.players.values().iterator().next();
        if (activePlayer == null) {
            if (npc.interactingEntities.isEmpty()) {
                npc.lastInteract = 0;
            }
            DIALOG_QUEUES.remove(npc);
            return;
        }

        npc.getNavigation().stop();
        npc.lastInteract = npc.tickCount;
        setOnlyInteractingEntity(npc, activePlayer);
        npc.getLookControl().setLookAt(activePlayer, SERVER_FOCUS_SPEED, npc.getMaxHeadXRot());
    }

    private static void setOnlyInteractingEntity(EntityNPCInterface npc, LivingEntity activeEntity) {
        npc.interactingEntities.removeIf(entity -> entity != activeEntity);
        if (!npc.interactingEntities.contains(activeEntity)) {
            npc.interactingEntities.add(activeEntity);
        }
    }

    private static DialogQueue getOrCreateQueueForDialogPlayer(EntityNPCInterface npc, ServerPlayer player) {
        PlayerData data = PlayerData.get(player);
        if (data.dialogId < 0
                || data.editingNpc != npc
                || !isValidDialogInteraction(player, npc)
                || !shouldStopAndInteract(npc)) {
            return null;
        }

        DialogQueue queue = DIALOG_QUEUES.computeIfAbsent(npc, ignored -> new DialogQueue());
        cleanupQueue(npc, queue);
        queue.players.putIfAbsent(player.getUUID(), player);
        refreshNpcInteraction(npc, queue);
        return queue;
    }

    private static void removePlayer(ServerPlayer player, EntityNPCInterface npc) {
        DialogQueue queue = DIALOG_QUEUES.get(npc);
        if (queue != null) {
            queue.players.remove(player.getUUID());
            clearManualLookBeforeDialogClose(queue, player, npc);
            if (queue.players.isEmpty()) {
                applyVanillaPostDialogFocus(npc, player);
                DIALOG_QUEUES.remove(npc);
                sendClear(player, npc);
                return;
            }

            npc.interactingEntities.remove(player);
            sendClear(player, npc);
            refreshNpcInteraction(npc, queue);
        } else {
            applyVanillaPostDialogFocus(npc, player);
            sendClear(player, npc);
        }
    }

    private static void clearManualLookBeforeDialogClose(DialogQueue queue, ServerPlayer player, EntityNPCInterface npc) {
        if (queue.manualLooks.remove(player.getUUID()) != null) {
            sendLookAtPlayer(player, npc);
        }
    }

    private static void applyVanillaPostDialogFocus(EntityNPCInterface npc, ServerPlayer player) {
        if (!isValidDialogInteraction(player, npc) || !shouldStopAndInteract(npc)) {
            npc.interactingEntities.remove(player);
            if (npc.interactingEntities.isEmpty()) {
                npc.lastInteract = 0;
            }
            return;
        }

        npc.getNavigation().stop();
        npc.lastInteract = npc.tickCount;
        setOnlyInteractingEntity(npc, player);
        npc.getLookControl().setLookAt(player, SERVER_FOCUS_SPEED, npc.getMaxHeadXRot());
    }

    private static ServerPlayer getServerPlayer(IPlayer<?> player) {
        if (player == null || !(player.getMCEntity() instanceof ServerPlayer serverPlayer)) {
            return null;
        }
        return serverPlayer;
    }

    private static void sendLookAtPlayer(ServerPlayer player, EntityNPCInterface npc) {
        PacketDistributor.sendToPlayer(player, ClientboundDialogLookPayload.lookAtPlayer(npc.getId(), CLIENT_PLAYER_FOCUS_SPEED));
    }

    private static void sendLookAtPosition(ServerPlayer player, EntityNPCInterface npc, ManualLook manualLook) {
        PacketDistributor.sendToPlayer(
                player,
                ClientboundDialogLookPayload.lookAtPosition(
                        npc.getId(),
                        manualLook.position.x,
                        manualLook.position.y,
                        manualLook.position.z,
                        manualLook.speed
                )
        );
    }

    private static void sendClear(ServerPlayer player, EntityNPCInterface npc) {
        PacketDistributor.sendToPlayer(player, ClientboundDialogLookPayload.clear(npc.getId()));
    }

    private static final class DialogQueue {
        private final LinkedHashMap<UUID, ServerPlayer> players = new LinkedHashMap<>();
        private final Map<UUID, ManualLook> manualLooks = new LinkedHashMap<>();
    }

    private record ManualLook(Vec3 position, float speed) {
    }
}
