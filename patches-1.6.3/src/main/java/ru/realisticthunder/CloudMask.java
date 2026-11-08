package ru.realisticthunder;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/** Samples the same texture, alpha cutoff, cell size and drift as vanilla 1.21.11 cloud renderer. */
public final class CloudMask {
    private static volatile BufferedImage mask;
    private static volatile boolean attempted;
    public static void invalidate() { mask=null;attempted=false; }
    private static BufferedImage getMask() {
        if(!attempted) synchronized(CloudMask.class) {
            if(!attempted) {
                attempted=true;
                try {
                    var entry=MinecraftClient.getInstance().getResourceManager().getResource(Identifier.of("minecraft","textures/environment/clouds.png"));
                    if(entry.isPresent()) try(var stream=entry.get().getInputStream()){ mask=ImageIO.read(stream); }
                } catch(Exception error) { System.err.println("Thunder System cloud mask: "+error); }
            }
        }
        return mask;
    }
    public static boolean beneath(World world,double x,double z) {
        if(world==null) return true;
        BufferedImage image=getMask(); if(image==null||image.getWidth()==0||image.getHeight()==0)return true;
        long ticks=Math.floorMod(world.getTime(),(long)image.getWidth()*400L);
        int px=Math.floorMod((int)Math.floor((x+ticks*0.03)/12.0),image.getWidth());
        int pz=Math.floorMod((int)Math.floor((z+3.96)/12.0),image.getHeight());
        return (image.getRGB(px,pz)>>>24)>=10;
    }
    public static boolean atPlayer() {
        var client=MinecraftClient.getInstance();
        return client.world==null || client.player==null || beneath(client.world,client.player.getX(),client.player.getZ());
    }
}
