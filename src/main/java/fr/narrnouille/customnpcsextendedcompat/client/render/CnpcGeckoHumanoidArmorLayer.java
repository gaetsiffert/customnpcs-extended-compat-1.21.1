package fr.narrnouille.customnpcsextendedcompat.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.client.ClientHooks;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Locale;
import java.util.Set;

public class CnpcGeckoHumanoidArmorLayer<T extends LivingEntity & GeoAnimatable> extends GeoRenderLayer<T> {
    private static final Set<String> HEAD_BONES = Set.of("head", "helmet", "hat");
    private static final Set<String> BODY_BONES = Set.of("body", "torso", "chest", "upper_body", "upperbody");
    private static final Set<String> RIGHT_ARM_BONES = Set.of("right_arm", "rightarm", "arm_right", "rightupperarm");
    private static final Set<String> LEFT_ARM_BONES = Set.of("left_arm", "leftarm", "arm_left", "leftupperarm");
    private static final Set<String> RIGHT_LEG_BONES = Set.of("right_leg", "rightleg", "leg_right", "rightthigh");
    private static final Set<String> LEFT_LEG_BONES = Set.of("left_leg", "leftleg", "leg_left", "leftthigh");
    private static final Set<String> RIGHT_BOOT_BONES = Set.of("right_boot", "rightboot", "right_foot", "rightfoot", "right_feet", "rightfeet");
    private static final Set<String> LEFT_BOOT_BONES = Set.of("left_boot", "leftboot", "left_foot", "leftfoot", "left_feet", "leftfeet");
    private static final HumanoidModel<LivingEntity> INNER_ARMOR_MODEL = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
    private static final HumanoidModel<LivingEntity> OUTER_ARMOR_MODEL = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));

    private ItemStack helmetStack = ItemStack.EMPTY;
    private ItemStack chestplateStack = ItemStack.EMPTY;
    private ItemStack leggingsStack = ItemStack.EMPTY;
    private ItemStack bootsStack = ItemStack.EMPTY;
    private boolean hasRightBootBone;
    private boolean hasLeftBootBone;

    public CnpcGeckoHumanoidArmorLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        this.helmetStack = animatable.getItemBySlot(EquipmentSlot.HEAD);
        this.chestplateStack = animatable.getItemBySlot(EquipmentSlot.CHEST);
        this.leggingsStack = animatable.getItemBySlot(EquipmentSlot.LEGS);
        this.bootsStack = animatable.getItemBySlot(EquipmentSlot.FEET);
        this.hasRightBootBone = hasArmorBone(bakedModel, RIGHT_BOOT_BONES);
        this.hasLeftBootBone = hasArmorBone(bakedModel, LEFT_BOOT_BONES);
    }

    @Override
    public void renderForBone(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        ArmorPart part = getArmorPart(bone);
        switch (part) {
            case HEAD -> {
                renderArmorPart(poseStack, animatable, bone, EquipmentSlot.HEAD, this.helmetStack, OUTER_ARMOR_MODEL, OUTER_ARMOR_MODEL.head, bufferSource, partialTick, packedLight, packedOverlay);
                renderArmorPart(poseStack, animatable, bone, EquipmentSlot.HEAD, this.helmetStack, OUTER_ARMOR_MODEL, OUTER_ARMOR_MODEL.hat, bufferSource, partialTick, packedLight, packedOverlay);
            }
            case BODY -> {
                renderArmorPart(poseStack, animatable, bone, EquipmentSlot.CHEST, this.chestplateStack, OUTER_ARMOR_MODEL, OUTER_ARMOR_MODEL.body, bufferSource, partialTick, packedLight, packedOverlay);
                renderArmorPart(poseStack, animatable, bone, EquipmentSlot.LEGS, this.leggingsStack, INNER_ARMOR_MODEL, INNER_ARMOR_MODEL.body, bufferSource, partialTick, packedLight, packedOverlay);
            }
            case RIGHT_ARM -> renderArmorPart(poseStack, animatable, bone, EquipmentSlot.CHEST, this.chestplateStack, OUTER_ARMOR_MODEL, OUTER_ARMOR_MODEL.rightArm, bufferSource, partialTick, packedLight, packedOverlay);
            case LEFT_ARM -> renderArmorPart(poseStack, animatable, bone, EquipmentSlot.CHEST, this.chestplateStack, OUTER_ARMOR_MODEL, OUTER_ARMOR_MODEL.leftArm, bufferSource, partialTick, packedLight, packedOverlay);
            case RIGHT_LEG -> {
                renderArmorPart(poseStack, animatable, bone, EquipmentSlot.LEGS, this.leggingsStack, INNER_ARMOR_MODEL, INNER_ARMOR_MODEL.rightLeg, bufferSource, partialTick, packedLight, packedOverlay);
                if (!this.hasRightBootBone) {
                    renderArmorPart(poseStack, animatable, bone, EquipmentSlot.FEET, this.bootsStack, OUTER_ARMOR_MODEL, OUTER_ARMOR_MODEL.rightLeg, bufferSource, partialTick, packedLight, packedOverlay);
                }
            }
            case LEFT_LEG -> {
                renderArmorPart(poseStack, animatable, bone, EquipmentSlot.LEGS, this.leggingsStack, INNER_ARMOR_MODEL, INNER_ARMOR_MODEL.leftLeg, bufferSource, partialTick, packedLight, packedOverlay);
                if (!this.hasLeftBootBone) {
                    renderArmorPart(poseStack, animatable, bone, EquipmentSlot.FEET, this.bootsStack, OUTER_ARMOR_MODEL, OUTER_ARMOR_MODEL.leftLeg, bufferSource, partialTick, packedLight, packedOverlay);
                }
            }
            case RIGHT_BOOT -> renderArmorPart(poseStack, animatable, bone, EquipmentSlot.FEET, this.bootsStack, OUTER_ARMOR_MODEL, OUTER_ARMOR_MODEL.rightLeg, bufferSource, partialTick, packedLight, packedOverlay);
            case LEFT_BOOT -> renderArmorPart(poseStack, animatable, bone, EquipmentSlot.FEET, this.bootsStack, OUTER_ARMOR_MODEL, OUTER_ARMOR_MODEL.leftLeg, bufferSource, partialTick, packedLight, packedOverlay);
            case NONE -> {
            }
        }
    }

    private void renderArmorPart(PoseStack poseStack, T animatable, GeoBone bone, EquipmentSlot slot, ItemStack stack, HumanoidModel<LivingEntity> baseModel, ModelPart modelPart, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem armorItem) || armorItem.getEquipmentSlot() != slot || modelPart.isEmpty()) {
            return;
        }

        HumanoidModel<?> model = getArmorModel(animatable, stack, slot, baseModel);
        ModelPart renderedPart = remapModelPart(model, modelPart, baseModel);
        if (renderedPart.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.501F, 0.0F);
        prepModelPartForBone(modelPart, renderedPart);
        renderVanillaArmorPiece(poseStack, animatable, bone, slot, stack, renderedPart, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private static <E extends LivingEntity & GeoAnimatable> HumanoidModel<?> getArmorModel(E animatable, ItemStack stack, EquipmentSlot slot, HumanoidModel<LivingEntity> baseModel) {
        Model model = ClientHooks.getArmorModel(animatable, stack, slot, GeckoLibServices.Client.ITEM_RENDERING.getArmorModelForItem(animatable, stack, slot, baseModel));
        return model instanceof HumanoidModel<?> humanoidModel ? humanoidModel : baseModel;
    }

    private static ModelPart remapModelPart(HumanoidModel<?> model, ModelPart originalPart, HumanoidModel<LivingEntity> originalModel) {
        if (originalPart == originalModel.head) {
            return model.head;
        }
        if (originalPart == originalModel.hat) {
            return model.hat;
        }
        if (originalPart == originalModel.body) {
            return model.body;
        }
        if (originalPart == originalModel.rightArm) {
            return model.rightArm;
        }
        if (originalPart == originalModel.leftArm) {
            return model.leftArm;
        }
        if (originalPart == originalModel.rightLeg) {
            return model.rightLeg;
        }
        if (originalPart == originalModel.leftLeg) {
            return model.leftLeg;
        }
        return originalPart;
    }

    private static void prepModelPartForBone(ModelPart sourcePart, ModelPart renderedPart) {
        renderedPart.visible = true;
        resetVanillaPartPosition(sourcePart, renderedPart);
        renderedPart.setRotation(0.0F, 0.0F, 0.0F);
        renderedPart.xScale = 1.0F;
        renderedPart.yScale = 1.0F;
        renderedPart.zScale = 1.0F;
    }

    private static void resetVanillaPartPosition(ModelPart sourcePart, ModelPart renderedPart) {
        if (isRightArm(sourcePart)) {
            renderedPart.setPos(-5.0F, 2.0F, 0.0F);
        } else if (isLeftArm(sourcePart)) {
            renderedPart.setPos(5.0F, 2.0F, 0.0F);
        } else if (isRightLeg(sourcePart)) {
            renderedPart.setPos(-1.9F, 12.0F, 0.0F);
        } else if (isLeftLeg(sourcePart)) {
            renderedPart.setPos(1.9F, 12.0F, 0.0F);
        } else {
            renderedPart.setPos(0.0F, 0.0F, 0.0F);
        }
    }

    private static boolean isRightArm(ModelPart modelPart) {
        return modelPart == OUTER_ARMOR_MODEL.rightArm || modelPart == INNER_ARMOR_MODEL.rightArm;
    }

    private static boolean isLeftArm(ModelPart modelPart) {
        return modelPart == OUTER_ARMOR_MODEL.leftArm || modelPart == INNER_ARMOR_MODEL.leftArm;
    }

    private static boolean isRightLeg(ModelPart modelPart) {
        return modelPart == OUTER_ARMOR_MODEL.rightLeg || modelPart == INNER_ARMOR_MODEL.rightLeg;
    }

    private static boolean isLeftLeg(ModelPart modelPart) {
        return modelPart == OUTER_ARMOR_MODEL.leftLeg || modelPart == INNER_ARMOR_MODEL.leftLeg;
    }

    private void renderVanillaArmorPiece(PoseStack poseStack, T animatable, GeoBone bone, EquipmentSlot slot, ItemStack stack, ModelPart modelPart, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Holder<ArmorMaterial> material = ((ArmorItem) stack.getItem()).getMaterial();
        for (ArmorMaterial.Layer layer : material.value().layers()) {
            int color = stack.is(ItemTags.DYEABLE) ? DyedItemColor.getOrDefault(stack, -6265536) : -1;
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.armorCutoutNoCull(layer.texture(slot == EquipmentSlot.LEGS)));
            modelPart.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }

        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim != null) {
            TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET).getSprite(slot == EquipmentSlot.LEGS ? trim.innerTexture(material) : trim.outerTexture(material));
            VertexConsumer vertexConsumer = sprite.wrap(bufferSource.getBuffer(Sheets.armorTrimsSheet(trim.pattern().value().decal())));
            modelPart.render(poseStack, vertexConsumer, packedLight, packedOverlay);
        }

        if (stack.hasFoil()) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.armorEntityGlint());
            modelPart.render(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }

    private static boolean hasArmorBone(BakedGeoModel model, Set<String> boneNames) {
        for (GeoBone bone : model.topLevelBones()) {
            if (hasArmorBone(bone, boneNames)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasArmorBone(GeoBone bone, Set<String> boneNames) {
        if (boneNames.contains(normalize(bone.getName()))) {
            return true;
        }

        for (GeoBone child : bone.getChildBones()) {
            if (hasArmorBone(child, boneNames)) {
                return true;
            }
        }
        return false;
    }

    private static ArmorPart getArmorPart(GeoBone bone) {
        String name = normalize(bone.getName());
        if (HEAD_BONES.contains(name)) {
            return ArmorPart.HEAD;
        }
        if (BODY_BONES.contains(name)) {
            return ArmorPart.BODY;
        }
        if (RIGHT_ARM_BONES.contains(name)) {
            return ArmorPart.RIGHT_ARM;
        }
        if (LEFT_ARM_BONES.contains(name)) {
            return ArmorPart.LEFT_ARM;
        }
        if (RIGHT_LEG_BONES.contains(name)) {
            return ArmorPart.RIGHT_LEG;
        }
        if (LEFT_LEG_BONES.contains(name)) {
            return ArmorPart.LEFT_LEG;
        }
        if (RIGHT_BOOT_BONES.contains(name)) {
            return ArmorPart.RIGHT_BOOT;
        }
        if (LEFT_BOOT_BONES.contains(name)) {
            return ArmorPart.LEFT_BOOT;
        }
        return ArmorPart.NONE;
    }

    private static String normalize(String name) {
        return name.toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
    }

    private enum ArmorPart {
        NONE,
        HEAD,
        BODY,
        RIGHT_ARM,
        LEFT_ARM,
        RIGHT_LEG,
        LEFT_LEG,
        RIGHT_BOOT,
        LEFT_BOOT
    }
}
