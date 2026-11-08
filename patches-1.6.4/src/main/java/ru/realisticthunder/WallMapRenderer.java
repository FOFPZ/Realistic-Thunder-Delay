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
import net.minecraft.client.MinecraftClient;
import ru.realisticthunder.mixin.MapCacheAccess;

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
        state.pixels=samplePanel(centre, state.left, state.top, state.cols, state.rows);
    }
    /** A 320-block long edge shared continuously by all panels, sampled only from explored chunks. */
    private static int[] samplePanel(BlockPos centre,int left,int top,int cols,int rows) {
        int[] result=new int[32*32];
        var cells=MapCacheAccess.thunder$getCells();
        double spanX=320.0*cols/Math.max(cols,rows), spanZ=320.0*rows/Math.max(cols,rows);
        for(int y=0;y<32;y++) for(int x=0;x<32;x++) {
            double wx=centre.getX()+(((left+(x+0.5)/32.0)/cols)-0.5)*spanX;
            double wz=centre.getZ()+(((top+(y+0.5)/32.0)/rows)-0.5)*spanZ;
            int cx=(int)Math.floor(wx/16), cz=(int)Math.floor(wz/16);
            long key=((long)cx<<32)|(cz&0xffffffffL);
            result[y*32+x]=cells.getOrDefault(key,0xff162735);
        }
        var client=MinecraftClient.getInstance();
        if(client.world!=null) {
            for(var strike:ThunderClient.strikes) if(strike.world()==client.world)
                mark(result,centre,left,top,cols,rows,spanX,spanZ,strike.x(),strike.z(),strike.sky()?0xfff489f4:0xffffd340);
            if(client.player!=null) mark(result,centre,left,top,cols,rows,spanX,spanZ,client.player.getX(),client.player.getZ(),0xff4600d7);
        }
        return result;
    }
    private static void mark(int[] pixels,BlockPos centre,int left,int top,int cols,int rows,double spanX,double spanZ,double x,double z,int color) {
        int px=(int)Math.floor(((x-centre.getX())/spanX+0.5)*cols*32)-left*32;
        int py=(int)Math.floor(((z-centre.getZ())/spanZ+0.5)*rows*32)-top*32;
        if(px>=0 && px<32 && py>=0 && py<32) pixels[py*32+px]=color;
    }
    @Override public void render(State state,MatrixStack matrices,OrderedRenderCommandQueue queue,CameraRenderState camera) {
        if(state.pixels==null) return;
        float rot=switch(state.blockState.get(WallMapBlock.FACING)){case EAST->90f;case SOUTH->180f;case WEST->270f;default->0f;};
        matrices.push();matrices.translate(0.5,0.5,0.5);matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-rot));matrices.translate(-0.5,-0.5,-0.5);
        int[] pixels=state.pixels.clone();int left=state.left,top=state.top,cols=state.cols,rows=state.rows;
        queue.submitCustom(matrices,RenderLayers.lightning(),(entry,out)-> {
            var m=entry.getPositionMatrix(); float z=0.873f;
            int xStart=0, xEnd=32, yStart=0, yEnd=32;
            for(int row=yStart;row<yEnd;row++) for(int col=xStart;col<xEnd;col++) {
                int color=pixels[row*32+col];float r=((color>>16)&255)/255f,g=((color>>8)&255)/255f,b=(color&255)/255f;
                float xa=left==0?0.05f:0f, xb=left==cols-1?0.95f:1f;
                float ya=top==0?0.95f:1f, yb=top==rows-1?0.05f:0f;
                float x0=xa+(xb-xa)*(col-xStart)/Math.max(1,xEnd-xStart),x1=xa+(xb-xa)*(col+1-xStart)/Math.max(1,xEnd-xStart);
                float y1=ya-(ya-yb)*(row-yStart)/Math.max(1,yEnd-yStart),y0=ya-(ya-yb)*(row+1-yStart)/Math.max(1,yEnd-yStart);
                out.vertex(m,x0,y0,z).color(r,g,b,1f);out.vertex(m,x0,y1,z).color(r,g,b,1f);out.vertex(m,x1,y1,z).color(r,g,b,1f);out.vertex(m,x1,y0,z).color(r,g,b,1f);
            }
        });matrices.pop();
    }
}
