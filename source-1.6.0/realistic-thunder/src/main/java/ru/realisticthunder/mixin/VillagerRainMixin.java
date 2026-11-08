package ru.realisticthunder.mixin;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.poi.PointOfInterestTypes;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public class VillagerRainMixin {
    // Apply after the brain has refreshed its daily schedule, not before it.
    @Inject(method="mobTick", at=@At("TAIL"))
    private void shelterInRain(ServerWorld world, CallbackInfo ci) {
        VillagerEntity v=(VillagerEntity)(Object)this;
        var brain=v.getBrain();
        if (!world.isRaining() || v.isAiDisabled() || v.hasCustomer()
            || world.getPrecipitation(v.getBlockPos()) != Biome.Precipitation.RAIN
            || brain.hasActivity(Activity.PANIC) || brain.hasActivity(Activity.RAID)
            || brain.hasActivity(Activity.HIDE) || brain.hasActivity(Activity.PRE_RAID)) return;
        var home=brain.getOptionalMemory(MemoryModuleType.HOME);
        if(home.isPresent() && home.get().dimension().equals(world.getRegistryKey())) {
            if (!brain.hasActivity(Activity.REST)) brain.doExclusively(Activity.REST);
            if(v.age%20==0 && v.squaredDistanceTo(home.get().pos().toCenterPos())>6.25) {
                BlockPos p=home.get().pos();
                brain.remember(MemoryModuleType.WALK_TARGET,new WalkTarget(p,1.2f,1));
                v.getNavigation().startMovingTo(p.getX()+0.5,p.getY(),p.getZ()+0.5,1.2);
            }
        } else if(v.age%40==0) {
            // Without a claimed bed, try a nearby existing bed, without claiming or stealing it.
            world.getPointOfInterestStorage().getNearestPosition(e->e.matchesKey(PointOfInterestTypes.HOME),v.getBlockPos(),48,PointOfInterestStorage.OccupationStatus.ANY).ifPresent(p->{
                if(v.squaredDistanceTo(p.toCenterPos())>6.25) {
                    brain.remember(MemoryModuleType.WALK_TARGET,new WalkTarget(p,1.2f,1));
                    v.getNavigation().startMovingTo(p.getX()+0.5,p.getY(),p.getZ()+0.5,1.2);
                }
            });
        }
    }
}
