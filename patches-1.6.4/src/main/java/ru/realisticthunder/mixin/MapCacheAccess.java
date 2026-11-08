package ru.realisticthunder.mixin;
import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import ru.realisticthunder.MapCache;
@Mixin(value=MapCache.class,remap=false)
public interface MapCacheAccess {
    @Accessor(value="cells",remap=false)
    static Map<Long,Integer> thunder$getCells() { throw new AssertionError(); }
}
