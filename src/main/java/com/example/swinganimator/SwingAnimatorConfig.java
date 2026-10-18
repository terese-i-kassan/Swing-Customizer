package com.example.swinganimator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Plain config POJO, persisted as JSON in the config folder.
 *
 * NOTE: swingSpeedPercent only changes how fast the swing animation
 * plays back visually. It never touches block-breaking speed or
 * attack-cooldown logic - those are handled by completely separate
 * game systems that this mod does not modify.
 */
public class SwingAnimatorConfig {

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = FabricLoader.getInstance()
			.getConfigDir()
			.resolve("swinganimator.json");

	private static SwingAnimatorConfig INSTANCE;

	public boolean enabled = true;

	// 100 = vanilla speed, 25 = 4x slower, 400 = 4x faster
	public int swingSpeedPercent = 100;

	// --- Hand position offset ---
	// Purely visual: shifts where the first-person hand(s)/held item render.
	// Stored as hundredths of a block so Cloth Config's int slider can be used
	// (-100..100 => -1.00..1.00 blocks on each axis).
	public boolean positionEnabled = false;
	public int offsetXHundredths = 0; // left(-) / right(+)
	public int offsetYHundredths = 0; // down(-) / up(+)
	public int offsetZHundredths = 0; // toward camera(-) / away(+)

	public static SwingAnimatorConfig get() {
		if (INSTANCE == null) {
			INSTANCE = load();
		}
		return INSTANCE;
	}

	private static SwingAnimatorConfig load() {
		if (Files.exists(CONFIG_PATH)) {
			try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
				SwingAnimatorConfig loaded = GSON.fromJson(reader, SwingAnimatorConfig.class);
				if (loaded != null) {
					loaded.sanitize();
					return loaded;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		SwingAnimatorConfig fresh = new SwingAnimatorConfig();
		fresh.save();
		return fresh;
	}

	public void sanitize() {
		if (swingSpeedPercent < 25) {
			swingSpeedPercent = 25;
		}
		if (swingSpeedPercent > 400) {
			swingSpeedPercent = 400;
		}
		offsetXHundredths = clampOffset(offsetXHundredths);
		offsetYHundredths = clampOffset(offsetYHundredths);
		offsetZHundredths = clampOffset(offsetZHundredths);
	}

	private static int clampOffset(int value) {
		if (value < -100) {
			return -100;
		}
		if (value > 100) {
			return 100;
		}
		return value;
	}

	public void save() {
		sanitize();
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Returns the multiplier applied to the swing animation's speed. 1.0 = vanilla. */
	public float getMultiplier() {
		return swingSpeedPercent / 100.0f;
	}

	public float getOffsetX() {
		return offsetXHundredths / 100.0f;
	}

	public float getOffsetY() {
		return offsetYHundredths / 100.0f;
	}

	public float getOffsetZ() {
		return offsetZHundredths / 100.0f;
	}
}
