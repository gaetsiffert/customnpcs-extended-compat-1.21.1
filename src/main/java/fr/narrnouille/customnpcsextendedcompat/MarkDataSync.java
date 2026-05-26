package fr.narrnouille.customnpcsextendedcompat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketMarkData;

public final class MarkDataSync {
    private MarkDataSync() {
    }

    public static void sendExistingTo(ServerPlayer player, LivingEntity entity) {
        MarkData data = MarkData.get(entity);
        if (!data.marks.isEmpty()) {
            send(player, entity, data);
        }
    }

    public static void sendTo(ServerPlayer player, LivingEntity entity) {
        send(player, entity, MarkData.get(entity));
    }

    private static void send(ServerPlayer player, LivingEntity entity, MarkData data) {
        Packets.send(player, new PacketMarkData(entity.getId(), data.getNBT()));
    }
}
