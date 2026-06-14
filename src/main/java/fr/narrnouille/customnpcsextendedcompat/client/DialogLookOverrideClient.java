package fr.narrnouille.customnpcsextendedcompat.client;

import fr.narrnouille.customnpcsextendedcompat.CustomNpcsExtendedCompatMod;
import fr.narrnouille.customnpcsextendedcompat.network.ClientboundDialogLookPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@EventBusSubscriber(modid = CustomNpcsExtendedCompatMod.MODID, value = Dist.CLIENT)
public final class DialogLookOverrideClient {
    private static final int PLAYER_FOCUS_CLEAR_TIMEOUT_TICKS = 20;
    private static final Map<Integer, Override> OVERRIDES = new HashMap<>();
    private static final Map<Integer, Override> RENDERED_ENTITY_OVERRIDES = new HashMap<>();
    private static int guiEntityDisplayRenderDepth;

    private DialogLookOverrideClient() {
    }

    public static void handle(ClientboundDialogLookPayload payload) {
        if (payload.mode() == ClientboundDialogLookPayload.MODE_CLEAR) {
            Override override = OVERRIDES.get(payload.npcId());
            if (override != null && override.shouldFinishPlayerFocusBeforeClear()) {
                override.startPendingClear();
                return;
            }

            Override removed = OVERRIDES.remove(payload.npcId());
            if (removed != null) {
                clearRenderedEntityAlias(removed);
            }
            return;
        }

        Override previous = OVERRIDES.get(payload.npcId());
        Override next = new Override(
                payload.mode(),
                new Vec3(payload.x(), payload.y(), payload.z()),
                Math.max(0.0F, payload.speed())
        );
        if (previous != null) {
            next.copyState(previous, payload.mode());
            clearRenderedEntityAlias(previous);
        }
        OVERRIDES.put(payload.npcId(), next);
        applyImmediately(payload.npcId(), next);
    }

    public static void beginGuiEntityDisplayRender() {
        guiEntityDisplayRenderDepth++;
    }

    public static void endGuiEntityDisplayRender() {
        if (guiEntityDisplayRenderDepth > 0) {
            guiEntityDisplayRenderDepth--;
        }
    }

