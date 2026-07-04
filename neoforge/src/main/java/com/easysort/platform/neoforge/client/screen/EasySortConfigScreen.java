package com.easysort.platform.neoforge.client.screen;

import com.easysort.core.sort.SortKey;
import com.easysort.platform.neoforge.client.config.EasySortClientConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class EasySortConfigScreen extends Screen {

	private final Screen parent;
	private SortKey primarySortKey;

	public EasySortConfigScreen(Screen parent) {
		super(Component.translatable("easy-sort.config.title"));
		this.parent = parent;
		this.primarySortKey = EasySortClientConfig.getPrimarySortKey();
	}

	@Override
	protected void init() {
		this.addRenderableWidget(Button.builder(sortByLabel(), button -> {
					primarySortKey = next(primarySortKey);
					button.setMessage(sortByLabel());
				})
				.bounds(this.width / 2 - 100, this.height / 2 - 20, 200, 20)
				.build());

		this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> {
					EasySortClientConfig.setPrimarySortKey(primarySortKey);
					this.onClose();
				})
				.bounds(this.width / 2 - 100, this.height / 2 + 10, 200, 20)
				.build());
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(parent);
	}

	private Component sortByLabel() {
		return Component.translatable("easy-sort.config.sort_by", Component.translatable(keyTranslationKey(primarySortKey)));
	}

	private static SortKey next(SortKey key) {
		SortKey[] values = SortKey.values();
		return values[(key.ordinal() + 1) % values.length];
	}

	private static String keyTranslationKey(SortKey key) {
		return "easy-sort.sort_key." + key.name().toLowerCase(Locale.ROOT);
	}
}
