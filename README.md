## Determinism Fix

In Slay the Spire, if you discover a card via a potion (e.g. an attack, colorless, skill, or power potion) or via the
Discovery card, there is a bug where the RNG is called an arbitrary number of times. This makes the remainder of the
floor unpredictable. In other words, if you were to try to "replay" the actions someone took, you would not get the same
outcome. This is the only instance of "true randomness" in a Slay the Spire run and is very likely an unintentional bug
given that Foreign Influence does not have this same issue.

This mod fixes this bug such that the RNG is called a deterministic number of times. 

## Example

This bug can be replicated by first discovering a card via a potion or via the Discovery card. Once that happens,
anything that relies on the `cardRandomRng` RNG (which is generally used for random effects caused by playing a card)
will be unpredictable.

For example, if you had an attack and power potion, you could re-roll the power potion as many times as you want since
the outcome will be determined by how many times `cardRandomRng` was called when the attack potion was used.

# TODO: add example video of this

## The bug

The bug occurs within `DiscoveryAction`'s `update` method:

```java
// DiscoveryAction.java
public void update() {
      ArrayList generatedCards;
      if (this.returnColorless) {
         generatedCards = this.generateColorlessCardChoices();
      } else {
         generatedCards = this.generateCardChoices(this.cardType);
      }

      if (this.duration == Settings.ACTION_DUR_FAST) {
         AbstractDungeon.cardRewardScreen.customCombatOpen(generatedCards, CardRewardScreen.TEXT[1], this.cardType != null);
         this.tickDuration();
      } else {
         // ...
      }
  }
```

The `generate[...]CardChoices` method is called every time `update` is called -- which happens an arbitrary number of
times, depending on how often the UI is re-rendered. These methods then use `cardRandomRng` to generate cards, so each
`DiscoveryAction` will advance `cardRandomRng` an arbitrary number of times. This makes any actions that use
`cardRandomRng` unpredictable for the rest of the floor (generally this is any source of randomness that arises from
playing cards, e.g. which enemies are hit by Bouncing Flask). Note that this RNG is reset when advancing floors. 

This is very likely an unintentional bug. There's no reason for this to be truly random, and the developers were clearly
careful about generating all meaningful randomness from the seed. Additionally, there is almost identical "discovery"
behavior via Foreign Influence and it handles this correctly by generating cards exactly once:

```java
public void update() {
	if (this.duration == Settings.ACTION_DUR_FAST) {
		AbstractDungeon.cardRewardScreen.customCombatOpen(this.generateCardChoices(), CardRewardScreen.TEXT[1], true);
		this.tickDuration();
	} else {
		// ...
	}
}
```

In other words, this bug could be fixed by the Slay the Spire developers by changing `DiscoveryAction.java` like so
(which is effectively equivalent to what this mod does):

```diff
// DiscoveryAction.java
public void update() {
-     ArrayList generatedCards;
-     if (this.returnColorless) {
-        generatedCards = this.generateColorlessCardChoices();
-     } else {
-        generatedCards = this.generateCardChoices(this.cardType);
-     }
-
      if (this.duration == Settings.ACTION_DUR_FAST) {
+        ArrayList generatedCards;
+        if (this.returnColorless) {
+           generatedCards = this.generateColorlessCardChoices();
+        } else {
+           generatedCards = this.generateCardChoices(this.cardType);
+        }
+
         AbstractDungeon.cardRewardScreen.customCombatOpen(generatedCards, CardRewardScreen.TEXT[1], this.cardType != null);
         this.tickDuration();
      } else {
         // ...
      }
  }
```