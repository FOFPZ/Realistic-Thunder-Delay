package ru.realisticthunder.mixin;
import java.util.List;
import net.minecraft.client.render.WeatherRendering;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WeatherRendering.class)
public abstract class WindRainMixin {
    @Unique private boolean thunderPatch$rain;
    @Shadow private void renderPieces(VertexConsumer v,List<WeatherRendering.Piece> pieces,Vec3d pos,float brightness,int radius,float intensity) { throw new AssertionError(); }
    @Redirect(method="renderPrecipitation",at=@At(value="INVOKE",target="Lnet/minecraft/client/render/WeatherRendering;renderPieces(Lnet/minecraft/client/render/VertexConsumer;Ljava/util/List;Lnet/minecraft/util/math/Vec3d;FIF)V",ordinal=0))
    private void thunderPatch$rainLayer(WeatherRendering self,VertexConsumer v,List<WeatherRendering.Piece> pieces,Vec3d pos,float brightness,int radius,float intensity) {
        thunderPatch$rain=true; try { renderPieces(v,pieces,pos,brightness,radius,intensity); } finally { thunderPatch$rain=false; }
    }
    @Redirect(method="renderPieces",at=@At(value="INVOKE",target="Lnet/minecraft/client/render/VertexConsumer;vertex(FFF)Lnet/minecraft/client/render/VertexConsumer;"))
    private VertexConsumer thunderPatch$tilt(VertexConsumer consumer,float x,float y,float z) {
        return consumer.vertex(thunderPatch$rain?x+y*0.18f:x,y,thunderPatch$rain?z+y*0.06f:z);
    }
}
