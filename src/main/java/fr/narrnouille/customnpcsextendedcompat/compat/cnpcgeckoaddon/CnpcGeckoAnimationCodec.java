package fr.narrnouille.customnpcsextendedcompat.compat.cnpcgeckoaddon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.RawAnimation;

public final class CnpcGeckoAnimationCodec {
    private static final String ANIMATIONS_TAG = "anims";
    private static final String NAME_TAG = "name";
    private static final String LOOP_TAG = "loop";
    private static final String ADDITIONAL_TICKS_TAG = "ticks";
    private static final String WAIT_ANIMATION = "internal.wait";

    private CnpcGeckoAnimationCodec() {
    }

    public static void encode(FriendlyByteBuf buffer, RawAnimation animation) {
        CompoundTag root = new CompoundTag();
        ListTag stages = new ListTag();

        for (RawAnimation.Stage stage : animation.getAnimationStages()) {
            CompoundTag stageTag = new CompoundTag();
            stageTag.putString(NAME_TAG, stage.animationName());
            stageTag.putString(LOOP_TAG, getLoopTypeId(stage.loopType()));
            stageTag.putInt(ADDITIONAL_TICKS_TAG, stage.additionalTicks());
            stages.add(stageTag);
        }

        root.put(ANIMATIONS_TAG, stages);
        buffer.writeNbt(root);
    }

    public static RawAnimation decode(FriendlyByteBuf buffer) {
        RawAnimation animation = RawAnimation.begin();
        CompoundTag root = buffer.readNbt();
        if (root == null) {
            return animation;
        }

        ListTag stages = root.getList(ANIMATIONS_TAG, CompoundTag.TAG_COMPOUND);
        for (int index = 0; index < stages.size(); index++) {
            CompoundTag stageTag = stages.getCompound(index);
            String animationName = stageTag.getString(NAME_TAG);
            if (WAIT_ANIMATION.equals(animationName)) {
                animation.thenWait(stageTag.getInt(ADDITIONAL_TICKS_TAG));
                continue;
            }

            animation.then(animationName, Animation.LoopType.fromString(stageTag.getString(LOOP_TAG)));
        }
        return animation;
    }

    private static String getLoopTypeId(Animation.LoopType loopType) {
        if (loopType == null) {
            return Animation.LoopType.PLAY_ONCE.getId();
        }

        try {
            return loopType.getId();
        } catch (IllegalStateException ignored) {
            return Animation.LoopType.PLAY_ONCE.getId();
        }
    }
}