    @SubscribeEvent
    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            OVERRIDES.clear();
            RENDERED_ENTITY_OVERRIDES.clear();
            return;
        }

        Iterator<Map.Entry<Integer, Override>> iterator = OVERRIDES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Override> entry = iterator.next();
            Entity entity = minecraft.level.getEntity(entry.getKey());
            if (!(entity instanceof EntityNPCInterface npc) || !npc.isAlive()) {
                clearRenderedEntityAlias(entry.getValue());
                iterator.remove();
                continue;
            }

            Override override = entry.getValue();
            Vec3 target = override.target(minecraft.player);
            override.tick(npc, target);
            applyLocalState(npc, override);
            LivingEntity renderedEntity = getRenderedEntity(npc);
            updateRenderedEntityAlias(override, renderedEntity == npc ? -1 : renderedEntity.getId());
            if (override.pendingClearComplete()) {
                clearRenderedEntityAlias(override);
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    private static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        if (guiEntityDisplayRenderDepth > 0) {
            return;
        }

        LivingEntity entity = event.getEntity();
        Override override = getRenderOverride(entity);

        if (override == null || !override.initialized) {
            return;
        }

        applyLocalState(entity, override);
    }

    private static Override getRenderOverride(LivingEntity entity) {
        if (entity instanceof EntityNPCInterface npc) {
            return OVERRIDES.get(npc.getId());
        }
        return RENDERED_ENTITY_OVERRIDES.get(entity.getId());
    }

    private static void updateRenderedEntityAlias(Override override, int renderedEntityId) {
        if (override.renderedEntityId == renderedEntityId) {
            if (renderedEntityId != -1) {
                RENDERED_ENTITY_OVERRIDES.put(renderedEntityId, override);
            }
            return;
        }

        clearRenderedEntityAlias(override);
        override.renderedEntityId = renderedEntityId;
        if (renderedEntityId != -1) {
            RENDERED_ENTITY_OVERRIDES.put(renderedEntityId, override);
        }
    }

    private static void clearRenderedEntityAlias(Override override) {
        if (override.renderedEntityId == -1) {
            return;
        }

        RENDERED_ENTITY_OVERRIDES.remove(override.renderedEntityId, override);
        override.renderedEntityId = -1;
    }

    private static void applyImmediately(int npcId, Override override) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        Entity entity = minecraft.level.getEntity(npcId);
        if (!(entity instanceof EntityNPCInterface npc) || !npc.isAlive()) {
            return;
        }

        Vec3 target = override.target(minecraft.player);
        override.initialize(npc);
        override.tick(npc, target);
        applyLocalState(npc, override);
    }

    private static void applyLocalState(LivingEntity entity, Override override) {
        applyLocalState(entity, override.bodyYaw, override.headYaw, override.pitch);
        if (!(entity instanceof EntityNPCInterface npc)) {
            return;
        }

        LivingEntity renderedEntity = getRenderedEntity(npc);
        if (renderedEntity != npc) {
            applyLocalState(renderedEntity, override.bodyYaw, override.headYaw, override.pitch);
        }
    }

    private static void applyLocalState(LivingEntity entity, float bodyYaw, float headYaw, float pitch) {
        // Write old/current fields together so renderers that read either field stay on the local state.
        entity.yBodyRotO = bodyYaw;
        entity.yBodyRot = bodyYaw;
        entity.yHeadRotO = headYaw;
        entity.yHeadRot = headYaw;
        entity.xRotO = pitch;
        entity.setXRot(pitch);
    }

    private static LivingEntity getRenderedEntity(EntityNPCInterface npc) {
        if (npc instanceof EntityCustomNpc customNpc && customNpc.modelData != null) {
            LivingEntity modelEntity = customNpc.modelData.getEntity(npc);
            if (modelEntity != null) {
                return modelEntity;
            }
        }
        return npc;
    }

    private static final class Override {
        private static final int BODY_FOLLOW_DELAY_TICKS = 10;
        private static final float BODY_FOLLOW_SPEED_RATIO = 0.75F;
        private static final float TARGET_REACHED_EPSILON = 0.5F;

        private final int mode;
        private final Vec3 position;
        private final float speed;
        private boolean initialized;
        private float bodyYaw;
        private float headYaw;
        private float pitch;
        private int headStableTicks;
        private boolean playerFocusBeforeClear;
        private boolean pendingClear;
        private int pendingClearTicks;
        private boolean targetReached;
        private int renderedEntityId = -1;

        private Override(int mode, Vec3 position, float speed) {
            this.mode = mode;
            this.position = position;
            this.speed = speed;
        }

        private Vec3 target(Player localPlayer) {
            if (this.mode == ClientboundDialogLookPayload.MODE_LOOK_AT_PLAYER) {
                return localPlayer.getEyePosition();
            }
            return this.position;
        }

        private void tick(EntityNPCInterface npc, Vec3 target) {
            initialize(npc);

            double dx = target.x - npc.getX();
            double dz = target.z - npc.getZ();
            double dy = target.y - npc.getEyeY();
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

            float targetYaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
            float maxHeadYaw = npc.getMaxHeadYRot();
            float maxPitch = npc.getMaxHeadXRot();
            float targetPitch = Mth.clamp(
                    (float) -(Mth.atan2(dy, horizontalDistance) * Mth.RAD_TO_DEG),
                    -maxPitch,
                    maxPitch
            );

            this.headYaw = Mth.approachDegrees(this.headYaw, targetYaw, this.speed);
            carryBodyWhenHeadExceedsLimit(maxHeadYaw);
            this.pitch = Mth.approachDegrees(this.pitch, targetPitch, this.speed);
            followHeadWhenStable(targetYaw);
            this.targetReached = Math.abs(Mth.wrapDegrees(targetYaw - this.headYaw)) <= TARGET_REACHED_EPSILON
                    && Math.abs(targetPitch - this.pitch) <= TARGET_REACHED_EPSILON;
            if (this.pendingClear && this.pendingClearTicks > 0) {
                this.pendingClearTicks--;
            }
        }

        private boolean shouldFinishPlayerFocusBeforeClear() {
            return this.playerFocusBeforeClear
                    && this.mode == ClientboundDialogLookPayload.MODE_LOOK_AT_PLAYER
                    && !this.pendingClear;
        }

        private void startPendingClear() {
            this.pendingClear = true;
            this.pendingClearTicks = PLAYER_FOCUS_CLEAR_TIMEOUT_TICKS;
            this.targetReached = false;
        }

        private boolean pendingClearComplete() {
            return this.pendingClear && (this.targetReached || this.pendingClearTicks <= 0);
        }

        private void carryBodyWhenHeadExceedsLimit(float maxHeadYaw) {
            float headDelta = Mth.wrapDegrees(this.headYaw - this.bodyYaw);
            float overflow = Math.abs(headDelta) - maxHeadYaw;
            if (overflow <= 0.0F) {
                return;
            }

            this.bodyYaw = Mth.wrapDegrees(this.bodyYaw + Math.copySign(overflow, headDelta));
            this.headYaw = Mth.rotateIfNecessary(this.headYaw, this.bodyYaw, maxHeadYaw);
        }

        private void followHeadWhenStable(float targetYaw) {
            if (Math.abs(Mth.wrapDegrees(targetYaw - this.headYaw)) > TARGET_REACHED_EPSILON) {
                this.headStableTicks = 0;
                return;
            }

            if (this.headStableTicks < BODY_FOLLOW_DELAY_TICKS) {
                this.headStableTicks++;
                return;
            }

            this.bodyYaw = Mth.approachDegrees(this.bodyYaw, this.headYaw, this.speed * BODY_FOLLOW_SPEED_RATIO);
        }

        private void initialize(EntityNPCInterface npc) {
            if (this.initialized) {
                return;
            }

            this.bodyYaw = npc.yBodyRot;
            this.headYaw = npc.yHeadRot;
            this.pitch = npc.getXRot();
            this.headStableTicks = 0;
            this.initialized = true;
        }

        private void copyState(Override previous, int newMode) {
            this.initialized = previous.initialized;
            this.bodyYaw = previous.bodyYaw;
            this.headYaw = previous.headYaw;
            this.pitch = previous.pitch;
            this.headStableTicks = previous.headStableTicks;
            this.playerFocusBeforeClear = previous.mode == ClientboundDialogLookPayload.MODE_LOOK_AT_POSITION
                    && newMode == ClientboundDialogLookPayload.MODE_LOOK_AT_PLAYER;
            this.pendingClear = false;
            this.pendingClearTicks = 0;
            this.targetReached = false;
        }
    }
}
