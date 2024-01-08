package de.greenman999.safejump.renderer

import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.Camera
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import org.joml.Quaternionf

class HeightLevelRenderer {

    fun renderHeightLevel(height: Int, pos: BlockPos, matrices: MatrixStack, world: World, camera: Camera, vertexConsumers: VertexConsumerProvider?) {
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
        val literal: Text = Text.literal(height.toString())
        val textRenderer = MinecraftClient.getInstance().textRenderer
        textRenderer.draw(
            literal,
            -textRenderer.getWidth(literal) / 2.0f,
            -3.5f,
            -0x1,
            false,
            matrices.peek().positionMatrix,
            vertexConsumers,
            TextRenderer.TextLayerType.SEE_THROUGH,
            0,
            15728880
        )
        matrices.pop()
    }
}