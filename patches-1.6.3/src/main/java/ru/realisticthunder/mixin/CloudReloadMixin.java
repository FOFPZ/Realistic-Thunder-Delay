package ru.realisticthunder.mixin;
import java.util.Optional;
import net.minecraft.client.render.CloudRenderer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.realisticthunder.CloudMask;
@Mixin(CloudRenderer.class)
public class CloudReloadMixin {
    @Inject(method="apply(Ljava/util/Optional;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",at=@At("TAIL"))
    private void thunderCloud$invalidate(Optional<?> cells,ResourceManager manager,Profiler profiler,CallbackInfo ci){ CloudMask.invalidate(); }
}
