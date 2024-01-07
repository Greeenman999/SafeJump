package de.greenman999.safejump

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory


object SafeJump : ClientModInitializer {
    private val logger = LoggerFactory.getLogger("safejump");
	lateinit var keyBinding: KeyBinding;
	private var isPressed: Boolean = false;

	override fun onInitializeClient() {
		keyBinding = KeyBindingHelper.registerKeyBinding(
			KeyBinding(
				"key.safejump.show",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_LEFT_SHIFT,
				"category.safejump.general"
			)
		)
	}

	fun onRenderInGameHud(context: DrawContext, tickDelta: Float) {
		if(isPressed) {
			context.drawText(MinecraftClient.getInstance().textRenderer, Text.literal("Pressed"), 10, 10, 0xFFFFFF, true);
		}
	}

	fun onRenderKeyPressed(action: Int) {
		isPressed = action == GLFW.GLFW_PRESS;
	}
}