package de.greenman999.safejump.mixin;

import de.greenman999.safejump.SafeJump;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Inject(at = @At("HEAD"), method = "render")
	private void init(DrawContext context, float tickDelta, CallbackInfo ci) {
		SafeJump.INSTANCE.onRenderInGameHud(context, tickDelta);
	}
}