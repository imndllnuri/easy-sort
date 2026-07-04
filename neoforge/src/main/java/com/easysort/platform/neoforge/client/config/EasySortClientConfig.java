package com.easysort.platform.neoforge.client.config;

import com.easysort.core.sort.SortKey;
import com.easysort.platform.neoforge.EasySortNeoForge;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Per-player sort preference, persisted client-side. Sent along with each
 * sort request rather than read server-side, so the preference follows the
 * player across servers instead of being a per-server/global setting.
 */
public final class EasySortClientConfig {

	private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("easy-sort.json");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final int CURRENT_VERSION = 1;

	private static SortKey primarySortKey = SortKey.MOD_ID;

	private EasySortClientConfig() {
	}

	public static SortKey getPrimarySortKey() {
		return primarySortKey;
	}

	public static void setPrimarySortKey(SortKey key) {
		primarySortKey = key;
		save();
	}

	public static void load() {
		if (!Files.exists(FILE)) {
			return;
		}
		try (Reader reader = Files.newBufferedReader(FILE)) {
			Data data = GSON.fromJson(reader, Data.class);
			if (data != null && data.configVersion == CURRENT_VERSION && data.primarySortKey != null) {
				primarySortKey = SortKey.valueOf(data.primarySortKey);
			}
		} catch (IOException | RuntimeException e) {
			EasySortNeoForge.LOGGER.warn("Failed to load Easy Sort config, using defaults", e);
		}
	}

	private static void save() {
		try {
			Files.createDirectories(FILE.getParent());
			Data data = new Data();
			data.configVersion = CURRENT_VERSION;
			data.primarySortKey = primarySortKey.name();
			try (Writer writer = Files.newBufferedWriter(FILE)) {
				GSON.toJson(data, writer);
			}
		} catch (IOException e) {
			EasySortNeoForge.LOGGER.warn("Failed to save Easy Sort config", e);
		}
	}

	private static final class Data {
		int configVersion;
		String primarySortKey;
	}
}
