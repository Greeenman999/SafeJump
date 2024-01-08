package de.greenman999.safejump

import de.greenman999.safejump.renderer.HeightLevelRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterEntities
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.lwjgl.glfw.GLFW
import org.slf4j.LoggerFactory


object SafeJump : ClientModInitializer {
    private val logger = LoggerFactory.getLogger("safejump")
	lateinit var keyBinding: KeyBinding
	private var isPressed: Boolean = false

	override fun onInitializeClient() {
		keyBinding = KeyBindingHelper.registerKeyBinding(
			KeyBinding(
				"key.safejump.show",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_LEFT_SHIFT,
				"category.safejump.general"
			)
		)

		WorldRenderEvents.AFTER_ENTITIES.register(AfterEntities { context ->
			if(!isPressed) return@AfterEntities
			val player = MinecraftClient.getInstance().player ?: return@AfterEntities
			val world = player.world
			val playerPos: BlockPos = player.blockPos
			val from = playerPos.add(-2, 0, -2)
			val to = playerPos.add(2, 0, 2)
			for (x in from.x..to.x) {
				for (z in from.z..to.z) {
					for (y in playerPos.y downTo -64) {
						val pos = BlockPos(x, y, z)
						val height = playerPos.y - y
						if(height < 0) {
							continue
						}
						if(height == 0) {
							if(!isAir(world, pos.down())) {
								break
							}
						}
						if(isAir(world, pos) && !isAir(world, pos.down())) {
							HeightLevelRenderer().renderHeightLevel(height, pos.down(), context.matrixStack(), player.world, context.camera(), context.consumers())
							break
						}
					}
				}
			}

		})
	}

	private fun isAir(world: World, pos: BlockPos): Boolean {
		return world.getBlockState(pos).isAir
	}

	fun onRenderKeyPressed(action: Int) {
		isPressed = action == GLFW.GLFW_PRESS
	}

}