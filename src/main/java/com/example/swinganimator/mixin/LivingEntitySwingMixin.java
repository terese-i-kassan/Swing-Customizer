package com.example.swinganimator.mixin;

import com.example.swinganimator.SwingAnimatorConfig;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Hooks the internal method that decides how many ticks the arm-swing
 * animation takes to play out (getCurrentSwingDuration).
 *
 * This value is PURELY cosmetic - it only controls how fast the swing
 * interpolates for rendering. It is completely separate from:
 *   - block breaking speed (handled server-side via tool efficiency / hardness)
 *   - attack cooldown / attack damage (handled via the attack_speed attribute)
 *
 * Because this mod's fabric.mod.json declares "environment": "client",
 * this mixin is never even loaded on a dedicated server.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntitySwingMixin {

	@Inject(method = "getCurrentSwingDuration", at = @At("RETURN"), cancellable = true)
	private void swinganimator$modifySwingDuration(CallbackInfoReturnable<Integer> cir) {
		SwingAnimatorConfig config = SwingAnimatorConfig.get();

		if (!config.enabled) {
			return;
		}

		float multiplier = config.getMultiplier();
		if (multiplier == 1.0f) {
			return;
		}

		int vanillaDuration = cir.getReturnValue();
		int adjusted = Math.round(vanillaDuration / multiplier);

		// Never let the animation collapse to 0 ticks - that would make it
		// pop instantly instead of playing, which looks broken rather than "fast".
		if (adjusted < 1) {
			adjusted = 1;
		}

		cir.setReturnValue(adjusted);
	}
}
