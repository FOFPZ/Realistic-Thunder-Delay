package ru.realisticthunder.mixin;
import net.minecraft.client.render.WeatherRendering;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WeatherRendering.class)
public class WeatherMixin {
    // Keep vanilla rain particles; replace only the rain sound.
    @Redirect(method="addParticlesAndSound", at=@At(value="INVOKE", target="Lnet/minecraft/client/world/ClientWorld;playSoundAtBlockCenterClient(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"))
    private void suppressRain(ClientWorld world, BlockPos pos, SoundEvent event, SoundCategory category, float volume, float pitch, boolean distance) { }
}
