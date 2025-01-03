package xyz.ryhon.simpleautoswitch.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.client.Mouse;
import xyz.ryhon.simpleautoswitch.SimpleAutoswitch;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {
	@Inject(at = @At("TAIL"), method = "onMouseScroll")
	private void scrollInHotbar(long window, double horizontal, double vertical, CallbackInfo info) {
		if(((Mouse)(Object)this).client == null || ((Mouse)(Object)this).client.player == null) return;

		PlayerInventory lv = ((Mouse)(Object)this).client.player.getInventory();

		if(lv.selectedSlot != SimpleAutoswitch.previousSlot)
		{
			SimpleAutoswitch.tempDisabled = true;
			SimpleAutoswitch.previousSlot = lv.selectedSlot;
		}
	}
}