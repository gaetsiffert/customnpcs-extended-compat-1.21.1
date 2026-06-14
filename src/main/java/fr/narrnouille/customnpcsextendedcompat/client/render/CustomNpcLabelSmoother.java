package fr.narrnouille.customnpcsextendedcompat.client.render;

import fr.narrnouille.customnpcsextendedcompat.compat.customnpcs.CustomNpcNameTagBridge;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.Map;
import java.util.WeakHashMap;

public final class CustomNpcLabelSmoother {
    private static final Map<EntityNPCInterface, State> STATES = new WeakHashMap<>();
    private static final float NAME_TRANSITION_SECONDS = 0.8F;
    private static final float SNAP_EPSILON = 0.0008F;
    private static final float MAX_DELTA_SECONDS = 0.1F;
    private static final float NANOS_TO_SECONDS = 1.0E-9F;

    private CustomNpcLabelSmoother() {
    }

    public static float getNameTagBaseY(EntityNPCInterface npc, boolean titleVisible) {
        float targetY = CustomNpcNameTagBridge.getNameTagBaseY(npc, titleVisible);
        State state = STATES.computeIfAbsent(npc, ignored -> new State());
        long now = System.nanoTime();
        if (!state.nameInitialized) {
            state.nameY = targetY;
            state.nameStartY = targetY;
            state.nameTargetY = targetY;
            state.nameLastNanos = now;
            state.nameInitialized = true;
            return targetY;
        }

        state.nameY = smooth(state, true, targetY, elapsedSeconds(state.nameLastNanos, now), NAME_TRANSITION_SECONDS);
        state.nameLastNanos = now;
        return state.nameY;
    }

    public static float getMarkY(EntityNPCInterface npc, boolean nameVisible, boolean titleVisible) {
        float targetY = nameVisible
                ? CustomNpcNameTagBridge.getMarkY(npc, titleVisible)
                : CustomNpcNameTagBridge.getStandaloneMarkY(npc);
        State state = STATES.computeIfAbsent(npc, ignored -> new State());
        long now = System.nanoTime();
        if (!state.markInitialized) {
            state.markY = targetY;
            state.markStartY = targetY;
            state.markTargetY = targetY;
            state.markLastNanos = now;
            state.markInitialized = true;
            return targetY;
        }

        state.markY = smooth(
                state,
                false,
                targetY,
                elapsedSeconds(state.markLastNanos, now),
                CustomNpcNameTagBridge.getMarkMovementSeconds(npc.display)
        );
        state.markLastNanos = now;
        return state.markY;
    }

    private static float elapsedSeconds(long previousNanos, long now) {
        if (previousNanos == 0L || now <= previousNanos) {
            return 0.0F;
        }

        return Math.min((now - previousNanos) * NANOS_TO_SECONDS, MAX_DELTA_SECONDS);
    }

    private static float smooth(State state, boolean name, float target, float deltaSeconds, float transitionSeconds) {
        float current = name ? state.nameY : state.markY;
        float previousTarget = name ? state.nameTargetY : state.markTargetY;
        float difference = target - current;
        if (Math.abs(difference) <= SNAP_EPSILON) {
            setTransition(state, name, target, target, target, 1.0F);
            return target;
        }
        if (deltaSeconds <= 0.0F) {
            return current;
        }
        if (transitionSeconds <= 0.0F) {
            setTransition(state, name, target, target, target, 1.0F);
            return target;
        }

        if (Math.abs(target - previousTarget) > SNAP_EPSILON) {
            setTransition(state, name, current, current, target, 0.0F);
        }

        float progress = Math.min((name ? state.nameProgress : state.markProgress) + deltaSeconds / transitionSeconds, 1.0F);
        float start = name ? state.nameStartY : state.markStartY;
        float value = start + (target - start) * progress;
        if (progress >= 1.0F || Math.abs(target - value) <= SNAP_EPSILON) {
            setTransition(state, name, target, target, target, 1.0F);
            return target;
        }

        setTransition(state, name, value, start, target, progress);
        return value;
    }

    private static void setTransition(State state, boolean name, float current, float start, float target, float progress) {
        if (name) {
            state.nameY = current;
            state.nameStartY = start;
            state.nameTargetY = target;
            state.nameProgress = progress;
        } else {
            state.markY = current;
            state.markStartY = start;
            state.markTargetY = target;
            state.markProgress = progress;
        }
    }

    private static final class State {
        private boolean nameInitialized;
        private boolean markInitialized;
        private float nameY;
        private float nameStartY;
        private float nameTargetY;
        private float nameProgress;
        private float markY;
        private float markStartY;
        private float markTargetY;
        private float markProgress;
        private long nameLastNanos;
        private long markLastNanos;
    }
}
