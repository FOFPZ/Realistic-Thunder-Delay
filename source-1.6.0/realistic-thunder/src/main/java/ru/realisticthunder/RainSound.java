package ru.realisticthunder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;

public class RainSound extends MovingSoundInstance {
    private final ClientWorld world;
    private final int layer;
    public RainSound(ClientWorld world, int layer) {
        super(SoundEvent.of(Identifier.of("realistic_thunder", new String[]{"rain","shelter","heavy_rain"}[layer])), SoundCategory.WEATHER, SoundInstance.createRandom());
        this.world = world; this.layer=layer;
        repeat=true; repeatDelay=0; relative=true;
        attenuationType=SoundInstance.AttenuationType.NONE; volume=0.01f;
    }
    @Override public void tick() {
        var client = MinecraftClient.getInstance();
        if (client.world != world || client.player == null || !world.isRaining()) { setDone(); return; }
        BlockPos pos=client.player.getBlockPos();
        boolean rainBiome=world.getPrecipitation(pos)==Biome.Precipitation.RAIN;
        int depth=Math.max(0,world.getTopPosition(Heightmap.Type.MOTION_BLOCKING,pos).getY()-pos.getY());
        // Shallow shelter: quiet surrounding rain and audible drops above. Deep underground fades out.
        float nearRoof=RoofDrops.hasRoof(world, pos) ? 1f : 0f;
        float deep=depth>24 ? 0.3f : 1f;
        float rain=rainBiome ? world.getRainGradient(1f) : 0f;
        float storm=world.getThunderGradient(1f);
        float target = switch(layer) {
            case 0 -> 0.40f*rain*deep*(nearRoof>0 ? 0.30f : 1f);
            case 1 -> 0.62f*rain*nearRoof;
            default -> 0.18f*rain*storm*deep*(nearRoof>0 ? 0.50f : 1f);
        };
        volume+=(target-volume)*0.08f;
    }
}
