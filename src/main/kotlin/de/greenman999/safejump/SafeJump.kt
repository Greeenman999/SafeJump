package de.greenman999.safejump

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterEntities
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import org.joml.Quaternionf
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
							val fallDamage = 0f.coerceAtLeast(height.toFloat() - 3.0f)
							val finalDamage = player.modifyAppliedDamage(world.damageSources.fall(), fallDamage)
							val death: Boolean = finalDamage >= player.health
							renderFloatOnBlock(finalDamage, pos.down(), context.matrixStack(), player.world, context.camera(), context.consumers(), death)
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

	private fun renderFloatOnBlock(value: Float, pos: BlockPos, matrices: MatrixStack, world: World, camera: Camera, vertexConsumers: VertexConsumerProvider?, death: Boolean) {
		val transformedPos = pos.toCenterPos().subtract(0.0, 0.5, 0.0).subtract(camera.pos)
		val outlineShape = world.getBlockState(pos).getOutlineShape(world, pos)
		val max = if (outlineShape.isEmpty) {
			1.0
		} else {
			outlineShape.getMax(Direction.Axis.Y)
		}
		matrices.push()
		matrices.translate(transformedPos.x, transformedPos.y + max, transformedPos.z)
		matrices.multiply(Quaternionf().fromAxisAngleDeg(1f, 0f, 0f, 90f))
		val size = 0.07f
		matrices.scale(-size, -size, size)
		val literal: Text = Text.literal(value.toString())
		val textRenderer = MinecraftClient.getInstance().textRenderer
		textRenderer.draw(
			literal,
			-textRenderer.getWidth(literal) / 2.0f,
			-3.5f,
			if(death) Colors.RED else Colors.WHITE,
			false,
			matrices.peek().positionMatrix,
			vertexConsumers,
			TextRenderer.TextLayerType.POLYGON_OFFSET,
			0,
			15728880
		)
		matrices.pop()
	}

}