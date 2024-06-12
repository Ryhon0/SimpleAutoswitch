package xyz.ryhon.simpleautoswitch;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
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
	SwitchButton requiresAttackHeldButton;
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

		requiresAttackHeldButton = new SwitchButton(
				enabledButton.getX(), enabledButton.getY() + enabledButton.getHeight(),
				enabledButton.getWidth(), enabledButton.getHeight(),
				SimpleAutoswitch.requiresAttackHeld) {
			@Override
			public void setToggled(boolean toggled) {
				super.setToggled(toggled);
				SimpleAutoswitch.requiresAttackHeld = toggled;
			}
		};
		addDrawableChild(requiresAttackHeldButton);
		addSelectableChild(requiresAttackHeldButton);
		t = new TextWidget(Text.translatable("simpleautoswitch.menuscreen.requiresAttackHeld"), textRenderer);
		t.setPosition((width / 2) - (panelWidth / 2),
				requiresAttackHeldButton.getY() + (buttonHeight / 2) - (textRenderer.fontHeight / 2));
		addDrawableChild(t);

		returnToPreviousSlotButton = new SwitchButton(
				requiresAttackHeldButton.getX(), requiresAttackHeldButton.getY() + requiresAttackHeldButton.getHeight(),
				requiresAttackHeldButton.getWidth(), requiresAttackHeldButton.getHeight(),
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
		static final Identifier TEXTURE = new Identifier("textures/gui/sprites/widget/button.png");
		static final Identifier TEXTURE_HIGHLIGHTED = new Identifier("textures/gui/sprites/widget/button_highlighted.png");

		public SwitchButton(int x, int y, int width, int height, boolean toggled) {
			super(x, y, width, height, toggled);
		}

		@Override
		protected boolean clicked(double mouseX, double mouseY) {
			if (super.clicked(mouseX, mouseY)) {
				setToggled(!toggled);
				return true;
			}
			return false;
		}

		@Override
		public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
			RenderSystem.disableDepthTest();
			context.drawTexture(hovered ? TEXTURE_HIGHLIGHTED : TEXTURE, this.getX(), this.getY(), 0, 0,
					this.width, this.height, this.width, this.height);
			RenderSystem.enableDepthTest();
			context.drawCenteredTextWithShadow(textRenderer,
					Text.translatable("simpleautoswitch.switchbutton.label." + (toggled ? "on" : "off")),
					getX() + (width / 2), getY() + (height / 2) - (textRenderer.fontHeight / 2),
					toggled ? 0x00ff00 : 0xff0000);
		}
	}
}
