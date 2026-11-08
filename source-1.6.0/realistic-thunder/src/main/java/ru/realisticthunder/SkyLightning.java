package ru.realisticthunder;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;

/** A short-lived client-only visual; it never ticks normal lightning logic or strikes blocks. */
public class SkyLightning extends LightningEntity {
    public static final long MARK=0x5a17c10d00000000L;
    public static final long MASK=0xffffffff00000000L;
    private int age;
    public SkyLightning(ClientWorld world, int id, double x, double y, double z, int variation) {
        super(EntityType.LIGHTNING_BOLT,world);
        setId(id); setPosition(x,y,z); seed=MARK | (variation & 0xffffffffL);
    }
    @Override public boolean shouldRender(double squaredDistance) { return squaredDistance < 512.0*512.0; }
    @Override public void tick() { if (++age > 4) discard(); }
}
