package ru.realisticthunder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;

/** Loaded chunks only: Minecraft has one weather state per dimension, not local storm cells. */
public class RadarScreen extends Screen {
    private static final int N=33;
    private final BlockPos center;
    private final int[][] colors=new int[N][N];
    private final int[][] heights=new int[N][N];
    private int refresh, zoom=1;
    private static final int[] STEPS={4,8,16,32};
    public RadarScreen(BlockPos pos) { super(Text.literal("ГРОЗОВОЙ РАДАР")); center=pos.toImmutable(); }
    @Override public boolean shouldPause() { return false; }
    @Override public void tick() {
        if (client==null || client.world==null || refresh++%20!=0) return;
        var w=client.world; int step=STEPS[zoom];
        for(int z=0;z<N;z++) for(int x=0;x<N;x++) {
            int px=center.getX()+(x-N/2)*step, pz=center.getZ()+(z-N/2)*step;
            if(!w.isChunkLoaded(px>>4,pz>>4)) { colors[x][z]=0xff202833; heights[x][z]=Integer.MIN_VALUE; continue; }
            int y=w.getTopY(Heightmap.Type.MOTION_BLOCKING,px,pz); heights[x][z]=y;
            var p=new BlockPos(px,y,pz); var type=w.getPrecipitation(p);
            boolean water=!w.getFluidState(p.down()).isEmpty();
            int base=water?0xff244e68:0xff4e674f;
            if(w.isRaining() && type==Biome.Precipitation.RAIN) base=w.isThundering()?0xff675081:0xff356f91;
            else if(w.isRaining() && type==Biome.Precipitation.SNOW) base=0xffa3b7c4;
            else if(type==Biome.Precipitation.NONE && !water) base=0xff897c55;
            int shade=Math.max(-20,Math.min(20,(y-64)/2));
            colors[x][z]=shade(base,shade);
        }
    }
    private int shade(int color,int amount) {
        int r=Math.max(0,Math.min(255,((color>>16)&255)+amount));
        int g=Math.max(0,Math.min(255,((color>>8)&255)+amount));
        int b=Math.max(0,Math.min(255,(color&255)+amount));
        return 0xff000000 | r<<16 | g<<8 | b;
    }
    @Override protected void init() { refresh=0; tick(); }
    @Override public boolean mouseScrolled(double x,double y,double horizontal,double vertical) {
        int newZoom=Math.max(0,Math.min(STEPS.length-1,zoom+(vertical>0?-1:1)));
        if(newZoom!=zoom){ zoom=newZoom; refresh=0; tick(); }
        return true;
    }
    @Override public void render(DrawContext c,int mouseX,int mouseY,float delta) {
        c.fill(0,0,width,height,0xe010151e);
        int cell=Math.max(3,Math.min(9,Math.min((width-36)/N,(height-110)/N)));
        int size=N*cell, l=(width-size)/2, top=Math.max(26,(height-size-56)/2), cx=l+size/2, cy=top+size/2, step=STEPS[zoom];
        c.fill(l-3,top-3,l+size+3,top+size+3,0xff58747b);
        c.fill(l-2,top-2,l+size+2,top+size+2,0xff121e29);
        c.drawCenteredTextWithShadow(textRenderer,title,width/2,top-20,0xffdbeff4);
        for(int z=0;z<N;z++) for(int x=0;x<N;x++) c.fill(l+x*cell,top+z*cell,l+(x+1)*cell,top+(z+1)*cell,colors[x][z]);
        for(int i=0;i<=N;i+=8) { c.fill(l+i*cell,top,l+i*cell+1,top+size,0x50324755); c.fill(l,top+i*cell,l+size,top+i*cell+1,0x50324755); }
        c.fill(cx-3,cy-1,cx+4,cy+2,0xffffffff); c.fill(cx-1,cy-3,cx+2,cy+4,0xffffffff);
        int ground=0, sky=0;
        String hoverStrike=null;
        if(client!=null && client.world!=null) {
            for(var s:ThunderClient.strikes) {
                if(s.world()!=client.world) continue;
                int px=cx+(int)Math.round((s.x()-center.getX())*cell/step), py=cy+(int)Math.round((s.z()-center.getZ())*cell/step);
                if(px>=l && px<l+size && py>=top && py<top+size) {
                    if(s.sky())sky++; else ground++;
                    if(Math.abs(mouseX-px)<=5 && Math.abs(mouseY-py)<=5) {
                        long seconds=Math.max(0,(ThunderClient.elapsedTicks()-s.time())/20);
                        hoverStrike=(s.sky()?"Небо":"Удар")+" X "+(int)s.x()+" Z "+(int)s.z()+" · "+seconds+" с назад";
                    }
                    int color=s.sky()?0xffee8cf7:0xffffd43c;
                    c.fill(px-3,py-3,px+4,py+4,0xff151c24);c.fill(px-2,py-2,px+3,py+3,color);
                }
            }
            if(client.player!=null) {
                int px=cx+(int)Math.round((client.player.getX()-center.getX())*cell/step);
                int py=cy+(int)Math.round((client.player.getZ()-center.getZ())*cell/step);
                if(px>=l && px<l+size && py>=top && py<top+size) { c.fill(px-2,py-2,px+3,py+3,0xff32ffd5); }
            }
        }
        c.drawCenteredTextWithShadow(textRenderer,"С",width/2,top-10,0xffc8d7dc);
        String weather=client!=null && client.world!=null ? (client.world.isThundering()?"ГРОЗА":client.world.isRaining()?"ДОЖДЬ":"ЯСНО") : "НЕТ ДАННЫХ";
        c.drawTextWithShadow(textRenderer,weather,l,top+size+6,0xffdbecf2);
        c.drawTextWithShadow(textRenderer,"Удары: "+ground+"  Небо: "+sky,l,top+size+18,0xffffe083);
        int gx=(mouseX-l)/cell,gz=(mouseY-top)/cell;
        String info="Колесо: масштаб · клетка " + step + " бл.";
        if(mouseX>=l && mouseX<l+size && mouseY>=top && mouseY<top+size && gx>=0 && gx<N && gz>=0 && gz<N) {
            int wx=center.getX()+(gx-N/2)*step,wz=center.getZ()+(gz-N/2)*step;
            info="X " + wx + "  Z " + wz + (heights[gx][gz]==Integer.MIN_VALUE ? " · нет данных" : "  Y " + heights[gx][gz]);
        }
        if(hoverStrike!=null) info=hoverStrike;
        c.drawCenteredTextWithShadow(textRenderer,info,width/2,top+size+30,0xffa9c4cc);
        c.drawTextWithShadow(textRenderer,"З",l-12,cy-4,0xffa9c4cc);
        c.drawTextWithShadow(textRenderer,"В",l+size+4,cy-4,0xffa9c4cc);
        c.drawTextWithShadow(textRenderer,"Жёлтый: удар  Розовый: небо  Бирюзовый: игрок",l,top+size+42,0xffa9c4cc);
    }
}
