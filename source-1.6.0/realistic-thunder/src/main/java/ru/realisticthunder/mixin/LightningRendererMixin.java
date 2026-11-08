package ru.realisticthunder.mixin;
import java.util.Random;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LightningEntityRenderer;
import net.minecraft.client.render.entity.state.LightningEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.realisticthunder.SkyLightning;

@Mixin(LightningEntityRenderer.class)
public class LightningRendererMixin {
    @Inject(method="render(Lnet/minecraft/client/render/entity/state/LightningEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at=@At("HEAD"), cancellable=true)
    private void renderBolt(LightningEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState camera, CallbackInfo ci) {
        long seed=state.seed;
        queue.submitCustom(matrices,RenderLayers.lightning(),(entry,out)->draw(entry.getPositionMatrix(),out,seed));
        ci.cancel();
    }
    private static void draw(Matrix4f m,VertexConsumer out,long seed) {
        Random r=new Random(seed);
        boolean sky=(seed & SkyLightning.MASK)==SkyLightning.MARK;
        float x=0,y=0,z=0, dx=0,dz=0;
        float dir=(float)(r.nextDouble()*Math.PI*2);
        int count=sky?42:64;
        for(int i=0;i<count;i++) {
            float nx,ny,nz;
            if(sky) {
                dx=dx*0.68f+(r.nextFloat()-0.5f)*0.95f;
                dz=dz*0.68f+(r.nextFloat()-0.5f)*0.95f;
                nx=x+(float)Math.cos(dir)*2.7f+dx;
                nz=z+(float)Math.sin(dir)*2.7f+dz;
                ny=y+(r.nextFloat()-0.5f)*1.8f;
            } else {
                dx=dx*0.68f+(r.nextFloat()-0.5f)*1.15f;
                dz=dz*0.68f+(r.nextFloat()-0.5f)*1.15f;
                nx=x+dx+(r.nextFloat()-0.5f)*0.7f;
                nz=z+dz+(r.nextFloat()-0.5f)*0.7f;
                ny=y+2f;
            }
            float taper=0.5f+0.5f*(float)i/count;
            float w=(sky?0.07f:0.09f)*taper;
            segment(m,out,x,y,z,nx,ny,nz,w*4,w*3,0.12f);
            segment(m,out,x,y,z,nx,ny,nz,w,w*0.8f,0.94f);
            if(i>4 && i<count-3 && r.nextFloat()<(sky?0.24f:0.20f)) {
                float angle=(float)(r.nextDouble()*Math.PI*2);
                branch(m,out,r,nx,ny,nz,(float)Math.cos(angle),(float)Math.sin(angle),sky, 2+r.nextInt(6),w*0.65f);
            }
            x=nx;y=ny;z=nz;
        }
    }
    private static void branch(Matrix4f m,VertexConsumer v,Random r,float x,float y,float z,float dx,float dz,boolean sky,int n,float w) {
        float speed=sky?2.1f:1.0f;
        for(int i=0;i<n;i++) {
            float nx=x+dx*speed+(r.nextFloat()-0.5f)*0.85f;
            float nz=z+dz*speed+(r.nextFloat()-0.5f)*0.85f;
            // Downward offshoots for cloud-to-ground lightning.
            float ny=y+(sky?(r.nextFloat()-0.5f)*1.2f:-1.7f-r.nextFloat());
            float a=w*(1f-(float)i/n), b=w*(1f-(float)(i+1)/n);
            segment(m,v,x,y,z,nx,ny,nz,a,b,0.78f);
            x=nx;y=ny;z=nz;
        }
    }
    private static void segment(Matrix4f m, VertexConsumer v,float x,float y,float z,float a,float b,float c,float w0,float w1,float alpha) {
        quad(m,v,x-w0,y,z,x+w0,y,z,a+w1,b,c,a-w1,b,c,alpha);
        quad(m,v,x,y-w0,z,x,y+w0,z,a,b+w1,c,a,b-w1,c,alpha);
        quad(m,v,x,y,z-w0,x,y,z+w0,a,b,c+w1,a,b,c-w1,alpha);
    }
    private static void quad(Matrix4f m,VertexConsumer v,float x1,float y1,float z1,float x2,float y2,float z2,float x3,float y3,float z3,float x4,float y4,float z4,float alpha) {
        v.vertex(m,x1,y1,z1).color(0.92f,0.96f,1f,alpha);
        v.vertex(m,x2,y2,z2).color(0.92f,0.96f,1f,alpha);
        v.vertex(m,x3,y3,z3).color(0.92f,0.96f,1f,alpha);
        v.vertex(m,x4,y4,z4).color(0.92f,0.96f,1f,alpha);
    }
}
