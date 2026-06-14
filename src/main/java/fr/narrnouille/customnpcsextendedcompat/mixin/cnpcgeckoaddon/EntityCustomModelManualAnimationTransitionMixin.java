package fr.narrnouille.customnpcsextendedcompat.mixin.cnpcgeckoaddon;

import fr.narrnouille.customnpcsextendedcompat.compat.cnpcgeckoaddon.EntityCustomModelTransitionBridge;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

@Pseudo
@Mixin(targets = "com.goodbird.cnpcgeckoaddon.entity.EntityCustomModel", remap = false)
public abstract class EntityCustomModelManualAnimationTransitionMixin implements EntityCustomModelTransitionBridge {
    private static final int DEFAULT_MOVEMENT_TRANSITION_TICKS = 10;
    private static final String WAIT_ANIMATION = "internal.wait";

    @Shadow(remap = false)
    public RawAnimation manualAnim;

    @Shadow(remap = false)
    public String idleAnim;

    @Unique
    private int customnpcsExtendedCompat$animationTransitionTicks = DEFAULT_MOVEMENT_TRANSITION_TICKS;

    @Unique
    private RawAnimation customnpcsExtendedCompat$sequenceSource;

    @Unique
    private RawAnimation customnpcsExtendedCompat$activeStageAnimation;

    @Unique
    private int customnpcsExtendedCompat$sequenceIndex;

    @Unique
    private int customnpcsExtendedCompat$waitUntilTick = -1;

    @Unique
    private RawAnimation customnpcsExtendedCompat$idleWaitAnimation;

    @Unique
    private String customnpcsExtendedCompat$idleWaitAnimationName = "";

    @Unique
    private boolean customnpcsExtendedCompat$dialogIdleAnimationEnabled;

    @Unique
    private RawAnimation customnpcsExtendedCompat$dialogIdleAnimation;

    @Unique
    private String customnpcsExtendedCompat$dialogIdleAnimationName = "";

    @Override
    public void customnpcsExtendedCompat$setAnimationTransitionTicks(int transitionTicks) {
        this.customnpcsExtendedCompat$animationTransitionTicks = Math.max(0, transitionTicks);
    }

    @Override
    public void customnpcsExtendedCompat$setDialogIdleAnimationEnabled(boolean enabled) {
        this.customnpcsExtendedCompat$dialogIdleAnimationEnabled = enabled;
    }

    @Override
    public void customnpcsExtendedCompat$stopManualAnimation() {
        this.manualAnim = null;
        this.customnpcsExtendedCompat$resetManualSequence();
    }

    @Inject(method = "predicateMovement", at = @At("HEAD"), cancellable = true, remap = false)
    private void customnpcs_extended_compat$useConfiguredAnimationTransition(
            AnimationState<?> animationState,
            CallbackInfoReturnable<PlayState> callbackInfo
    ) {
        animationState.getController().transitionLength(this.customnpcsExtendedCompat$animationTransitionTicks);

        if (this.manualAnim == null) {
            this.customnpcsExtendedCompat$resetManualSequence();
            return;
        }

        if (this.manualAnim.getStageCount() <= 1) {
            this.customnpcsExtendedCompat$resetManualSequence();
            return;
        }

        if (this.customnpcsExtendedCompat$playManualSequence(animationState)) {
            callbackInfo.setReturnValue(PlayState.CONTINUE);
        }
    }

    @Inject(
            method = "predicateMovement",
            at = @At(value = "INVOKE", target = "Lsoftware/bernie/geckolib/animation/AnimationState;getLimbSwingAmount()F", ordinal = 0),
            cancellable = true,
            remap = false
    )
    private void customnpcsExtendedCompat$useDialogIdleInsteadOfWalk(
            AnimationState<?> animationState,
            CallbackInfoReturnable<PlayState> callbackInfo
    ) {
        if (!this.customnpcsExtendedCompat$dialogIdleAnimationEnabled) {
            return;
        }

        if (this.idleAnim == null || this.idleAnim.isEmpty()) {
            callbackInfo.setReturnValue(PlayState.STOP);
            return;
        }

        this.customnpcsExtendedCompat$playDialogIdle(animationState.getController());
        callbackInfo.setReturnValue(PlayState.CONTINUE);
    }

    @Unique
    private void customnpcsExtendedCompat$playDialogIdle(AnimationController<?> controller) {
        RawAnimation animation = this.customnpcsExtendedCompat$getDialogIdleAnimation();
        if (controller.getCurrentRawAnimation() != animation) {
            controller.forceAnimationReset();
        }
        controller.setAnimation(animation);
    }

