package determinismfix.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.unique.DiscoveryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import determinismfix.DeterminismFix;
import determinismfix.DiscoveryActionCardCache;

import java.util.ArrayList;
import java.util.stream.Collectors;

/** Patches non-determinism in DiscoveryAction. See README. */
public class DiscoveryActionPatch {
	public static DiscoveryActionCardCache cache;

	public static void onStartGame() {
		cache = new DiscoveryActionCardCache();
	}

	@SpirePatch(clz = DiscoveryAction.class, method="generateColorlessCardChoices")
	@SpirePatch(clz = DiscoveryAction.class, method="generateCardChoices")
	public static class GeneratePatch {
		public static SpireReturn<ArrayList<AbstractCard>> Prefix(DiscoveryAction instance) {
			// Check if we have already generated cards for this DiscoveryAction.
			ArrayList<AbstractCard> cards = cache.get(instance);

			if (cards != null) {
				// Log for debugging
				String cardStr = cards.stream().map((AbstractCard c) -> c.name).collect(Collectors.joining(", "));
				DeterminismFix.logger.info("Using cached cards for DiscoveryAction ({}): {}", instance.hashCode(), cardStr);

				// Avoid generating cards again by returning the cached cards.
				return SpireReturn.Return(cards);
			}

			// Generate new cards. These will be cached via the Postfix patch below.
			return SpireReturn.Continue();
		}

		public static ArrayList<AbstractCard> Postfix(ArrayList<AbstractCard> returnValue, DiscoveryAction instance) {
			// Check if returned cards have been cached yet.
			ArrayList<AbstractCard> cards = cache.get(instance);
			if (cards == null) {
				// Log for debugging
				String cardStr = cards.stream().map((AbstractCard c) -> c.name).collect(Collectors.joining(", "));
				DeterminismFix.logger.info("Caching cards for DiscoveryAction ({}): {}", instance.hashCode(), cardStr);

				// We just generated cards for this DiscoveryAction for this first time. Cache them.
				cache.set(instance, returnValue);
			}

			return returnValue;
		}
	}
}
