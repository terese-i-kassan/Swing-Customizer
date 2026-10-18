package com.example.swinganimator;

import net.fabricmc.api.ClientModInitializer;

public class SwingAnimatorMod implements ClientModInitializer {

	public static final String MOD_ID = "swinganimator";

	@Override
	public void onInitializeClient() {
		// Just make sure the config is loaded (and written to disk if missing)
		// as early as possible.
		SwingAnimatorConfig.get();
	}
}
