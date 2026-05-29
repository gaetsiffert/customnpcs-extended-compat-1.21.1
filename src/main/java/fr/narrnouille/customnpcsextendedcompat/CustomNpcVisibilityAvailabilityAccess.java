package fr.narrnouille.customnpcsextendedcompat;

import noppes.npcs.api.handler.data.IAvailability;

public interface CustomNpcVisibilityAvailabilityAccess {
    IAvailability getVisibilityAvailability();

    void refreshVisibility();
}
