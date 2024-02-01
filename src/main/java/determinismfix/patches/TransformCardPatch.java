package determinismfix.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.random.Random;
import determinismfix.DeterminismFix;

/** Patches non-determinism when transforming cards. See README. */
public class TransformCardPatch {
	// TODO: persist counter to save file
	// TODO: reset on continue
	private static Random rng;

	public static void onStartGame() {
		// We use a similar approach to RNGFix to avoid correlated randomness.
		// (For context, see: https://forgottenarbiter.github.io/Correlated-Randomness)
		Random seedRNG = new Random(Settings.seed);
		// However, to avoid correlating w/ RNGFix, we use a different RNG to create our seed.
		seedRNG = new Random(seedRNG.random.nextLong());

		rng = new Random(seedRNG.random.nextLong());
	}

	@SpirePatch(
		clz= AbstractDungeon.class,
		method="transformCard",
		paramtypez = {AbstractCard.class, boolean.class}
	)
	public static class Patch {
		public static SpireReturn<Void> Prefix(AbstractCard c, boolean autoUpgrade) {
			DeterminismFix.logger.info("Using seeded RNG to transform card ({})", c.name);

			// Vanilla STS does this:
			// transformCard(c, autoUpgrade, new Random());
			// We use a seeded RNG instead:
			AbstractDungeon.transformCard(c, autoUpgrade, rng);

			return SpireReturn.Return();
		}
	}
}
