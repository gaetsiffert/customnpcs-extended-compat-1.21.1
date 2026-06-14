package fr.narrnouille.customnpcsextendedcompat.client.render;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Matrix4f;

public final class CustomNpcNameTagDepthRenderer {
    private static final RenderType DEPTH_WRITING_NAME_TAG_BACKGROUND = RenderType.create(
            "customnpcs_extended_compat_name_tag_background_depth_write",
            DefaultVertexFormat.POSITION_COLOR_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_TEXT_BACKGROUND_SHADER)
                    .setTextureState(RenderStateShard.NO_TEXTURE)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .createCompositeState(false)
    );
    private static final ByteBufferBuilder BUFFER = new ByteBufferBuilder(1536);
    private static final List<Background> BACKGROUNDS = new ArrayList<>();
    private static final List<Text> TEXTS = new ArrayList<>();

    private CustomNpcNameTagDepthRenderer() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(CustomNpcNameTagDepthRenderer::onRenderLevelStage);
    }

    public static boolean shouldDeferDepthWritingNameTags() {
        return ModList.get().isLoaded("iris") || ModList.get().isLoaded("oculus");
    }

    public static void enqueueNameTag(
            Font font,
            Component component,
            float x,
            float y,
            int color,
            boolean dropShadow,
            Matrix4f matrix,
            Font.DisplayMode displayMode,
            int backgroundColor,
            int packedLight
    ) {
        if (backgroundColor != 0) {
            enqueueBackground(font, component, x, y, matrix, backgroundColor, packedLight);
        }

        if (displayMode != Font.DisplayMode.SEE_THROUGH) {
            enqueueText(font, component, x, y, color, dropShadow, matrix, displayMode, packedLight);
        }
    }

    private static void enqueueBackground(Font font, Component component, float x, float y, Matrix4f matrix, int backgroundColor, int packedLight) {
        int alpha = backgroundColor >>> 24;
        if (alpha <= 0) {
            return;
        }

        float width = font.width(component);
        BACKGROUNDS.add(new Background(
                new Matrix4f(matrix),
                x - 1.0F,
                x + width + 1.0F,
                y - 1.0F,
                y + 9.0F,
                -0.01F,
                alpha / 255.0F,
                packedLight
        ));
    }

    private static void enqueueText(
            Font font,
            Component component,
            float x,
            float y,
            int color,
            boolean dropShadow,
            Matrix4f matrix,
            Font.DisplayMode displayMode,
            int packedLight
    ) {
        TEXTS.add(new Text(font, component, x, y, color, dropShadow, new Matrix4f(matrix), displayMode, packedLight));
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            renderQueued();
            return;
        }

        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            clearQueued();
        }
    }

    private static void renderQueued() {
        if (BACKGROUNDS.isEmpty() && TEXTS.isEmpty()) {
            return;
        }

        MultiBufferSource.BufferSource buffer = MultiBufferSource.immediate(BUFFER);
        VertexConsumer backgroundBuffer = buffer.getBuffer(DEPTH_WRITING_NAME_TAG_BACKGROUND);
        for (Background background : BACKGROUNDS) {
            background.render(backgroundBuffer);
        }
        buffer.endBatch(DEPTH_WRITING_NAME_TAG_BACKGROUND);

        for (Text text : TEXTS) {
            text.render(buffer);
        }
        buffer.endBatch();
        clearQueued();
    }

    private static void clearQueued() {
        BACKGROUNDS.clear();
        TEXTS.clear();
    }

    private record Background(Matrix4f matrix, float left, float right, float top, float bottom, float z, float alpha,
                              int packedLight) {
        private void render(VertexConsumer vertexConsumer) {
            vertexConsumer.addVertex(matrix, left, bottom, z).setColor(0.0F, 0.0F, 0.0F, alpha).setLight(packedLight);
            vertexConsumer.addVertex(matrix, right, bottom, z).setColor(0.0F, 0.0F, 0.0F, alpha).setLight(packedLight);
            vertexConsumer.addVertex(matrix, right, top, z).setColor(0.0F, 0.0F, 0.0F, alpha).setLight(packedLight);
            vertexConsumer.addVertex(matrix, left, top, z).setColor(0.0F, 0.0F, 0.0F, alpha).setLight(packedLight);
        }
    }

    private record Text(
            Font font,
            Component component,
            float x,
            float y,
            int color,
            boolean dropShadow,
            Matrix4f matrix,
            Font.DisplayMode displayMode,
            int packedLight
    ) {
        private void render(MultiBufferSource buffer) {
            font.drawInBatch(component, x, y, color, dropShadow, matrix, buffer, displayMode, 0, packedLight);
        }
    }
}
