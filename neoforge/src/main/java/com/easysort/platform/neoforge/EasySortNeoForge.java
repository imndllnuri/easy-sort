package com.easysort.platform.neoforge;

import com.easysort.platform.neoforge.client.EasySortNeoForgeClient;
import com.easysort.platform.neoforge.network.NetworkSetup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.eventbus.api.IEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(EasySortNeoForge.MOD_ID)
public final class EasySortNeoForge {

	// NeoForge's modid validation rejects hyphens (unlike Fabric, which
	// allows them - confirmed earlier against Fabric's own Identifier
	// validator). "easysort" here only affects NeoForge's internal loader
	// identifier; branding/display name/resource namespaces elsewhere are
	// unaffected.
	public static final String MOD_ID = "easysort";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// 1.20.1's mod entrypoint is a plain no-arg constructor that fetches the
	// mod event bus from FMLJavaModLoadingContext - the constructor-injected
	// IEventBus/ModContainer parameters (used on the current NeoForge
	// mainline, see mc/26.1.2 and later) are a later addition.
	public EasySortNeoForge() {
		LOGGER.info("Easy Sort (NeoForge) initializing");
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		NetworkSetup.register();

		// EasySortNeoForgeClient (and the client-only NeoForge/vanilla classes
		// it references) is only ever loaded when this branch actually runs,
		// so it never loads on a dedicated server.
		if (FMLEnvironment.dist == Dist.CLIENT) {
			EasySortNeoForgeClient.init(modEventBus);
		}
	}
}
