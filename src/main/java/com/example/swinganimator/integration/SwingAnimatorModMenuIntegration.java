package com.example.swinganimator.integration;

import com.example.swinganimator.SwingAnimatorConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class SwingAnimatorModMenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parentScreen -> {
			SwingAnimatorConfig config = SwingAnimatorConfig.get();

			ConfigBuilder builder = ConfigBuilder.create()
					.setParentScreen(parentScreen)
					.setTitle(Component.literal("Swing Animator"))
					.setSavingRunnable(config::save);

			ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
			ConfigEntryBuilder entryBuilder = builder.entryBuilder();

			general.addEntry(entryBuilder
					.startBooleanToggle(Component.literal("Enable Custom Swing Speed"), config.enabled)
					.setDefaultValue(true)
					.setTooltip(Component.literal(
							"Turns the custom swing animation speed on or off. When off, vanilla timing is used."))
					.setSaveConsumer(value -> config.enabled = value)
					.build());

			general.addEntry(entryBuilder
					.startIntSlider(Component.literal("Swing Speed"), config.swingSpeedPercent, 25, 400)
					.setDefaultValue(100)
					.setTextGetter(value -> Component.literal(value + "%"))
					.setTooltip(Component.literal(
							"Speed of the arm swing animation. 100% is vanilla. This is purely visual - it does not "
									+ "change how fast you mine blocks or attack."))
					.setSaveConsumer(value -> config.swingSpeedPercent = value)
					.build());

			ConfigCategory position = builder.getOrCreateCategory(Component.literal("Hand Position"));

			position.addEntry(entryBuilder
					.startBooleanToggle(Component.literal("Enable Custom Hand Position"), config.positionEnabled)
					.setDefaultValue(false)
					.setTooltip(Component.literal(
							"Turns the hand/held-item position offset on or off. When off, vanilla positioning is used."))
					.setSaveConsumer(value -> config.positionEnabled = value)
					.build());

			position.addEntry(entryBuilder
					.startIntSlider(Component.literal("X Offset (left / right)"), config.offsetXHundredths, -100, 100)
					.setDefaultValue(0)
					.setTextGetter(value -> Component.literal(String.format("%.2f", value / 100.0f)))
					.setTooltip(Component.literal(
							"Shifts both hands left (negative) or right (positive). Purely visual."))
					.setSaveConsumer(value -> config.offsetXHundredths = value)
					.build());

			position.addEntry(entryBuilder
					.startIntSlider(Component.literal("Y Offset (down / up)"), config.offsetYHundredths, -100, 100)
					.setDefaultValue(0)
					.setTextGetter(value -> Component.literal(String.format("%.2f", value / 100.0f)))
					.setTooltip(Component.literal(
							"Shifts both hands down (negative) or up (positive). Purely visual."))
					.setSaveConsumer(value -> config.offsetYHundredths = value)
					.build());

			position.addEntry(entryBuilder
					.startIntSlider(Component.literal("Z Offset (near / far)"), config.offsetZHundredths, -100, 100)
					.setDefaultValue(0)
					.setTextGetter(value -> Component.literal(String.format("%.2f", value / 100.0f)))
					.setTooltip(Component.literal(
							"Shifts both hands toward (negative) or away from (positive) the camera. Purely visual."))
					.setSaveConsumer(value -> config.offsetZHundredths = value)
					.build());

			return builder.build();
		};
	}
}
