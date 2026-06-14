# CustomNPCs Extended Compat

CustomNPCs Extended Compat is a NeoForge 1.21.1 compatibility and quality-of-life extension mod for `CustomNPCs-Unofficial-NeoForge-1.21.1.20251230`.

It is built as a practical patch mod for a larger server setup. Additions are made when a CustomNPCs issue, hard-coded limit, confusing workflow, or small bug gets in the way of real use and needs to be fixed or made easier to work with.

Its goal is to stabilize the targeted CustomNPCs build and remove a few hard-coded editor/UI limits that get in the way of larger NPC setups, without turning this into a full CustomNPCs rewrite.

Before version 1.0.6, this mod was named CustomNPCs Scoreboard Compat and used the mod id `customnpcs_scoreboard_compat`.

## What it changes

Without this mod, the targeted CustomNPCs build can have problems such as:

- crashes when rejoining a world or server
- join failures that may appear as `Invalid player data`
- errors after changing a scoreboard value
- NPC marks disappearing after reconnecting to a dedicated server
- mark or child dialog scoreboard conditions not reacting after reconnect until edited again
- only a small hard-coded mark type list
- a 10 marks per NPC editor limit
- a 12 dialog slots per NPC editor/API limit
- large model or animation selectors with no search
- inventory tabs that cannot be configured and may be visually misaligned

This mod is meant for players, server owners, and modpacks that want more stable and more flexible CustomNPCs setups, including:

- NPC dialog availability
- NPC dialog option availability
- NPC quest availability
- NPC mark availability
- larger mark setups with custom `custom_mark_N` textures
- more NPC dialog slots
- CNPC-Gecko model and animation selection
- progression links with other mods or server systems

## What to expect

With this mod installed, scoreboard-based CustomNPCs conditions should be stable again on the targeted version set. This includes conditions that are evaluated client-side, such as marks above NPCs and child dialog options. NPC marks are also saved and resynced correctly after reconnecting to a dedicated server.

It also expands the mark editor to 64 marks per NPC, adds `custom_mark_1` through `custom_mark_64`, expands NPC dialogue slots to 64, adds search to shared string selection lists, and adds a client config for showing or hiding the CustomNPCs inventory factions/quests tabs.

It also includes optional CNPC-Gecko-Addon compatibility fixes. When CNPC-Gecko-Addon is installed, the compat registers the addon's animation sync payloads correctly and fixes the returned payload IDs so `syncAnimationsFor(...)` scripts no longer disconnect the client on the targeted NeoForge version. It also restores CustomNPCs marks and vanilla armor rendering on humanoid Gecko NPC models.

It also fixes two CustomNPCs scoreboard scripting API issues on the affected build: scripts can safely set scores for offline or fake scoreboard names, and `deletePlayerScore(...)` now deletes the score instead of removing the player from their scoreboard team.

## Changement Brover

Depuis le clone de reference `origin/neoforge-1-21-1`, les changements Brover etendent surtout les gros setups CustomNPCs utilisant des tailles personnalisees, des nametags complexes, des modeles Gecko et des dialogues scripts.

Par theme :

- dimensions et hitbox : taille de NPC plus precise, dimensions vanilla/Gecko mieux synchronisees, hitbox ajustees et options d'edition plus coherentes
- nametags et marques : rendu plus lisible, gestion de profondeur, compatibilite Iris/Oculus, marques qui font face au joueur, position adaptee au nom/titre, offset vertical configurable et troisieme slot d'availability scoreboard
- CNPC-Gecko-Addon : overlay de texture restaure, objets tenus rendus comme en Third Person, support main gauche/main droite, positionnement d'objets plus permissif, preview 3/4 face et correction du regard sur les modeles dont la tete depend d'une chaine de bones inclinee
- animations et dialogues : `thenWait(int)` corrige, sequences d'animations manuelles multi-etapes fiabilisees, retour a l'idle apres sequence manuelle meme pendant un dialogue, idle de dialogue force pendant `stopAndInteract`, et nouvelles commandes script de focus ou d'arret d'animation

## Installation

### Dedicated server

- install CustomNPCs on the server
- install this mod on the server
- clients can still join without this compat mod for scoreboard-only use
- install this mod on clients that need the CustomNPCs inventory tab config, searchable selectors, custom mark selector, or Gecko rendering fixes
- for CNPC-Gecko-Addon animation sync, install this compat mod wherever CNPC-Gecko-Addon is installed
- for CNPC-Gecko-Addon mark or armor rendering fixes, install this compat mod on the client
- scoreboard scripting API fixes run on the server side

This means the mod is server-required and client-optional on dedicated servers for scoreboard-only use. It is not strictly server-side only: the jar is built for both sides and includes optional client compatibility code for CNPC-Gecko-Addon mark and armor rendering.

Side requirements by feature:

- scoreboard condition, mark persistence, and scoreboard scripting fixes: server required
- scoreboard-only clients may join without this compat mod
- mark/dialog editor extensions and inventory tab UI fixes: client required for editing/UI use
- CNPC-Gecko-Addon animation sync fixes: install wherever CNPC-Gecko-Addon runs, usually both server and client
- CNPC-Gecko-Addon mark and armor rendering fixes: client required
- GeckoLib is not required for scoreboard-only use

### Singleplayer / LAN

- install CustomNPCs
- install this mod in the same game instance
- install CNPC-Gecko-Addon and GeckoLib only if you need Gecko model animation sync or Gecko model rendering fixes

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
- focused on compatibility fixes and editor/UI quality-of-life extensions
- uses vanilla scoreboard packets and CustomNPCs packets only; no compat-specific client packet is required
- CNPC-Gecko-Addon support is optional and inactive when that addon is absent

tested with :
- Minecraft `1.21.1`
- NeoForge `21.1.230`
- `CustomNPCs-Unofficial-NeoForge-1.21.1.20251230` (testing with another CustomNPCs version is at the user's own risk)
- optional CNPC-Gecko-Addon `CNPC-Gecko-Addon-NeoForge-1.21.1-1.0.1`
- optional GeckoLib `geckolib-neoforge-1.21.1-4.8.4`

Other CustomNPCs, CNPC-Gecko-Addon, GeckoLib, Minecraft, or NeoForge builds are not implied to be supported by this release.
