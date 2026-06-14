package fr.narrnouille.customnpcsextendedcompat.compat.customnpcs;

import noppes.npcs.constants.EnumAvailabilityScoreboard;

public interface CustomAvailabilityScoreboardBridge {
    String customnpcsExtendedCompat$getScoreboard3Objective();

    void customnpcsExtendedCompat$setScoreboard3Objective(String objective);

    EnumAvailabilityScoreboard customnpcsExtendedCompat$getScoreboard3Type();

    void customnpcsExtendedCompat$setScoreboard3Type(EnumAvailabilityScoreboard type);

    int customnpcsExtendedCompat$getScoreboard3Value();

    void customnpcsExtendedCompat$setScoreboard3Value(int value);
}
