package com.example.swinganimator.mixin;

import com.example.swinganimator.SwingAnimatorConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks the method that submits both first-person hands (and whatever
 * they're holding) for rendering, and nudges the whole pair by a
 * configurable offset before anything else touches the pose stack.
 *
 * This is purely cosmetic: it only moves where the hand model is drawn on
 * screen. It does not affect reach, hitboxes, interaction, or anything
 * else about gameplay.
 *
 * NOTE: Minecraft 26.2's render pipeline rewrite renamed this method from
 * "renderHandsWithItems" (1.21.x / 26.1.2) to "submitHandsWithItems", and
 * swapped its MultiBufferSource.BufferSource parameter for a
 * SubmitNodeCollector. The signature below targets the 26.2 name.
 */
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

	@Inject(method = "submitHandsWithItems", at = @At("HEAD"))
	private void swinganimator$offsetHands(float partialTick, PoseStack poseStack, SubmitNodeCollector collector,
			LocalPlayer player, int combinedLight, CallbackInfo ci) {
		SwingAnimatorConfig config = SwingAnimatorConfig.get();

		if (!config.positionEnabled) {
			return;
		}

		float x = config.getOffsetX();
		float y = config.getOffsetY();
		float z = config.getOffsetZ();

		if (x == 0.0f && y == 0.0f && z == 0.0f) {
			return;
		}

		// Applied before the method does its own pushPose()/popPose() calls
		// for each hand, so it shifts both hands together as one unit.
		poseStack.translate(x, y, z);
	}
}
