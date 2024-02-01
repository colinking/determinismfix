## Determinism Fix

In a [Slay the Spire](https://store.steampowered.com/app/646570/Slay_the_Spire/) run, all randomness (e.g. card draw,
map layout, etc.) is determined by the run's seed -- with a few notable (likely unintentional) omissions.

This mod patches those cases such that their output is deterministic.

```
// All randomness in a Slay the Spire run is generated from the seed EXCEPT
// for three specific kinds of card transforms...
//
//   1. Living Wall event
//   2. Transmogrifier event
//   3. Neow's "Transform a card" bonus
//
// ...and DiscoveryAction's card generation (which triggers an RNG call on every update() call).
//
// (note: all other transforms (e.g. Neow's "Transform 2 cards" bonus) are determined
// by the seed.)
//
// In other words, you can reproduce an arbitrary Slay the Spire run by "replaying" the
// actions someone took in a run, UNLESS one of the events above is encountered. These
// events use an unseeded RNG and will produce different results every time. This could
// be abused e.g. to guarantee a specific transform by saving+continuing to reroll a
// transform until the desired card is produced.
//
// This seems like an oversight, as all other card transforms (including Neow's
// "Transform 2 cards" bonus) all used seeded RNGs.
//
// This patch replaces the unseeded RNG used by these three transform events with a
// seeded, uncorrelated RNG. It also implements caching for DiscoveryAction s.t. the
// number of RNG calls is deterministic.
```