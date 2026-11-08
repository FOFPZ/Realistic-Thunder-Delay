package ru.realisticthunder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import java.util.ArrayList;
import java.util.Random;

/** Compact faceted cloud volumes aligned with the vanilla cloud mask. */
public final class VolumetricClouds implements ClientModInitializer {
    private record Puff(float x,float y,float z,float rx,float ry,float rz,float shade) {}
    @Override public void onInitializeClient() {
        WorldRenderEvents.BEFORE_ENTITIES.register(context->{
            var client=MinecraftClient.getInstance();var w=client.world;
            if(w==null || !w.getRegistryKey().equals(World.OVERWORLD) || context.matrices()==null || client.options.getCloudRenderModeValue()==CloudRenderMode.OFF)return;
            var camera=client.gameRenderer.getCamera().getCameraPos();
            // Vanilla clouds move 0.03 blocks per tick and are 12 blocks per mask cell.
            double drift=w.getTime()*0.03;
            int cx=(int)Math.floor((camera.x+drift)/12),cz=(int)Math.floor((camera.z+3.96)/12);
            var puffs=new ArrayList<Puff>();float storm=w.getRainGradient(1f);
            for(int dz=-7;dz<=7;dz++)for(int dx=-7;dx<=7;dx++){
                int gx=cx+dx,gz=cz+dz;
                double wx=gx*12.0-drift+6,wz=gz*12.0-3.96+6;
                if(!CloudMask.beneath(w,wx,wz))continue;
                Random rng=new Random((long)gx*341873128712L+(long)gz*132897987541L);
                float base=.90f-storm*.28f;
                for(int k=0;k<2;k++)puffs.add(new Puff((float)(wx-camera.x+rng.nextDouble()*6-3),(float)(188-camera.y+rng.nextDouble()*3),
                    (float)(wz-camera.z+rng.nextDouble()*6-3),5+rng.nextFloat()*3,3+rng.nextFloat()*3,5+rng.nextFloat()*3,base));
            }
            if(puffs.isEmpty())return;
            context.commandQueue().submitCustom(context.matrices(),RenderLayers.debugQuads(),(entry,out)->{
                Matrix4f m=entry.getPositionMatrix();
                for(Puff p:puffs) ellipsoid(out,m,p);
            });
        });
    }
    private static void ellipsoid(VertexConsumer out,Matrix4f m,Puff p){
        int bands=4,slices=8;
        for(int b=0;b<bands;b++){
            double v0=-Math.PI/2+Math.PI*b/bands,v1=-Math.PI/2+Math.PI*(b+1)/bands;
            float shade=p.shade*(.82f+.18f*(float)Math.sin((v0+v1)*.5)*.5f);
            for(int a=0;a<slices;a++){
                double u0=2*Math.PI*a/slices,u1=2*Math.PI*(a+1)/slices;
                vertex(out,m,p,u0,v0,shade);vertex(out,m,p,u0,v1,shade);vertex(out,m,p,u1,v1,shade);vertex(out,m,p,u1,v0,shade);
                vertex(out,m,p,u1,v0,shade);vertex(out,m,p,u1,v1,shade);vertex(out,m,p,u0,v1,shade);vertex(out,m,p,u0,v0,shade);
            }
        }
    }
    private static void vertex(VertexConsumer out,Matrix4f m,Puff p,double u,double v,float c){
        out.vertex(m,p.x+(float)(Math.cos(v)*Math.cos(u))*p.rx,p.y+(float)Math.sin(v)*p.ry,p.z+(float)(Math.cos(v)*Math.sin(u))*p.rz).color(c,c,c,0.75f);
    }
}