    @Unique
    private RawAnimation customnpcsExtendedCompat$getDialogIdleAnimation() {
        if (this.customnpcsExtendedCompat$dialogIdleAnimation == null
                || !this.idleAnim.equals(this.customnpcsExtendedCompat$dialogIdleAnimationName)) {
            this.customnpcsExtendedCompat$dialogIdleAnimationName = this.idleAnim;
            this.customnpcsExtendedCompat$dialogIdleAnimation = RawAnimation.begin().thenLoop(this.idleAnim);
        }

        return this.customnpcsExtendedCompat$dialogIdleAnimation;
    }

    @Unique
    private boolean customnpcsExtendedCompat$playManualSequence(AnimationState<?> animationState) {
        if (this.customnpcsExtendedCompat$sequenceSource != this.manualAnim) {
            this.customnpcsExtendedCompat$sequenceSource = this.manualAnim;
            this.customnpcsExtendedCompat$sequenceIndex = 0;
            this.customnpcsExtendedCompat$activeStageAnimation = null;
            this.customnpcsExtendedCompat$waitUntilTick = -1;
        }

        AnimationController<?> controller = animationState.getController();
        int currentTick = ((Entity) (Object) this).tickCount;
        while (this.customnpcsExtendedCompat$sequenceIndex < this.customnpcsExtendedCompat$sequenceSource.getStageCount()) {
            RawAnimation.Stage stage = this.customnpcsExtendedCompat$sequenceSource.getAnimationStages()
                    .get(this.customnpcsExtendedCompat$sequenceIndex);

            if (WAIT_ANIMATION.equals(stage.animationName())) {
                if (this.customnpcsExtendedCompat$handleIdleWaitStage(stage, controller, currentTick)) {
                    return true;
                }
                continue;
            }

            RawAnimation.Stage nextStage = this.customnpcsExtendedCompat$getStage(this.customnpcsExtendedCompat$sequenceIndex + 1);
            if (nextStage != null
                    && WAIT_ANIMATION.equals(nextStage.animationName())
                    && this.customnpcsExtendedCompat$handlesWaitWithCurrentStage(stage)) {
                if (this.customnpcsExtendedCompat$handleAnimationBeforeWait(stage, nextStage, controller, currentTick)) {
                    return true;
                }
                continue;
            }

            if (this.customnpcsExtendedCompat$activeStageAnimation == null) {
                this.customnpcsExtendedCompat$activeStageAnimation = RawAnimation.begin()
                        .then(stage.animationName(), stage.loopType());
                controller.forceAnimationReset();
            }

            if (controller.getCurrentRawAnimation() == this.customnpcsExtendedCompat$activeStageAnimation
                    && controller.getAnimationState() == AnimationController.State.STOPPED) {
                this.customnpcsExtendedCompat$sequenceIndex++;
                this.customnpcsExtendedCompat$activeStageAnimation = null;
                continue;
            }

            controller.setAnimation(this.customnpcsExtendedCompat$activeStageAnimation);
            return true;
        }

        this.manualAnim = null;
        this.customnpcsExtendedCompat$resetManualSequence();
        if (this.customnpcsExtendedCompat$dialogIdleAnimationEnabled
                && this.idleAnim != null
                && !this.idleAnim.isEmpty()) {
            this.customnpcsExtendedCompat$playDialogIdle(controller);
            return true;
        }
        controller.forceAnimationReset();
        return false;
    }

    @Unique
    private boolean customnpcsExtendedCompat$handleAnimationBeforeWait(
            RawAnimation.Stage stage,
            RawAnimation.Stage waitStage,
            AnimationController<?> controller,
            int currentTick
    ) {
        if (stage.loopType() == Animation.LoopType.LOOP) {
            return this.customnpcsExtendedCompat$handleLoopDuringWait(stage, waitStage, controller, currentTick);
        }

        if (stage.loopType() == Animation.LoopType.HOLD_ON_LAST_FRAME) {
            return this.customnpcsExtendedCompat$handleHoldDuringWait(stage, waitStage, controller, currentTick);
        }

        return false;
    }

    @Unique
    private boolean customnpcsExtendedCompat$handleLoopDuringWait(
            RawAnimation.Stage stage,
            RawAnimation.Stage waitStage,
            AnimationController<?> controller,
            int currentTick
    ) {
        if (this.customnpcsExtendedCompat$activeStageAnimation == null) {
            this.customnpcsExtendedCompat$activeStageAnimation = RawAnimation.begin()
                    .then(stage.animationName(), stage.loopType());
            this.customnpcsExtendedCompat$waitUntilTick = currentTick + Math.max(0, waitStage.additionalTicks());
            controller.forceAnimationReset();
        }

        if (currentTick >= this.customnpcsExtendedCompat$waitUntilTick) {
            this.customnpcsExtendedCompat$finishWaitPair();
            controller.forceAnimationReset();
            return false;
        }

        controller.setAnimation(this.customnpcsExtendedCompat$activeStageAnimation);
        return true;
    }

