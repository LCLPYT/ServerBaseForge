package work.lclpnet.serverbase.prot;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.FrostedIceBlock;
import net.minecraft.block.IceBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.bukkitlike.BlockStateToStateEvent;
import net.minecraftforge.event.bukkitlike.FoodLevelChangeEvent;
import net.minecraftforge.event.bukkitlike.PlayerArmorStandManipulateEvent;
import net.minecraftforge.event.bukkitlike.PlayerBucketEmptyEvent;
import net.minecraftforge.event.bukkitlike.PlayerBucketFillEvent;
import net.minecraftforge.event.custom.FrostWalkerEvent;
import net.minecraftforge.event.custom.SnowFallEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.core.util.Location;
import work.lclpnet.core.util.MessageType;
import work.lclpnet.serverbase.Config;
import work.lclpnet.serverbase.ServerBase;

@EventBusSubscriber(modid = ServerBase.MODID, bus = Bus.FORGE)
public class ProtectionListener {

	@SubscribeEvent
	public static void onDamage(LivingHurtEvent e) {
		if(!Config.isSpawnProtEnabled() || !isInSpawnRange(e.getEntity())) return;

		if(e.getEntity() instanceof PlayerEntity) {
			PlayerEntity p = (PlayerEntity) e.getEntity();
			if(p.getFireTimer() > 0) p.setFireTimer(0);
			e.setCanceled(true);
			return;
		}
		
		Entity trueSource = e.getSource().getTrueSource();
		if(trueSource != null && !(trueSource instanceof PlayerEntity && canBypass((PlayerEntity) trueSource))) e.setCanceled(true);
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

		if(((!Config.shouldIceMelt() && e.getState().getBlock() instanceof IceBlock && !(e.getState().getBlock() instanceof FrostedIceBlock)) //ice melting
				|| (!Config.shouldWaterFreeze() && e.getState().getBlock() instanceof FlowingFluidBlock) //water freezing
				|| (!Config.shouldSnowMelt() && e.getState().getBlock() instanceof SnowBlock)
				) && isInSpawnRange((World) e.getWorld(), e.getPos()))
			e.setCanceled(true);
	}

	@SubscribeEvent
	public static void onSnow(SnowFallEvent e) {
		if(Config.isSpawnProtEnabled() && !Config.shouldSnowFall()
				&& isInSpawnRange((World) e.getWorld(), e.getPos())) e.setCanceled(true);
	}

	@SubscribeEvent
	public static void onArmorStand(PlayerArmorStandManipulateEvent e) {
		if(!Config.isSpawnProtEnabled() || Config.allowArmorStandInteraction() || !(e.getTarget() instanceof ArmorStandEntity)) return;

		if(!canBypass(e.getPlayer()) && isInSpawnRange(e.getTarget())) e.setCanceled(true);
	}

	@SubscribeEvent
	public static void onExplode(ExplosionEvent.Start e) {
		if(Config.isSpawnProtEnabled() && !Config.allowExplosions() && isInSpawnRange(e.getWorld(), e.getExplosion().getPosition())) 
			e.setCanceled(true);
	}

	@SubscribeEvent
	public static void onMobGrief(EntityMobGriefingEvent e) {
		if(Config.isSpawnProtEnabled() && !Config.allowMobGriefing() && isInSpawnRange(e.getEntity()))
			e.setResult(Result.DENY);
	}

	@SubscribeEvent
	public static void onItemFrameRotate(PlayerInteractEvent.EntityInteract e) {
		if(Config.isSpawnProtEnabled() && !Config.allowItemFrameInteraction() 
				&& e.getTarget() instanceof ItemFrameEntity && !canBypass(e.getPlayer()) && isInSpawnRange(e.getTarget()))
			e.setCanceled(true);
	}
	
	@SubscribeEvent
	public static void onEntitySpawn(CheckSpawn e) {
		if(!Config.isSpawnProtEnabled() || Config.allowSpawnMonsters()) return;
		
		Entity en = e.getEntity();
		boolean bool = en instanceof SlimeEntity || en instanceof BatEntity 
				|| en instanceof ChickenEntity || en instanceof MonsterEntity || en instanceof PhantomEntity;
		if(bool && isInSpawnRange(en)) e.setResult(Result.DENY);
	}
	
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent e) {
		if(!Config.isSpawnProtEnabled() || Config.allowBreakBlocks() || canBypass(e.getPlayer())
				|| !isInSpawnRange((World) e.getWorld(), e.getPos())) return;
		
		e.setCanceled(true);
		e.getPlayer().sendMessage(ServerBase.TEXT.message("You can't break blocks in the spawn area.", MessageType.ERROR));
	}
	
	@SubscribeEvent
	public static void onBlockPlace(BlockEvent.EntityPlaceEvent e) {
		if(!Config.isSpawnProtEnabled() || Config.allowPlaceBlocks() 
				|| (e.getEntity() instanceof PlayerEntity && canBypass((PlayerEntity) e.getEntity()))
				|| !isInSpawnRange((World) e.getWorld(), e.getPos())) return;
		
		e.setCanceled(true);
		if(e.getEntity() instanceof PlayerEntity) ((PlayerEntity) e.getEntity()).sendMessage(ServerBase.TEXT.message("You can't place blocks in the spawn area.", MessageType.ERROR));
	}
	
	@SubscribeEvent
	public static void onEntityBlockForm(FrostWalkerEvent e) {
		if(!Config.isSpawnProtEnabled()
				|| (e.getEntity() instanceof PlayerEntity && canBypass((PlayerEntity) e.getEntity()))
				|| !isInSpawnRange(e.getEntity())) return;
		
		e.setCanceled(true);
	}
	
	@SubscribeEvent
	public static void onBucketEmpty(PlayerBucketFillEvent e) {
		if(Config.isSpawnProtEnabled() && !Config.allowBucketInteraction() 
				&& !canBypass(e.getPlayer()) && isInSpawnRange((World) e.getWorld(), e.getBlock()))
			e.setCanceled(true);
	}
	
	@SubscribeEvent
	public static void onBucketFill(PlayerBucketEmptyEvent e) {
		if(Config.isSpawnProtEnabled() && !Config.allowBucketInteraction() 
				&& !canBypass(e.getPlayer()) && isInSpawnRange((World) e.getWorld(), e.getBlock()))
			e.setCanceled(true);
	}

	public static boolean canBypass(PlayerEntity player) {
		return player.hasPermissionLevel(2) && player.abilities.isCreativeMode;
	}

	public static boolean isInSpawnRange(Entity en) {
		return isInSpawnRange(new Location(en.world, en.getPosX(), en.getPosY(), en.getPosZ()));
	}

	public static boolean isInSpawnRange(World w, Vec3d pos) {
		return isInSpawnRange(new Location(w, pos));
	}
	
	public static boolean isInSpawnRange(World w, BlockPos pos) {
		return isInSpawnRange(new Location(w, pos.getX(), pos.getY(), pos.getZ()));
	}

	public static boolean isInSpawnRange(Location loc) {
		return Config.getSpawnProtectedDimensions().contains(loc.getWorld().getDimension().getType().getRegistryName().toString()) 
				&& loc.squareDistanceTo(getOrigin(loc.world)) <= Config.getSpawnProtectionRange() * Config.getSpawnProtectionRange();
	}
	
	private static Location getOrigin(World w) {
		BlockPos spawnPoint = w.getSpawnPoint();
		return new Location(w, (double) spawnPoint.getX(), (double) spawnPoint.getY(), (double) spawnPoint.getZ());
	}

}
