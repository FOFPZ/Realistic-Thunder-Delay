package ru.realisticthunder;
import java.util.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import org.joml.Matrix4f;

public class RainRipples implements ClientModInitializer {
    private record Ring(ClientWorld world,double x,double y,double z,long start) {}
    private static final ArrayList<Ring> rings=new ArrayList<>();
    private static final Random rng=new Random(); private static long tick;
    @Override public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client->{
            tick++;
            if(client.world==null || client.player==null) {rings.clear();return;}
            rings.removeIf(r -> r.world!=client.world || tick-r.start>15);
            if(!client.world.isRaining() || tick%2!=0) return;
            for(int k=0;k<5 && rings.size()<50;k++) {
                int x=client.player.getBlockX()+rng.nextInt(25)-12,z=client.player.getBlockZ()+rng.nextInt(25)-12;
                if(!client.world.isChunkLoaded(x>>4,z>>4)) continue;
                int y=client.world.getTopY(Heightmap.Type.MOTION_BLOCKING,x,z);
                BlockPos p=new BlockPos(x,y,z);
                if(Math.abs(y-client.player.getY())>8 || client.world.getPrecipitation(p)!=Biome.Precipitation.RAIN || !client.world.getBlockState(p).isReplaceable())continue;
                var floor=client.world.getBlockState(p.down());
                if(!floor.isFullCube(client.world,p.down()) && floor.getFluidState().isEmpty())continue;
                rings.add(new Ring(client.world,x+rng.nextDouble(),y+0.018,z+rng.nextDouble(),tick));
            }
        });
        WorldRenderEvents.BEFORE_ENTITIES.register(context->{
            var client=MinecraftClient.getInstance();
            if(client.world==null || context.matrices()==null || rings.isEmpty())return;
            var cam=client.gameRenderer.getCamera().getCameraPos();
            var copy=List.copyOf(rings);long now=tick;
            context.commandQueue().submitCustom(context.matrices(),RenderLayers.lightning(),(entry,out)->{
                Matrix4f m=entry.getPositionMatrix();
                for(Ring r:copy) {
                    float t=Math.min(1f,(now-r.start)/15f); float radius=0.035f+0.16f*t, width=0.012f;
                    float x=(float)(r.x-cam.x),y=(float)(r.y-cam.y),z=(float)(r.z-cam.z),alpha=0.6f*(1f-t);
                    for(int i=0;i<8;i++) {
                        double a=i*Math.PI/4,b=(i+1)*Math.PI/4;
                        float ax=(float)Math.cos(a),az=(float)Math.sin(a),bx=(float)Math.cos(b),bz=(float)Math.sin(b);
                        quad(out,m,x+ax*radius,y,z+az*radius,x+ax*(radius+width),y,z+az*(radius+width),x+bx*(radius+width),y,z+bz*(radius+width),x+bx*radius,y,z+bz*radius,alpha);
                    }
                }
            });
        });
    }
    private static void quad(VertexConsumer v,Matrix4f m,float a,float b,float c,float d,float e,float f,float g,float h,float i,float j,float k,float l,float alpha){
        v.vertex(m,a,b,c).color(.72f,.83f,.88f,alpha);v.vertex(m,d,e,f).color(.72f,.83f,.88f,alpha);v.vertex(m,g,h,i).color(.72f,.83f,.88f,alpha);v.vertex(m,j,k,l).color(.72f,.83f,.88f,alpha);
        v.vertex(m,j,k,l).color(.72f,.83f,.88f,alpha);v.vertex(m,g,h,i).color(.72f,.83f,.88f,alpha);v.vertex(m,d,e,f).color(.72f,.83f,.88f,alpha);v.vertex(m,a,b,c).color(.72f,.83f,.88f,alpha);
    }
}
