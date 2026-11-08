package ru.realisticthunder;
import java.util.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/** Connected, coplanar panels share one 64x64 survey with no seams. */
public class WallMapRenderer implements BlockEntityRenderer<WallMapEntity,WallMapRenderer.State> {
    public static class State extends BlockEntityRenderState { int[] pixels; int left,top,cols,rows; }
    public WallMapRenderer(BlockEntityRendererFactory.Context ignored) {}
    @Override public State createRenderState() { return new State(); }
    private static int horizontal(BlockPos pos, Direction face) {
        return switch(face) { case NORTH->pos.getX(); case EAST->pos.getZ(); case SOUTH->-pos.getX(); default->-pos.getZ(); };
    }
    private static Direction right(Direction face) { return switch(face){case NORTH->Direction.EAST;case EAST->Direction.SOUTH;case SOUTH->Direction.WEST;default->Direction.NORTH;}; }
    @Override public void updateRenderState(WallMapEntity be,State state,float delta,Vec3d camera,ModelCommandRenderer.CrumblingOverlayCommand overlay) {
        BlockEntityRenderer.super.updateRenderState(be,state,delta,camera,overlay);
        World world=be.getWorld(); BlockPos start=be.getPos(); Direction face=be.getCachedState().get(WallMapBlock.FACING),r=right(face);
        ArrayDeque<BlockPos> queue=new ArrayDeque<>(); Set<BlockPos> group=new HashSet<>();queue.add(start);group.add(start);
        while(world!=null && !queue.isEmpty() && group.size()<64) {
            BlockPos p=queue.removeFirst();
            for(Direction d:new Direction[]{r,r.getOpposite(),Direction.UP,Direction.DOWN}) {
                BlockPos n=p.offset(d); if(group.contains(n) || !world.isChunkLoaded(n)) continue;
                BlockState s=world.getBlockState(n);
                if(s.isOf(WeatherMod.WALL_MAP) && s.get(WallMapBlock.FACING)==face && group.size()<64) {group.add(n);queue.add(n);}
            }
        }
        int minX=Integer.MAX_VALUE,maxX=Integer.MIN_VALUE,minY=Integer.MAX_VALUE,maxY=Integer.MIN_VALUE;
        for(BlockPos p:group) {int x=horizontal(p,face);minX=Math.min(minX,x);maxX=Math.max(maxX,x);minY=Math.min(minY,p.getY());maxY=Math.max(maxY,p.getY());}
        state.cols=maxX-minX+1; state.rows=maxY-minY+1;
        state.left=horizontal(start,face)-minX;state.top=maxY-start.getY();
        // Physical centre is shared by every panel in the same connected group.
        int centreH=(minX+maxX)/2;
        int h=horizontal(start,face); BlockPos centre=start.offset(r,centreH-h).withY((minY+maxY)/2);
        state.pixels=MapCache.snapshot(centre);
    }
    @Override public void render(State state,MatrixStack matrices,OrderedRenderCommandQueue queue,CameraRenderState camera) {
        if(state.pixels==null) return;
        float rot=switch(state.blockState.get(WallMapBlock.FACING)){case EAST->90f;case SOUTH->180f;case WEST->270f;default->0f;};
        matrices.push();matrices.translate(0.5,0.5,0.5);matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-rot));matrices.translate(-0.5,-0.5,-0.5);
        int[] pixels=state.pixels.clone();int left=state.left,top=state.top,cols=state.cols,rows=state.rows;
        queue.submitCustom(matrices,RenderLayers.lightning(),(entry,out)-> {
            var m=entry.getPositionMatrix(); float z=0.873f;
            int xStart=left*64/cols, xEnd=(left+1)*64/cols, yStart=top*64/rows,yEnd=(top+1)*64/rows;
            for(int row=yStart;row<yEnd;row++) for(int col=xStart;col<xEnd;col++) {
                int color=pixels[row*64+col];float r=((color>>16)&255)/255f,g=((color>>8)&255)/255f,b=(color&255)/255f;
                float xa=left==0?0.05f:0f, xb=left==cols-1?0.95f:1f;
                float ya=top==0?0.95f:1f, yb=top==rows-1?0.05f:0f;
                float x0=xa+(xb-xa)*(col-xStart)/Math.max(1,xEnd-xStart),x1=xa+(xb-xa)*(col+1-xStart)/Math.max(1,xEnd-xStart);
                float y1=ya-(ya-yb)*(row-yStart)/Math.max(1,yEnd-yStart),y0=ya-(ya-yb)*(row+1-yStart)/Math.max(1,yEnd-yStart);
                out.vertex(m,x0,y0,z).color(r,g,b,1f);out.vertex(m,x0,y1,z).color(r,g,b,1f);out.vertex(m,x1,y1,z).color(r,g,b,1f);out.vertex(m,x1,y0,z).color(r,g,b,1f);
            }
        });matrices.pop();
    }
}