    @Unique
    private boolean customnpcsExtendedCompat$handleHoldDuringWait(
            RawAnimation.Stage stage,
            RawAnimation.Stage waitStage,
            AnimationController<?> controller,
            int currentTick
    ) {
        if (this.customnpcsExtendedCompat$activeStageAnimation == null) {
            this.customnpcsExtendedCompat$activeStageAnimation = RawAnimation.begin()
                    .then(stage.animationName(), stage.loopType());
            controller.forceAnimationReset();
        }

        if (controller.getCurrentRawAnimation() == this.customnpcsExtendedCompat$activeStageAnimation
                && controller.getAnimationState() == AnimationController.State.PAUSED) {
            if (this.customnpcsExtendedCompat$waitUntilTick < 0) {
                this.customnpcsExtendedCompat$waitUntilTick = currentTick + Math.max(0, waitStage.additionalTicks());
            }

            if (currentTick >= this.customnpcsExtendedCompat$waitUntilTick) {
                this.customnpcsExtendedCompat$finishWaitPair();
                controller.forceAnimationReset();
                return false;
            }
        }

        controller.setAnimation(this.customnpcsExtendedCompat$activeStageAnimation);
        return true;
    }

    @Unique
    private boolean customnpcsExtendedCompat$handleIdleWaitStage(
            RawAnimation.Stage stage,
            AnimationController<?> controller,
            int currentTick
    ) {
        this.customnpcsExtendedCompat$activeStageAnimation = null;
        if (this.customnpcsExtendedCompat$waitUntilTick < 0) {
            this.customnpcsExtendedCompat$waitUntilTick = currentTick + Math.max(0, stage.additionalTicks());
        }

        if (currentTick < this.customnpcsExtendedCompat$waitUntilTick) {
            this.customnpcsExtendedCompat$playIdleDuringWait(controller);
            return true;
        }

        this.customnpcsExtendedCompat$waitUntilTick = -1;
        this.customnpcsExtendedCompat$sequenceIndex++;
        return false;
    }

    @Unique
    private void customnpcsExtendedCompat$playIdleDuringWait(AnimationController<?> controller) {
        if (this.idleAnim == null || this.idleAnim.isEmpty()) {
            return;
        }

        if (this.customnpcsExtendedCompat$idleWaitAnimation == null
                || !this.idleAnim.equals(this.customnpcsExtendedCompat$idleWaitAnimationName)) {
            this.customnpcsExtendedCompat$idleWaitAnimationName = this.idleAnim;
            this.customnpcsExtendedCompat$idleWaitAnimation = RawAnimation.begin().thenLoop(this.idleAnim);
            controller.forceAnimationReset();
        }

        controller.setAnimation(this.customnpcsExtendedCompat$idleWaitAnimation);
    }

    @Unique
    private boolean customnpcsExtendedCompat$handlesWaitWithCurrentStage(RawAnimation.Stage stage) {
        return stage.loopType() == Animation.LoopType.LOOP
                || stage.loopType() == Animation.LoopType.HOLD_ON_LAST_FRAME;
    }

    @Unique
    private RawAnimation.Stage customnpcsExtendedCompat$getStage(int index) {
        if (this.customnpcsExtendedCompat$sequenceSource == null
                || index < 0
                || index >= this.customnpcsExtendedCompat$sequenceSource.getStageCount()) {
            return null;
        }

        return this.customnpcsExtendedCompat$sequenceSource.getAnimationStages().get(index);
    }

    @Unique
    private void customnpcsExtendedCompat$finishWaitPair() {
        this.customnpcsExtendedCompat$waitUntilTick = -1;
        this.customnpcsExtendedCompat$sequenceIndex += 2;
        this.customnpcsExtendedCompat$activeStageAnimation = null;
    }

    @Unique
    private void customnpcsExtendedCompat$resetManualSequence() {
        this.customnpcsExtendedCompat$sequenceSource = null;
        this.customnpcsExtendedCompat$activeStageAnimation = null;
        this.customnpcsExtendedCompat$sequenceIndex = 0;
        this.customnpcsExtendedCompat$waitUntilTick = -1;
    }
}
