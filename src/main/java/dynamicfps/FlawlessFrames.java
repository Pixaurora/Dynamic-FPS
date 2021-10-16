package dynamicfps;

import net.fabricmc.loader.api.FabricLoader;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 Implements the FREX Flawless Frames API to allow other mods to request all frames to be processed until requested to
 go back to normal operation, such as ReplayMod rendering a video.<p>
 
 See https://github.com/grondag/frex/pull/9
 */
public class FlawlessFrames {
	private static final Set<Object> ACTIVE = ConcurrentHashMap.newKeySet();
	
	@SuppressWarnings("unchecked") // Since we ask for Consumer.class, it's unchecked to pass a Consumer<Boolean> to it. But it's guaranteed to ask it by API
	static void onClientInitialization() {
		Function<String, Consumer<Boolean>> provider = name -> {
			Object token = new Object();
			return active -> {
				if (active) {
					ACTIVE.add(token);
				} else {
					ACTIVE.remove(token);
				}
			};
		};
		FabricLoader.getInstance()
			.getEntrypoints("frex_flawless_frames", Consumer.class)
			.forEach(api -> api.accept(provider));
	}
	
	/**
	 Returns whether one or more mods have requested Flawless Frames to be active, and therefore frames should not be skipped.
	 */
	public static boolean isActive() {
		return !ACTIVE.isEmpty();
	}
}