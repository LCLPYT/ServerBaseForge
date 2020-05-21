package work.lclpnet.serverbase.prot;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.IceBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.bukkitlike.BlockStateToStateEvent;
import net.minecraftforge.event.bukkitlike.FoodLevelChangeEvent;
import net.minecraftforge.event.custom.SnowFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.core.util.Location;
import work.lclpnet.serverbase.Config;
import work.lclpnet.serverbase.ServerBase;

@EventBusSubscriber(modid = ServerBase.MODID, bus = Bus.FORGE)
public class ProtectionListener {

	@SubscribeEvent
	public static void onDamagePlayer(LivingHurtEvent e) {
		if(!Config.isSpawnProtEnabled() 
				|| !(e.getEntity() instanceof PlayerEntity)
				|| !isInSpawnRange(e.getEntity())) return;
		
		PlayerEntity p = (PlayerEntity) e.getEntity();
		if(p.getFireTimer() > 0) p.setFireTimer(0);
		
		e.setCanceled(true);
	}
	
	@SubscribeEvent
	public static void onHunger(FoodLevelChangeEvent e) {
		if(Config.isSpawnProtEnabled() 
				&& isInSpawnRange(e.getEntity())
				&& e.getFromLevel() > e.getToLevel()) e.setCanceled(true);
	}
	
	@SubscribeEvent
	public static void onFade(BlockStateToStateEvent e) {
		if(!Config.isSpawnProtEnabled() 
				|| !(e.getWorld() instanceof World)) return;
		
		if(!(!Config.shouldIceMelt() && e.getState().getBlock() instanceof IceBlock) //ice melting
				&& !(!Config.shouldWaterFreeze() && e.getState().getBlock() instanceof FlowingFluidBlock) //water freezing
				&& !(!Config.shouldSnowMelt() && e.getState().getBlock() instanceof SnowBlock)) return; 
		
		BlockPos pos = e.getPos();
		if(isInSpawnRange(new Location((World) e.getWorld(), pos.getX(), pos.getY(), pos.getZ())))
			e.setCanceled(true);
	}
	
	@SubscribeEvent
	public static void onSnow(SnowFallEvent e) {
		if(!Config.isSpawnProtEnabled() || Config.shouldSnowFall()) return;
		
		BlockPos pos = e.getPos();
		if(isInSpawnRange(new Location((World) e.getWorld(), pos.getX(), pos.getY(), pos.getZ())))
			e.setCanceled(true);
	}
	
	public static boolean isInSpawnRange(Entity en) {
		return isInSpawnRange(new Location(en.world, en.getPosX(), en.getPosY(), en.getPosZ()));
	}
	
	public static boolean isInSpawnRange(Location loc) {
		return loc.squareDistanceTo(getOrigin(loc.world)) <= Config.getSpawnProtectionRange() * Config.getSpawnProtectionRange();
	}
	
	private static Location getOrigin(World w) {
		BlockPos spawnPoint = w.getSpawnPoint();
		return new Location(w, (double) spawnPoint.getX(), (double) spawnPoint.getY(), (double) spawnPoint.getZ());
	}
	
}
