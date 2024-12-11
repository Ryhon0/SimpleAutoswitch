package xyz.ryhon.simpleautoswitch;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AutoswitchSettingsScreen extends Screen {
	Screen parent;

	protected AutoswitchSettingsScreen(Screen parent) {
		super(Text.empty());
		this.parent = parent;
	}

	SwitchButton enabledButton;
	SwitchButton returnToPreviousSlotButton;
	SwitchButton sneakToggleButton;
	ButtonWidget doneButton;

	@Override
	protected void init() {
		super.init();

		int buttonWidth = 48;
		int buttonHeight = 18;
		int panelWidth = 256;

		enabledButton = new SwitchButton(
				(width / 2) + (panelWidth / 2) - (buttonWidth), (height / 2) - (buttonHeight * 2),
				buttonWidth, buttonHeight, SimpleAutoswitch.enabled) {
			@Override
			public void setToggled(boolean toggled) {
				super.setToggled(toggled);
				SimpleAutoswitch.enabled = toggled;
			}
		};
		addDrawableChild(enabledButton);
		addSelectableChild(enabledButton);
		TextWidget t = new TextWidget(Text.translatable("simpleautoswitch.menuscreen.enabled"), textRenderer);
		t.setPosition((width / 2) - (panelWidth / 2),
				enabledButton.getY() + (buttonHeight / 2) - (textRenderer.fontHeight / 2));
		addDrawableChild(t);

		returnToPreviousSlotButton = new SwitchButton(
				enabledButton.getX(), enabledButton.getY() + enabledButton.getHeight(),
				enabledButton.getWidth(), enabledButton.getHeight(),
				SimpleAutoswitch.returnToPreviousSlot) {
			@Override
			public void setToggled(boolean toggled) {
				super.setToggled(toggled);
				SimpleAutoswitch.returnToPreviousSlot = toggled;
			}
		};
		addDrawableChild(returnToPreviousSlotButton);
		addSelectableChild(returnToPreviousSlotButton);
		t = new TextWidget(Text.translatable("simpleautoswitch.menuscreen.returnToPreviousSlot"), textRenderer);
		t.setPosition((width / 2) - (panelWidth / 2),
				returnToPreviousSlotButton.getY() + (buttonHeight / 2) - (textRenderer.fontHeight / 2));
		addDrawableChild(t);

		sneakToggleButton = new SwitchButton(
				returnToPreviousSlotButton.getX(),
				returnToPreviousSlotButton.getY() + returnToPreviousSlotButton.getHeight(),
				returnToPreviousSlotButton.getWidth(), returnToPreviousSlotButton.getHeight(),
				SimpleAutoswitch.sneakToggle) {
			@Override
			public void setToggled(boolean toggled) {
				super.setToggled(toggled);
				SimpleAutoswitch.sneakToggle = toggled;
			}
		};
		addDrawableChild(sneakToggleButton);
		addSelectableChild(sneakToggleButton);
		t = new TextWidget(Text.translatable("simpleautoswitch.menuscreen.sneakToggle"), textRenderer);
		t.setPosition((width / 2) - (panelWidth / 2),
				sneakToggleButton.getY() + (buttonHeight / 2) - (textRenderer.fontHeight / 2));
		addDrawableChild(t);

		doneButton = ButtonWidget.builder(Text.translatable("simpleautoswitch.menuscreen.done"), (ButtonWidget b) -> {
			close();
		})
				.size(96, 24)
				.position((width / 2) - (96 / 2), sneakToggleButton.getY() + sneakToggleButton.getHeight() + 8)
				.build();
		addDrawableChild(doneButton);
		addSelectableChild(doneButton);
	}

	@Override
	public void close() {
		client.setScreen(parent);
		SimpleAutoswitch.saveConfig();
	}

	public class SwitchButton extends ToggleButtonWidget {
		private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.of("widget/button"),
				Identifier.of("widget/button"), Identifier.of("widget/button_highlighted"));

		public SwitchButton(int x, int y, int width, int height, boolean toggled) {
			super(x, y, width, height, toggled);
			setTextures(TEXTURES);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (super.mouseClicked(mouseX, mouseY, button)) {
				setToggled(!toggled);
				return true;
			}
			return false;
		}

		@Override
		public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
			super.renderWidget(context, mouseX, mouseY, delta);
			context.drawCenteredTextWithShadow(textRenderer,
					Text.translatable("simpleautoswitch.switchbutton.label." + (toggled ? "on" : "off")),
					getX() + (width / 2), getY() + (height / 2) - (textRenderer.fontHeight / 2),
					toggled ? 0x00ff00 : 0xff0000);
		}
	}
}
