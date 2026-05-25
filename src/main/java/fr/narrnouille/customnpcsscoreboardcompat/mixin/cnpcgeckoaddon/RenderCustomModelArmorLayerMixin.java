package fr.narrnouille.customnpcsscoreboardcompat.mixin.cnpcgeckoaddon;

import fr.narrnouille.customnpcsscoreboardcompat.client.render.CnpcGeckoHumanoidArmorLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Mixin(targets = "com.goodbird.cnpcgeckoaddon.client.renderer.RenderCustomModel", remap = false)
public abstract class RenderCustomModelArmorLayerMixin {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "<init>", at = @At("TAIL"))
    private void customnpcsScoreboardCompat$addHumanoidArmorLayer(EntityRendererProvider.Context context, CallbackInfo callbackInfo) {
        GeoEntityRenderer renderer = (GeoEntityRenderer) (Object) this;
        renderer.addRenderLayer(new CnpcGeckoHumanoidArmorLayer<>(renderer));
    }
}
