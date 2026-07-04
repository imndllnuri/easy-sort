package com.easysort.platform.neoforge;

import com.easysort.platform.neoforge.client.EasySortNeoForgeClient;
import com.easysort.platform.neoforge.network.NetworkSetup;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(EasySortNeoForge.MOD_ID)
public final class EasySortNeoForge {

	// NeoForge's modid validation rejects hyphens (unlike Fabric, which
	// allows them - confirmed earlier against Fabric's own ResourceLocation
	// validator). "easysort" here only affects NeoForge's internal loader
	// identifier; branding/display name/resource namespaces elsewhere are
	// unaffected.
	public static final String MOD_ID = "easysort";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public EasySortNeoForge(IEventBus modEventBus, ModContainer modContainer) {
		LOGGER.info("Easy Sort (NeoForge) initializing");
		modEventBus.addListener(NetworkSetup::register);

		// EasySortNeoForgeClient (and the client-only NeoForge/vanilla classes
		// it references) is only ever loaded when this branch actually runs,
		// so it never loads on a dedicated server.
		if (FMLEnvironment.getDist() == Dist.CLIENT) {
			EasySortNeoForgeClient.init(modEventBus);
		}
	}
}
