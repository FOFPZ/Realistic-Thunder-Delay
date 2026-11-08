package ru.realisticthunder.mixin;
import net.minecraft.client.render.WeatherRendering;
import net.minecraft.client.render.state.WeatherRenderState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.realisticthunder.CloudMask;
@Mixin(WeatherRendering.class)
public class CloudWeatherMixin {
    @Inject(method="buildPrecipitationPieces",at=@At("TAIL"))
    private void thunderCloud$filter(World world,int ticks,float tickDelta,Vec3d camera,WeatherRenderState state,CallbackInfo ci) {
        state.rainPieces.removeIf(p -> !CloudMask.beneath(world,p.x(),p.z()));
    }
    @Redirect(method="addParticlesAndSound",at=@At(value="INVOKE",target="Lnet/minecraft/client/world/ClientWorld;addParticleClient(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private void thunderCloud$particle(ClientWorld world,ParticleEffect type,double x,double y,double z,double vx,double vy,double vz) {
        if(CloudMask.beneath(world,x,z)) world.addParticleClient(type,x,y,z,vx,vy,vz);
    }
}
