package com.easysort.platform.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
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
	}
}
