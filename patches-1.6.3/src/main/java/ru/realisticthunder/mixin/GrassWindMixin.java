package ru.realisticthunder.mixin;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderManager.class)
public class GrassWindMixin {
    @Unique private static boolean thunderWind$plant(BlockState state) {
        return state.isOf(Blocks.SHORT_GRASS)||state.isOf(Blocks.FERN)||state.isOf(Blocks.SHORT_DRY_GRASS)||state.isOf(Blocks.DEAD_BUSH);
    }
    @Inject(method="renderBlock",at=@At("HEAD"))
    private void thunderWind$start(BlockState state,BlockPos pos,BlockRenderView world,MatrixStack matrices,VertexConsumer v,boolean cull,List<BlockModelPart> parts,CallbackInfo ci) {
        if(thunderWind$plant(state)) { matrices.push();matrices.translate(0,0.00010,0);matrices.scale(1f,1.00020f,1f); }
    }
    @Inject(method="renderBlock",at=@At("RETURN"))
    private void thunderWind$finish(BlockState state,BlockPos pos,BlockRenderView world,MatrixStack matrices,VertexConsumer v,boolean cull,List<BlockModelPart> parts,CallbackInfo ci) {
        if(thunderWind$plant(state)) matrices.pop();
    }
}
