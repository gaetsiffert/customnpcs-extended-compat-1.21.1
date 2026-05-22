package fr.narrnouille.customnpcsscoreboardcompat;

import fr.narrnouille.customnpcsscoreboardcompat.mixin.minecraft.ServerScoreboardAccessor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.scores.Objective;

public final class ScoreboardObjectiveSync {
    private ScoreboardObjectiveSync() {
    }

    public static void sendOrStartTracking(ServerGamePacketListenerImpl connection, Packet<?> packet) {
        if (!(packet instanceof ClientboundSetObjectivePacket objectivePacket)
                || objectivePacket.getMethod() != ClientboundSetObjectivePacket.METHOD_ADD) {
            connection.send(packet);
            return;
        }

        ServerScoreboard scoreboard = (ServerScoreboard) connection.player.getScoreboard();
        Objective objective = scoreboard.getObjective(objectivePacket.getObjectiveName());
        if (objective == null) {
            connection.send(packet);
            return;
        }

        // Starting tracking lets vanilla send the objective and scores once; tracked objectives already have that state.
        if (!((ServerScoreboardAccessor) scoreboard).customnpcsScoreboardCompat$getTrackedObjectives().contains(objective)) {
            scoreboard.startTrackingObjective(objective);
        }
    }

    public static void sendLoginObjectiveState(ServerGamePacketListenerImpl connection, Packet<?> packet) {
        if (!(packet instanceof ClientboundSetObjectivePacket objectivePacket)
                || objectivePacket.getMethod() != ClientboundSetObjectivePacket.METHOD_ADD) {
            connection.send(packet);
            return;
        }

        ServerScoreboard scoreboard = (ServerScoreboard) connection.player.getScoreboard();
        Objective objective = scoreboard.getObjective(objectivePacket.getObjectiveName());
        if (objective == null) {
            connection.send(packet);
            return;
        }

        ((ServerScoreboardAccessor) scoreboard).customnpcsScoreboardCompat$getTrackedObjectives().add(objective);
        // The client may already know this objective in singleplayer; remove first so the ADD is always safe.
        connection.send(new ClientboundSetObjectivePacket(objective, ClientboundSetObjectivePacket.METHOD_REMOVE));
        connection.send(packet);
    }
}
