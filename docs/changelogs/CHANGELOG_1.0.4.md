# Changelog 1.0.4

## Fixed

- fixed CustomNPCs scoreboard scripting API crashes when `setPlayerScore(...)`, `getPlayerScore(...)`, or `hasPlayerObjective(...)` is called with an offline player name or fake scoreboard holder name
- fixed CustomNPCs scoreboard scripting API `deletePlayerScore(...)` removing the player from their scoreboard team instead of deleting the requested score

## Changed

- CustomNPCs scoreboard scripting API score access now uses vanilla scoreboard name holders instead of resolving only online players

## Compatibility

- scoreboard condition behavior is unchanged
- the fix is scoped to `noppes.npcs.api.wrapper.ScoreboardWrapper`
