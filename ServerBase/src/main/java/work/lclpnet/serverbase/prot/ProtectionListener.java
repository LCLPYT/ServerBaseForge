package work.lclpnet.serverbase.prot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
	
	public static boolean isInSpawnRange(Entity en) {
		return isInSpawnRange(new Location(en.world, en.getPosX(), en.getPosY(), en.getPosZ()));
	}
	
	public static boolean isInSpawnRange(Location loc) {
		return loc.squareDistanceTo(getOrigin(loc.world)) <= Config.getSpawnProtectionRange();
	}
	
	private static Location getOrigin(World w) {
		BlockPos spawnPoint = w.getSpawnPoint();
		return new Location(w, (double) spawnPoint.getX(), (double) spawnPoint.getY(), (double) spawnPoint.getZ());
	}
	
}
