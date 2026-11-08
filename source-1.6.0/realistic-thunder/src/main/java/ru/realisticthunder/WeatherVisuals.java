package ru.realisticthunder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import org.joml.Matrix4f;

/** Client-only wet patches; they never place blocks or modify the save. */
public final class WeatherVisuals {
    private record Patch(ClientWorld world, BlockPos floor, long expiry, float size) {}
    private static final ArrayList<Patch> patches=new ArrayList<>();
    private static final Random random=new Random();
    private static long tick;
    public static void tick(MinecraftClient c) {
        tick++;
        if(c.world==null || c.player==null) { patches.clear(); return; }
        var w=c.world; var player=c.player;
        Iterator<Patch> it=patches.iterator();
        while(it.hasNext()) {
            Patch p=it.next();
            if(p.world!=w || p.expiry<tick || p.floor.getSquaredDistance(player.getEntityPos())>45*45 || !w.getBlockState(p.floor.up()).isReplaceable()) it.remove();
        }
        if(!w.isRaining()) { patches.removeIf(p->p.expiry<tick+400); return; }
        for(int i=0;i<10;i++) {
            int x=player.getBlockX()+random.nextInt(41)-20, z=player.getBlockZ()+random.nextInt(41)-20;
            if(!w.isChunkLoaded(x>>4,z>>4)) continue;
            int y=w.getTopY(Heightmap.Type.MOTION_BLOCKING,x,z);
            BlockPos surface=new BlockPos(x,y,z), floor=surface.down();
            if(Math.abs(y-player.getY())>14 || w.getPrecipitation(surface)!=Biome.Precipitation.RAIN || !w.getBlockState(surface).isReplaceable() || !w.getBlockState(floor).isFullCube(w,floor) || !w.getFluidState(floor).isEmpty()) continue;
            if(random.nextInt(4)==0) w.addParticleClient(ParticleTypes.SPLASH,x+random.nextDouble(),y+0.04,z+random.nextDouble(),0,0.03,0);
            if(random.nextInt(5)==0) w.addParticleClient(ParticleTypes.FALLING_WATER,x+random.nextDouble(),y+2+random.nextDouble()*4,z+random.nextDouble(),0,-0.08,0);
            if(patches.size()<220 && random.nextInt(3)==0 && patches.stream().noneMatch(p->p.floor.equals(floor))) {
                patches.add(new Patch(w,floor,tick+700+random.nextInt(500),0.24f+random.nextFloat()*0.22f));
            }
        }
    }
    public static void render(WorldRenderContext context) {
        var c=MinecraftClient.getInstance();
        if(c.world==null || patches.isEmpty() || context.consumers()==null || context.matrices()==null) return;
        var camera=c.gameRenderer.getCamera().getCameraPos();
        var snapshot=java.util.List.copyOf(patches);
        // Submit before the entity layer is flushed; output vertices in both orientations.
        context.commandQueue().submitCustom(context.matrices(), RenderLayers.lightning(), (entry,out) -> {
            Matrix4f m=entry.getPositionMatrix();
            for(var p:snapshot) {
                float x=(float)(p.floor.getX()+0.5-camera.x), y=(float)(p.floor.getY()+1.012-camera.y), z=(float)(p.floor.getZ()+0.5-camera.z);
                float a=p.size, b=a*0.73f;
                Random shape=new Random(p.floor.asLong());
                float[] radius=new float[8];
                for(int i=0;i<8;i++) radius[i]=0.72f+shape.nextFloat()*0.28f;
                for(int i=0;i<8;i++) {
                    double u=i*Math.PI/4, v=((i+1)%8)*Math.PI/4;
                    float x1=x+(float)Math.cos(u)*a*radius[i], z1=z+(float)Math.sin(u)*b*radius[i];
                    float x2=x+(float)Math.cos(v)*a*radius[(i+1)%8], z2=z+(float)Math.sin(v)*b*radius[(i+1)%8];
                    quad(out,m,x,y,z,x1,y,z1,x2,y,z2,x,y,z);
                    quad(out,m,x,y,z,x2,y,z2,x1,y,z1,x,y,z);
                }
                // A narrow, muted highlight suggests a wet reflective surface.
                float h=y+0.002f;
                glint(out,m,x-a*0.30f,h,z-b*0.25f,x+a*0.25f,h,z-b*0.25f,x+a*0.20f,h,z-b*0.17f,x-a*0.30f,h,z-b*0.17f);
            }
        });
    }
    private static void glint(VertexConsumer v,Matrix4f m,float x1,float y1,float z1,float x2,float y2,float z2,float x3,float y3,float z3,float x4,float y4,float z4) {
        v.vertex(m,x4,y4,z4).color(0.65f,0.76f,0.81f,0.32f);
        v.vertex(m,x3,y3,z3).color(0.65f,0.76f,0.81f,0.32f);
        v.vertex(m,x2,y2,z2).color(0.65f,0.76f,0.81f,0.32f);
        v.vertex(m,x1,y1,z1).color(0.65f,0.76f,0.81f,0.32f);
    }
    private static void quad(VertexConsumer v,Matrix4f m,float x1,float y1,float z1,float x2,float y2,float z2,float x3,float y3,float z3,float x4,float y4,float z4) {
        v.vertex(m,x1,y1,z1).color(0.30f,0.44f,0.50f,0.40f);
        v.vertex(m,x2,y2,z2).color(0.30f,0.44f,0.50f,0.40f);
        v.vertex(m,x3,y3,z3).color(0.30f,0.44f,0.50f,0.40f);
        v.vertex(m,x4,y4,z4).color(0.30f,0.44f,0.50f,0.40f);
    }
}
