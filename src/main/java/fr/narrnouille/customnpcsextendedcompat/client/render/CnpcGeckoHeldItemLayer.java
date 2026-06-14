package fr.narrnouille.customnpcsextendedcompat.client.render;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CnpcGeckoHeldItemLayer<T extends LivingEntity & GeoAnimatable> extends GeoRenderLayer<T> {
    private static final String RIGHT_HELD_ITEM_BONE = "held_item";
    private static final String RIGHT_HELD_ITEM_LOCATOR = "held_item_locator";
    private static final String LEFT_HELD_ITEM_BONE = "left_held_item";
    private static final String LEFT_HELD_ITEM_LOCATOR = "left_held_item_locator";
    private static final double MODEL_UNIT = 16.0D;
    private static final float RIGHT_ITEM_ANCHOR_X_ROTATION = -90.0F;
    private static final float RIGHT_ITEM_ANCHOR_Y_ROTATION = 0.0F;
    private static final float RIGHT_ITEM_ANCHOR_Z_ROTATION = 0.0F;
    private static final float LEFT_ITEM_ANCHOR_X_ROTATION = -90.0F;
    private static final float LEFT_ITEM_ANCHOR_Y_ROTATION = 0.0F;
    private static final float LEFT_ITEM_ANCHOR_Z_ROTATION = 0.0F;
    private static final double RIGHT_ITEM_ANCHOR_X_OFFSET = 0.0D;
    private static final double RIGHT_ITEM_ANCHOR_Y_OFFSET = 0.125D;
    private static final double RIGHT_ITEM_ANCHOR_Z_OFFSET = -0.125D;
    private static final double LEFT_ITEM_ANCHOR_X_OFFSET = 0.0D;
    private static final double LEFT_ITEM_ANCHOR_Y_OFFSET = 0.125D;
    private static final double LEFT_ITEM_ANCHOR_Z_OFFSET = -0.125D;
    private static final Map<ResourceLocation, LocatorData> LOCATOR_CACHE = new ConcurrentHashMap<>();

    public CnpcGeckoHeldItemLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    public void renderForBone(
            PoseStack poseStack,
            T animatable,
            GeoBone bone,
            RenderType renderType,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            float partialTick,
            int packedLight,
            int packedOverlay
    ) {
        String boneName = bone.getName();
        LocatorData locatorData = getLocatorData(animatable);
        LocatorTransform rightLocator = locatorData.right();
        LocatorTransform leftLocator = locatorData.left();

        boolean leftHand = leftLocator.matchesBone(boneName) || (leftLocator.missing() && LEFT_HELD_ITEM_BONE.equals(boneName));
        boolean rightHand = rightLocator.matchesBone(boneName) || (rightLocator.missing() && RIGHT_HELD_ITEM_BONE.equals(boneName));
        if (!leftHand && !rightHand) {
            return;
        }

        ItemStack stack = leftHand ? animatable.getOffhandItem() : animatable.getMainHandItem();
        if (stack.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        RenderUtil.translateToPivotPoint(poseStack, bone);
        LocatorTransform locator = leftHand ? leftLocator : rightLocator;
        if (!locator.missing()) {
            locator.apply(poseStack);
        }
        applyItemAnchor(leftHand, poseStack);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                animatable,
                stack,
                leftHand ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                leftHand,
                poseStack,
                bufferSource,
                animatable.level(),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                0
        );
        poseStack.popPose();
    }

    private static void applyItemAnchor(boolean leftHand, PoseStack poseStack) {
        poseStack.mulPose(Axis.XP.rotationDegrees(leftHand ? LEFT_ITEM_ANCHOR_X_ROTATION : RIGHT_ITEM_ANCHOR_X_ROTATION));
        poseStack.mulPose(Axis.YP.rotationDegrees(leftHand ? LEFT_ITEM_ANCHOR_Y_ROTATION : RIGHT_ITEM_ANCHOR_Y_ROTATION));
        poseStack.mulPose(Axis.ZP.rotationDegrees(leftHand ? LEFT_ITEM_ANCHOR_Z_ROTATION : RIGHT_ITEM_ANCHOR_Z_ROTATION));
        poseStack.translate(
                leftHand ? LEFT_ITEM_ANCHOR_X_OFFSET : RIGHT_ITEM_ANCHOR_X_OFFSET,
                leftHand ? LEFT_ITEM_ANCHOR_Y_OFFSET : RIGHT_ITEM_ANCHOR_Y_OFFSET,
                leftHand ? LEFT_ITEM_ANCHOR_Z_OFFSET : RIGHT_ITEM_ANCHOR_Z_OFFSET
        );
    }

    private LocatorData getLocatorData(T animatable) {
        ResourceLocation modelLocation = getGeoModel().getModelResource(animatable);
        if (modelLocation == null) {
            return LocatorData.EMPTY;
        }
        return LOCATOR_CACHE.computeIfAbsent(modelLocation, CnpcGeckoHeldItemLayer::loadLocatorData);
    }

    private static LocatorData loadLocatorData(ResourceLocation modelLocation) {
        Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(modelLocation);
        if (resource.isEmpty()) {
            return LocatorData.EMPTY;
        }

        try (BufferedReader reader = resource.get().openAsReader()) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonElement geometryElement = root.get("minecraft:geometry");
            if (geometryElement == null || !geometryElement.isJsonArray() || geometryElement.getAsJsonArray().isEmpty()) {
                return LocatorData.EMPTY;
            }

            JsonObject geometry = geometryElement.getAsJsonArray().get(0).getAsJsonObject();
            JsonElement bonesElement = geometry.get("bones");
            if (bonesElement == null || !bonesElement.isJsonArray()) {
                return LocatorData.EMPTY;
            }

            LocatorTransform right = LocatorTransform.MISSING;
            LocatorTransform left = LocatorTransform.MISSING;
            for (JsonElement boneElement : bonesElement.getAsJsonArray()) {
                if (!boneElement.isJsonObject()) {
                    continue;
                }

                JsonObject bone = boneElement.getAsJsonObject();
                JsonElement nameElement = bone.get("name");
                JsonElement locatorsElement = bone.get("locators");
                if (nameElement == null || locatorsElement == null || !locatorsElement.isJsonObject()) {
                    continue;
                }

                String boneName = nameElement.getAsString();
                JsonObject locators = locatorsElement.getAsJsonObject();
                if (locators.has(RIGHT_HELD_ITEM_LOCATOR)) {
                    right = readLocator(boneName, locators.get(RIGHT_HELD_ITEM_LOCATOR));
                }
                if (locators.has(LEFT_HELD_ITEM_LOCATOR)) {
                    left = readLocator(boneName, locators.get(LEFT_HELD_ITEM_LOCATOR));
                }
            }
            return new LocatorData(right, left);
        } catch (IOException | IllegalStateException ignored) {
            return LocatorData.EMPTY;
        }
    }

    private static LocatorTransform readLocator(String boneName, JsonElement locatorElement) {
        double[] offset = null;
        double[] rotation = null;

        if (locatorElement.isJsonArray()) {
            offset = readArray(locatorElement);
        } else if (locatorElement.isJsonObject()) {
            JsonObject locator = locatorElement.getAsJsonObject();
            if (locator.has("offset")) {
                offset = readArray(locator.get("offset"));
            }
            if (locator.has("rotation")) {
                rotation = readArray(locator.get("rotation"));
            }
        }

        return new LocatorTransform(boneName, offset, rotation);
    }

    private static double[] readArray(JsonElement element) {
        if (element == null || !element.isJsonArray()) {
            return null;
        }

        double[] values = new double[Math.min(3, element.getAsJsonArray().size())];
        for (int i = 0; i < values.length; i++) {
            values[i] = element.getAsJsonArray().get(i).getAsDouble();
        }
        return values;
    }

    private record LocatorData(LocatorTransform right, LocatorTransform left) {
        private static final LocatorData EMPTY = new LocatorData(LocatorTransform.MISSING, LocatorTransform.MISSING);
    }

    private record LocatorTransform(String boneName, double[] offset, double[] rotation) {
        private static final LocatorTransform MISSING = new LocatorTransform(null, null, null);

        private boolean missing() {
            return this.boneName == null;
        }

        private boolean matchesBone(String boneName) {
            return this.boneName != null && this.boneName.equals(boneName);
        }

        private void apply(PoseStack poseStack) {
            if (this.offset != null && this.offset.length >= 3) {
                poseStack.translate(this.offset[0] / MODEL_UNIT, this.offset[1] / MODEL_UNIT, this.offset[2] / MODEL_UNIT);
            }
            if (this.rotation != null && this.rotation.length >= 3) {
                poseStack.mulPose(Axis.ZP.rotationDegrees((float) this.rotation[2]));
                poseStack.mulPose(Axis.YP.rotationDegrees((float) this.rotation[1]));
                poseStack.mulPose(Axis.XP.rotationDegrees((float) this.rotation[0]));
            }
        }
    }
}
