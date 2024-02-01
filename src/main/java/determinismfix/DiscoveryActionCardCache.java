package determinismfix;

import com.megacrit.cardcrawl.actions.unique.DiscoveryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;

import java.util.ArrayList;

public class DiscoveryActionCardCache {
	private final ArrayList<CacheResult> elements;

	public DiscoveryActionCardCache() {
		this.elements = new ArrayList<>();
	}

	private static class CacheResult {
		public DiscoveryAction action;
		public ArrayList<AbstractCard> cards;

		public CacheResult(DiscoveryAction action, ArrayList<AbstractCard> cards) {
			this.action = action;
			this.cards = cards;
		}
	}

	public ArrayList<AbstractCard> get(DiscoveryAction action) {
		for (CacheResult element : this.elements) {
			if (element.action == action) {
				return element.cards;
			}
		}
		return null;
	}

	public void set(DiscoveryAction action, ArrayList<AbstractCard> cards) {
		for (CacheResult element : this.elements) {
			if (element.action == action) {
				element.cards = cards;
				return;
			}
		}
		this.elements.add(new CacheResult(action, cards));
	}
}
