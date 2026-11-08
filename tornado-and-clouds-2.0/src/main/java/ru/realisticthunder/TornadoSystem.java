package ru.realisticthunder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import java.util.*;

/** Server-authoritative, bounded tornado simulation. Only loaded chunks are touched. */
public final class TornadoSystem implements ModInitializer {
    private static final Map<ServerWorld,Storm> ACTIVE=new IdentityHashMap<>();
    private static final Map<ServerWorld,Integer> CHECKS=new IdentityHashMap<>();
    private static final Random RNG=new Random();
    private static final int INTERVAL=20*60*15;
    private static final class Storm {
        double x,z,dx,dz; int age; final int target,life;
        Storm(double x,double z,int target){this.x=x;this.z=z;this.target=target;double a=RNG.nextDouble()*Math.PI*2;dx=Math.cos(a)*.095;dz=Math.sin(a)*.095;life=20*(90+target*25);}
        int strength(){ return Math.min(target,age/(20*20)); }
    }
    @Override public void onInitialize(){
        CommandRegistrationCallback.EVENT.register((dispatcher,registry,environment)->dispatcher.register(
            CommandManager.literal("tornado").requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
              .then(CommandManager.literal("spawn").then(CommandManager.argument("ef",IntegerArgumentType.integer(0,5)).executes(ctx->spawnCommand(ctx.getSource(),IntegerArgumentType.getInteger(ctx,"ef")))))
              .then(CommandManager.literal("stop").executes(ctx->{boolean removed=ACTIVE.remove(ctx.getSource().getWorld())!=null;ctx.getSource().sendFeedback(()->Text.literal(removed?"Торнадо остановлен":"Здесь нет торнадо"),false);return removed?1:0;}))));
        ServerTickEvents.END_WORLD_TICK.register(TornadoSystem::tick);
        ServerLifecycleEvents.SERVER_STOPPED.register(server->{ACTIVE.clear();CHECKS.clear();});
    }
    private static int spawnCommand(ServerCommandSource source,int ef){
        ServerWorld world=source.getWorld(); Vec3d p=source.getPosition();
        if(!world.getRegistryKey().equals(World.OVERWORLD)){source.sendError(Text.literal("Торнадо доступны только в обычном мире"));return 0;}
        if(ACTIVE.containsKey(world)){source.sendError(Text.literal("В этом мире уже есть торнадо"));return 0;}
        if(!world.isChunkLoaded((int)Math.floor(p.x/16),(int)Math.floor(p.z/16))){source.sendError(Text.literal("Чанк не загружен"));return 0;}
        ACTIVE.put(world,new Storm(p.x,p.z,ef));CHECKS.put(world,0);
        source.sendFeedback(()->Text.literal("Торнадо появился: EF0, максимальная сила EF"+ef+". Остановить: /tornado stop"),true);return 1;
    }
    private static void tick(ServerWorld world){
        if(!world.getRegistryKey().equals(World.OVERWORLD))return;
        Storm s=ACTIVE.get(world);
        if(s==null){
            int timer=CHECKS.getOrDefault(world,0)+1; CHECKS.put(world,timer);
            if(timer<INTERVAL || !world.isThundering() || world.getPlayers().isEmpty())return;
            CHECKS.put(world,INTERVAL-20*60*5);
            if(RNG.nextFloat()>.35f)return;
            var players=world.getPlayers();var p=players.get(RNG.nextInt(players.size()));double a=RNG.nextDouble()*Math.PI*2;
            double x=p.getX()+Math.cos(a)*(40+RNG.nextInt(30)),z=p.getZ()+Math.sin(a)*(40+RNG.nextInt(30));
            if(!world.isChunkLoaded((int)Math.floor(x/16),(int)Math.floor(z/16)))return;
            int n=RNG.nextInt(100),ef=n<35?0:n<62?1:n<80?2:n<91?3:n<98?4:5;
            ACTIVE.put(world,new Storm(x,z,ef));CHECKS.put(world,0);return;
        }
        s.age++;
        if(s.age>s.life){ACTIVE.remove(world);CHECKS.put(world,0);return;}
        double nx=s.x+s.dx,nz=s.z+s.dz;
        if(!world.isChunkLoaded((int)Math.floor(nx/16),(int)Math.floor(nz/16))){ACTIVE.remove(world);CHECKS.put(world,0);return;}
        s.x=nx;s.z=nz;
        int ground=world.getTopY(Heightmap.Type.MOTION_BLOCKING,(int)Math.floor(s.x),(int)Math.floor(s.z));
        int ef=s.strength();double radius=5+ef*2.4;
        if(s.age%2==0) for(int i=0;i<30;i++){
            double h=RNG.nextDouble()*64, angle=s.age*.16+i*2.399+h*.10;
            double r=(2.0+ef*.5)+(h/64)*(10+ef*1.5)+RNG.nextGaussian()*1.0;
            world.spawnParticles(i%3==0?ParticleTypes.LARGE_SMOKE:ParticleTypes.CLOUD,true,true,s.x+Math.cos(angle)*r,ground+h,s.z+Math.sin(angle)*r,1,0,0,0,0);
        }
        if(s.age%4==0){
            Box box=new Box(s.x-radius,ground-8,s.z-radius,s.x+radius,ground+60,s.z+radius);
            for(LivingEntity e:world.getEntitiesByClass(LivingEntity.class,box,e->e.isAlive() && (!(e instanceof PlayerEntity p) || (!p.isCreative()&&!p.isSpectator())))){
                double x=s.x-e.getX(),z=s.z-e.getZ(),d=Math.max(1,Math.sqrt(x*x+z*z));
                if(d>radius)continue;
                double f=(1-d/radius)*(0.07+ef*.025);
                e.addVelocity(x/d*f-z/d*f*.65,0.045+ef*.017,z/d*f+x/d*f*.65);e.fallDistance=0;
            }
        }
        if(s.age%6==0) for(int i=0;i<2+ef*3;i++){
            double angle=RNG.nextDouble()*Math.PI*2,rad=Math.sqrt(RNG.nextDouble())*radius;
            int x=(int)Math.floor(s.x+Math.cos(angle)*rad),z=(int)Math.floor(s.z+Math.sin(angle)*rad);
            if(!world.isChunkLoaded(x>>4,z>>4))continue;
            int y=world.getTopY(Heightmap.Type.MOTION_BLOCKING,x,z)-1-RNG.nextInt(3);
            BlockPos pos=new BlockPos(x,y,z);BlockState state=world.getBlockState(pos);
            float hardness=state.getHardness(world,pos);
            if(state.isAir()||hardness<0||hardness>0.5f+ef*1.1f||state.hasBlockEntity()||!state.getFluidState().isEmpty())continue;
            world.breakBlock(pos,false);
        }
    }
}
