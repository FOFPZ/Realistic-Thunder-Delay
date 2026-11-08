package ru.realisticthunder.mixin;
import net.minecraft.entity.LightningEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.realisticthunder.ThunderClient;
@Mixin(LightningEntity.class)
public class LightningMixin {
    @Redirect(method="tick", at=@At(value="INVOKE", target="Lnet/minecraft/world/World;playSoundClient(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"))
    private void realisticThunder(World world, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
        if (sound == SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER) ThunderClient.schedule(world, x, y, z);
        else if (sound != SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT) world.playSoundClient(x, y, z, sound, category, volume, pitch, useDistance);
    }
}
