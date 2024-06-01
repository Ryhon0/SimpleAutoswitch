package xyz.ryhon.simpleautoswitch;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class SimpleAutoswitch implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("simple-autoswitch");

	static public boolean enabled = true;
	static public boolean requiresAttackHeld = true;
	static public boolean returnToPreviousSlot = true;
	static public boolean sneakToggle = true;

	boolean wasPressed = false;
	static public boolean tempDisabled = false;
	static public int previousSlot = 0;

	int ticks = 0;
	static final int autoSaveTicks = 20 * 60 * 3;

	@Override
	public void onInitialize() {
		{
			loadConfig();

			String bindCategory = "category.simpleautoswitch";
			KeyBinding toggleBind = new KeyBinding("key.simpleautoswitch.toggle", InputUtil.Type.KEYSYM,
					GLFW.GLFW_KEY_UNKNOWN, bindCategory);
			KeyBindingHelper.registerKeyBinding(toggleBind);

			KeyBinding menuBind = new KeyBinding("key.simpleautoswitch.menu", InputUtil.Type.KEYSYM,
					GLFW.GLFW_KEY_UNKNOWN, bindCategory);
			KeyBindingHelper.registerKeyBinding(menuBind);

			ClientTickEvents.START_CLIENT_TICK.register((client) -> {
				ticks++;
				if (ticks == autoSaveTicks) {
					ticks = 0;
					saveConfig();
				}

				if (menuBind.wasPressed())
					client.setScreen(new AutoswitchSettingsScreen(null));

				if (toggleBind.wasPressed())
					enabled = !enabled;

				boolean sw = shouldSwitch(client);

				if (!wasPressed && sw) {
					tempDisabled = false;
					previousSlot = client.player.getInventory().selectedSlot;
				}

				if (sw) {
					switchToBestSlot(client);

					for (int i = 0; i < client.options.hotbarKeys.length; i++) {
						if (client.options.hotbarKeys[i].isPressed()) {
							tempDisabled = true;
							previousSlot = i;
						}
					}
				}

				if (wasPressed && !sw) {
					if (returnToPreviousSlot) {
						client.player.getInventory().selectedSlot = previousSlot;
						client.interactionManager.syncSelectedSlot();
					}
					tempDisabled = false;
				}

				wasPressed = sw;
			});
		}
	}

	boolean shouldSwitch(MinecraftClient client) {
		if (client.player == null)
			return false;

		if (!enabled)
			return false;

		if (sneakToggle && client.player.isSneaking())
			return false;

		if (requiresAttackHeld)
			return client.options.attackKey.isPressed();
		else
			return true;
	}

	void switchToBestSlot(MinecraftClient client) {
		if (client == null || client.player == null || client.crosshairTarget == null || tempDisabled)
			return;
		ClientWorld world = client.world;
		ClientPlayerEntity p = client.player;
		HitResult hit = client.crosshairTarget;
		if (hit.getType() == HitResult.Type.BLOCK) {
			BlockHitResult blockHit = (BlockHitResult) hit;
			BlockState state = world.getBlockState(blockHit.getBlockPos());
			if (state.isAir())
				return;

			int oldSlot = p.getInventory().selectedSlot;
			int currentSlot = p.getInventory().selectedSlot;
			float maxDelta = state.calcBlockBreakingDelta(p, p.getWorld(), blockHit.getBlockPos());

			for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
				p.getInventory().selectedSlot = i;
				float delta = state.calcBlockBreakingDelta(p, p.getWorld(), blockHit.getBlockPos());
				delta = Math.min(delta, 1f);
				if (delta > maxDelta) {
					maxDelta = delta;
					currentSlot = i;
				}
			}
			p.getInventory().selectedSlot = currentSlot;

			if (oldSlot != currentSlot)
				client.interactionManager.syncSelectedSlot();
		}
	}

	static Path configDir = FabricLoader.getInstance().getConfigDir().resolve("simpleautoswitch");
	static Path configFile = configDir.resolve("config.json");

	static void loadConfig() {
		try {
			Files.createDirectories(configDir);
			if (!Files.exists(configFile))
				return;

			String str = Files.readString(configFile);
			JsonObject jo = (JsonObject) JsonParser.parseString(str);

			if (jo.has("enabled"))
				enabled = jo.get("enabled").getAsBoolean();
			if (jo.has("requiresAttackHeld"))
				requiresAttackHeld = jo.get("requiresAttackHeld").getAsBoolean();
			if (jo.has("returnToPreviousSlot"))
				returnToPreviousSlot = jo.get("returnToPreviousSlot").getAsBoolean();
			if (jo.has("sneakToggle"))
				sneakToggle = jo.get("sneakToggle").getAsBoolean();

		} catch (Exception e) {
			LOGGER.error("Failed to load config", e);
		}
	}

	static void saveConfig() {
		JsonObject jo = new JsonObject();

		jo.add("enabled", new JsonPrimitive(enabled));
		jo.add("requiresAttackHeld", new JsonPrimitive(requiresAttackHeld));
		jo.add("returnToPreviousSlot", new JsonPrimitive(returnToPreviousSlot));
		jo.add("sneakToggle", new JsonPrimitive(sneakToggle));

		try {
			Files.createDirectories(configDir);
			Files.writeString(configFile, new Gson().toJson(jo));
		} catch (Exception e) {
			LOGGER.error("Failed to save config", e);
		}
	}
}