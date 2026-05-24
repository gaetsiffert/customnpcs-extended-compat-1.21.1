# CustomNPCs Scoreboard Compat

CustomNPCs Scoreboard Compat is a NeoForge 1.21.1 compatibility mod for `CustomNPCs-Unofficial-NeoForge-1.21.1.20251230`.

Its goal is simple: make CustomNPCs scoreboard conditions usable again on the affected build.

## What it fixes

Without this compat mod, using scoreboards in CustomNPCs availability conditions can cause problems such as:

- crashes when rejoining a world or server
- join failures that may appear as `Invalid player data`
- errors after changing a scoreboard value
- NPC marks disappearing after reconnecting to a dedicated server
- mark or child dialog scoreboard conditions not reacting after reconnect until edited again

This mod is meant for players, server owners, and modpacks that want to use scoreboards to drive:

- NPC dialog availability
- NPC dialog option availability
- NPC quest availability
- NPC mark availability
- progression links with other mods or server systems

## What to expect

With this mod installed, scoreboard-based CustomNPCs conditions should be stable again on the targeted version set. This includes conditions that are evaluated client-side, such as marks above NPCs and child dialog options. NPC marks are also saved and resynced correctly after reconnecting to a dedicated server.

This mod does not add new gameplay or new scoreboard features. It is only a compatibility fix.

It also includes an optional CNPC-Gecko-Addon compatibility fix for Gecko animation sync scripts that call `syncAnimationsFor(...)`. When CNPC-Gecko-Addon is installed, the compat registers the addon's animation sync payloads correctly and fixes the returned payload IDs so those scripts no longer disconnect the client on the targeted NeoForge version.

It also fixes two CustomNPCs scoreboard scripting API issues on the affected build: scripts can safely set scores for offline or fake scoreboard names, and `deletePlayerScore(...)` now deletes the score instead of removing the player from their scoreboard team.

## Installation

### Dedicated server

- install CustomNPCs on the server
- install this mod on the server
- clients can still join without this compat mod for scoreboard-only use
- for CNPC-Gecko-Addon animation sync scripts, install this compat mod wherever CNPC-Gecko-Addon is installed
- scoreboard scripting API fixes run on the server side

This means the mod is server-required and client-optional on dedicated servers for scoreboard-only use. It is not strictly server-side only: the jar is built for both sides and includes optional client compatibility code for CNPC-Gecko-Addon mark rendering.

### Singleplayer / LAN

- install CustomNPCs
- install this mod in the same game instance

Singleplayer still uses an integrated server, so the mod is needed there too.

## Important removal warning

This mod fixes the issue while it is installed.

If you remove it later but keep NPC dialogs or quests that still rely on scoreboard conditions on the affected CustomNPCs build, the original crashes or join issues can come back.

## Recommended usage

For server setups, it is usually cleaner to use dedicated `dummy` objectives for progression bridges, for example:

```mcfunction
/scoreboard objectives add cnpc_progress dummy "CNPC Progress"
```

That makes it easier to connect CustomNPCs with:

- quests
- commands
- datapacks
- admin automation
- other progression systems

## Support and questions

If you find a bug, please report it in the comments or open an issue. You can also contact me directly on CurseForge/Modrinth for more specific questions !
If you want to use or modify this code, please check the license first.
More detailed change notes and technical context are available on GitHub.

## Notes

- server-required and client-optional on dedicated servers for scoreboard-only use
- not strictly server-side only; optional client compatibility code is included
- still required in singleplayer
- focused on compatibility, not feature expansion
- uses vanilla scoreboard packets and CustomNPCs packets only; no compat-specific client packet is required
- CNPC-Gecko-Addon support is optional and inactive when that addon is absent

tested with :
- Minecraft `1.21.1`
- NeoForge `21.1.230`
- `CustomNPCs-Unofficial-NeoForge-1.21.1.20251230` (testing with another CustomNPCs version is at the user's own risk)
- optional CNPC-Gecko-Addon `1.21.1.20251029`
