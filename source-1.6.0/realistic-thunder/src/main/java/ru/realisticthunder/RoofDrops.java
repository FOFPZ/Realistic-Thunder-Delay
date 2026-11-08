package ru.realisticthunder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import java.util.Random;

public final class RoofDrops {
    private static final Random random=new Random();
    private static int delay=20;
    public static boolean hasRoof(ClientWorld world, BlockPos pos) {
        // Look for an actual overhead block, including ceilings higher than eight blocks.
        for(int i=2;i<=32;i++) {
            BlockPos p=pos.up(i); var state=world.getBlockState(p);
            if(!state.isAir() && !state.isReplaceable() && state.getFluidState().isEmpty()) return true;
        }
        return false;
    }
    public static void tick(MinecraftClient client) {
        if(client.world==null || client.player==null || !client.world.isRaining()) { delay=20; return; }
        var pos=client.player.getBlockPos();
        if(client.world.getPrecipitation(pos)!=Biome.Precipitation.RAIN || !hasRoof(client.world,pos)) { delay=20; return; }
        if(--delay<=0) {
            delay=55+random.nextInt(50);
            client.getSoundManager().play(new PositionedSoundInstance(Identifier.of("realistic_thunder","roof_drop"), SoundCategory.WEATHER,0.62f,0.96f+random.nextFloat()*0.08f, SoundInstance.createRandom(),false,0,SoundInstance.AttenuationType.NONE,0,0,0,true));
        }
    }
}
