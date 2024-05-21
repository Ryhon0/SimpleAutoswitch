package xyz.ryhon.simpleautoswitch.mixin;

import net.minecraft.entity.player.PlayerInventory;
import xyz.ryhon.simpleautoswitch.SimpleAutoswitch;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
	@Inject(at = @At("TAIL"), method = "scrollInHotbar")
	private void scrollInHotbar(double scrollAmount, CallbackInfo info) {
		SimpleAutoswitch.tempDisabled = true;
		SimpleAutoswitch.previousSlot = ((PlayerInventory)(Object)this).selectedSlot;
	}
}