package determinismfix;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.PreStartGameSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.Patcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import determinismfix.patches.TransformCardPatch;
import determinismfix.patches.DiscoveryActionPatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scannotation.AnnotationDB;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@SpireInitializer
public class DeterminismFix implements PostInitializeSubscriber, PreStartGameSubscriber {
	public static ModInfo info;
	public static String modID;
	static { loadModInfo(); }

	public static final Logger logger = LogManager.getLogger(modID);

	public DeterminismFix() {
		BaseMod.subscribe(this);
	}

	// https://github.com/kiooeht/ModTheSpire/wiki/SpireInitializer
	public static void initialize() {
		new DeterminismFix();
	}

	@Override
	public void receivePreStartGame() {
		DiscoveryActionPatch.onStartGame();
		TransformCardPatch.onStartGame();
	}

	@Override
	public void receivePostInitialize() {
		// Set up the mod information displayed in the in-game mods menu.
		BaseMod.registerModBadge(ImageMaster.loadImage("badge.png"), info.Name, info.Authors[0], info.Description, null);
	}

	// This determines the mod's ID based on information stored by ModTheSpire.
	private static void loadModInfo() {
		Optional<ModInfo> infos = Arrays.stream(Loader.MODINFOS).filter((modInfo)->{
			AnnotationDB annotationDB = Patcher.annotationDBMap.get(modInfo.jarURL);
			if (annotationDB == null)
				return false;
			Set<String> initializers = annotationDB.getAnnotationIndex().getOrDefault(SpireInitializer.class.getName(), Collections.emptySet());
			return initializers.contains(DeterminismFix.class.getName());
		}).findFirst();
		if (infos.isPresent()) {
			info = infos.get();
			modID = info.ID;
		}
		else {
			throw new RuntimeException("Failed to determine mod info/ID based on initializer.");
		}
	}
}
