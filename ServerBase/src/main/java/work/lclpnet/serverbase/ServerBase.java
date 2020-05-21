package work.lclpnet.serverbase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.custom.ServerReloadedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import work.lclpnet.core.util.ComponentSupplier;
import work.lclpnet.serverbase.prot.ProtectionListener;

@Mod(ServerBase.MODID)
public class ServerBase {
	
	public static final String MODID = "serverbase";
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ComponentSupplier TEXT = new ComponentSupplier("ServerBase");

	public ServerBase() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		IEventBus bus = MinecraftForge.EVENT_BUS;
		bus.register(this);
		bus.register(new ProtectionListener());
	}

	private void setup(final FMLCommonSetupEvent event) { //preinit
		LOGGER.info("ServerBase initializing...");

		Config.load();
		
		LOGGER.info("ServerBase initialized.");
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent e) {
		LOGGER.info("ServerBase starting...");
	}

	@SubscribeEvent
	public void onServerStarted(FMLServerStartedEvent e) {
		LOGGER.info("ServerBase started.");
	}
	
	@SubscribeEvent
	public void onServerStop(FMLServerStoppingEvent e) {
		LOGGER.info("ServerBase stopping...");
	}
	
	@SubscribeEvent
	public void onServerStopped(FMLServerStoppedEvent e) {
		LOGGER.info("ServerBase stopped.");
	}

	@SubscribeEvent
	public void onServerReloaded(ServerReloadedEvent e) {
		LOGGER.info("ServerBase reloading...");

		Config.load();
		
		LOGGER.info("ServerBase reloaded.");
	}
	
}
