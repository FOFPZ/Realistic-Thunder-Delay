package ru.realisticthunder.mixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.CloudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
@Mixin(CloudRenderer.class)
public class CloudMixin {
    @ModifyVariable(method="renderClouds", at=@At("HEAD"), argsOnly=true, ordinal=0)
    private int weatherCloudColor(int color) {
        var world=MinecraftClient.getInstance().world;
        if(world==null) return color;
        float factor=1f-0.20f*world.getRainGradient(1f)-0.22f*world.getThunderGradient(1f);
        int a=(color>>>24)&255, r=(int)(((color>>>16)&255)*factor), g=(int)(((color>>>8)&255)*factor), b=(int)((color&255)*factor);
        return (a<<24)|(r<<16)|(g<<8)|b;
    }
}
