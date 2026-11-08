package ru.realisticthunder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ThunderClient implements ClientModInitializer {
    public static final List<Strike> strikes = new ArrayList<>();
    public record Strike(World world, double x, double z, long time, boolean sky) {}
    private static final SoundEvent THUNDER = SoundEvent.of(Identifier.of("realistic_thunder", "thunder"));
    private static final SoundEvent SKY_THUNDER = SoundEvent.of(Identifier.of("realistic_thunder", "sky_thunder"));
    private record Pending(World world, double x, double y, double z, long due, boolean sky) {}
    private static final List<Pending> pending = new ArrayList<>();
    private static long tick;
    public static long elapsedTicks() { return tick; }
    private static final RainSound[] rain=new RainSound[3];
    private static final Random RANDOM=new Random();
    private static int skyCountdown=100, fakeId=-1000000000;
    public static void schedule(World world, double x, double y, double z) { schedule(world,x,y,z,false); }
    private static void schedule(World world, double x, double y, double z, boolean sky) {
        var client=MinecraftClient.getInstance();
        if(client.player==null) return;
        strikes.add(new Strike(world,x,z,tick,sky));
        if(strikes.size()>128) strikes.remove(0);
        double distance=Math.sqrt(client.player.squaredDistanceTo(x,y,z));
        pending.add(new Pending(world,x,y,z,tick+Math.max(0,Math.round(distance/343.0*20)),sky));
    }
    @Override public void onInitializeClient() {
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player,world,hand,hit)-> {
            if (hand == net.minecraft.util.Hand.MAIN_HAND && world.isClient() && world.getBlockState(hit.getBlockPos()).isOf(WeatherMod.RADAR)) {
                MinecraftClient.getInstance().setScreen(new RadarScreen(hit.getBlockPos()));
                return net.minecraft.util.ActionResult.SUCCESS;
            }
            return net.minecraft.util.ActionResult.PASS;
        });
        net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents.BEFORE_ENTITIES.register(WeatherVisuals::render);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tick++;
            strikes.removeIf(s -> s.world()!=client.world || tick-s.time()>3600);
            WeatherVisuals.tick(client);
            RoofDrops.tick(client);
            if(client.world != null && client.player != null && client.world.isRaining()) {
                for(int i=0;i<rain.length;i++) if(rain[i]==null || rain[i].isDone() || (tick % 80 == 0 && !client.getSoundManager().isPlaying(rain[i]))) {
                    rain[i]=new RainSound(client.world,i); client.getSoundManager().play(rain[i]);
                }
                if(client.world.isThundering()) {
                    if(--skyCountdown<=0) {
                        skyCountdown=900+RANDOM.nextInt(900);
                        var p=client.player;
                        if(RANDOM.nextFloat()<0.75f) {
                            double angle=RANDOM.nextDouble()*Math.PI*2, d=45+RANDOM.nextInt(26);
                            double x=p.getX()+Math.cos(angle)*d, z=p.getZ()+Math.sin(angle)*d, y=p.getY()+35+RANDOM.nextInt(15);
                            SkyLightning bolt=new SkyLightning(client.world,fakeId--,x,y,z,RANDOM.nextInt());
                            client.world.addEntity(bolt);
                            schedule(client.world,x,y,z,true);
                        }
                    }
                }
            } else { for(int i=0;i<rain.length;i++) rain[i]=null; skyCountdown=100; }
            Iterator<Pending> it=pending.iterator();
            while(it.hasNext()) {
                Pending p=it.next();
                if(client.world!=p.world || client.player==null) { it.remove(); continue; }
                if(tick>=p.due) {
                    if (p.sky) {
                        client.getSoundManager().play(new net.minecraft.client.sound.PositionedSoundInstance(Identifier.of("realistic_thunder","sky_thunder"), SoundCategory.WEATHER, 0.28f, 0.9f, net.minecraft.client.sound.SoundInstance.createRandom(), false, 0, net.minecraft.client.sound.SoundInstance.AttenuationType.NONE, 0, 0, 0, true));
                    } else client.world.playSoundClient(p.x,p.y,p.z,THUNDER,SoundCategory.WEATHER,32.0f,1.0f,false);
                    it.remove();
                }
            }
        });
    }
}
