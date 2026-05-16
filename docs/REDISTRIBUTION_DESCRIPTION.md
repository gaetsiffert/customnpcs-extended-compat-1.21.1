# CustomNPCs Scoreboard Compat

CustomNPCs Scoreboard Compat is a NeoForge 1.21.1 compatibility mod for `CustomNPCs-Unofficial-NeoForge-1.21.1.20251230`.

Its goal is simple: make CustomNPCs scoreboard conditions usable again on the affected build.

## What it fixes

Without this compat mod, using scoreboards in CustomNPCs availability conditions can cause problems such as:

- crashes when rejoining a world or server
- join failures that may appear as `Invalid player data`
- errors after changing a scoreboard value

This mod is meant for players, server owners, and modpacks that want to use scoreboards to drive:

- NPC dialog availability
- NPC quest availability
- progression links with other mods or server systems

## What to expect

With this mod installed, scoreboard-based CustomNPCs conditions should be stable again on the targeted version set.

This mod does not add new gameplay or new scoreboard features. It is only a compatibility fix.

## Installation

### Dedicated server

- install CustomNPCs on the server
- install this mod on the server
- clients can still join without this compat mod

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

- client-optional on dedicated servers
- still required in singleplayer
- focused on compatibility, not feature expansion

tested with :
- Minecraft `1.21.1`
- NeoForge `21.1.230`
- `CustomNPCs-Unofficial-NeoForge-1.21.1.20251230`
