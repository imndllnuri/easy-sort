package com.easysort.platform.fabric.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

/**
 * A flat, text-height button meant to sit inline with a screen's title row.
 * Vanilla's Button uses a beveled 3-slice texture that reads as too heavy at
 * that size/position, so this draws a plain filled rectangle instead.
 */
public final class MiniButton extends AbstractButton {

	private static final int COLOR_BORDER = 0xFF373737;
	private static final int COLOR_BACKGROUND = 0xFF8B8B8B;
	private static final int COLOR_BACKGROUND_HOVERED = 0xFFA0A0A0;
	private static final int COLOR_TEXT = 0xFFFFFFFF;
	private static final int COLOR_TEXT_DISABLED = 0xFF707070;
	private static final float TEXT_SCALE = 0.75f;

	private final Runnable onPress;

	public MiniButton(int x, int y, int size, Component message, Runnable onPress) {
		super(x, y, size, size, message);
		this.onPress = onPress;
	}

	@Override
	public void onPress(InputWithModifiers input) {
		this.onPress.run();
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
		int background = this.active && this.isHovered() ? COLOR_BACKGROUND_HOVERED : COLOR_BACKGROUND;
		graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), COLOR_BORDER);
		graphics.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, background);

		Font font = Minecraft.getInstance().font;
		int textColor = this.active ? COLOR_TEXT : COLOR_TEXT_DISABLED;
		int textWidth = font.width(getMessage());

		graphics.pose().pushMatrix();
		graphics.pose().translate(getX() + getWidth() / 2f, getY() + getHeight() / 2f);
		graphics.pose().scale(TEXT_SCALE, TEXT_SCALE);
		graphics.text(font, getMessage(), -textWidth / 2, -4, textColor);
		graphics.pose().popMatrix();
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		this.defaultButtonNarrationText(output);
	}
}
