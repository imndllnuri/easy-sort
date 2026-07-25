package com.easysort.platform.fabric.client.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/**
 * A flat, text-height button meant to sit inline with a screen's title row.
 * Vanilla's Button uses a beveled 3-slice texture that reads as too heavy at
 * that size/position, so this draws a plain filled rectangle instead.
 *
 * <p>GuiGraphics (and the whole extractContents()-based rendering API) is a
 * Minecraft 1.20+ addition - on 1.19.2, widgets render with a PoseStack and
 * GuiComponent's static fill()/blit() helpers instead, and AbstractButton's
 * onPress() takes no arguments.
 *
 * <p>Tooltip is a Minecraft 1.19.4+ addition (the reusable
 * AbstractWidget#setTooltip API) - on 1.19.2 this instead stores the tooltip
 * Component itself and leaves rendering it to the caller, which has access to
 * a Screen to call {@code Screen#renderTooltip} on (see
 * AbstractContainerScreenMixin).
 */
public final class MiniButton extends AbstractButton {

	private static final int COLOR_BORDER = 0xFF373737;
	private static final int COLOR_BACKGROUND = 0xFF8B8B8B;
	private static final int COLOR_BACKGROUND_HOVERED = 0xFFA0A0A0;
	private static final int COLOR_TEXT = 0xFFFFFFFF;
	private static final int COLOR_TEXT_DISABLED = 0xFF707070;
	private static final float TEXT_SCALE = 0.75f;

	private final Runnable onPress;
	private Component tooltip;

	public MiniButton(int x, int y, int size, Component message, Runnable onPress) {
		super(x, y, size, size, message);
		this.onPress = onPress;
	}

	public MiniButton setTooltip(Component tooltip) {
		this.tooltip = tooltip;
		return this;
	}

	public Component getTooltip() {
		return this.tooltip;
	}

	@Override
	public void onPress() {
		this.onPress.run();
	}

	@Override
	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		int background = this.active && this.isHovered ? COLOR_BACKGROUND_HOVERED : COLOR_BACKGROUND;
		GuiComponent.fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, COLOR_BORDER);
		GuiComponent.fill(poseStack, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, background);

		Font font = Minecraft.getInstance().font;
		int textColor = this.active ? COLOR_TEXT : COLOR_TEXT_DISABLED;
		int textWidth = font.width(this.getMessage());

		poseStack.pushPose();
		poseStack.translate(this.x + this.width / 2f, this.y + this.height / 2f, 0);
		poseStack.scale(TEXT_SCALE, TEXT_SCALE, 1f);
		font.draw(poseStack, this.getMessage(), -textWidth / 2f, -4, textColor);
		poseStack.popPose();
	}

	@Override
	public void updateNarration(NarrationElementOutput output) {
		this.defaultButtonNarrationText(output);
	}
}
