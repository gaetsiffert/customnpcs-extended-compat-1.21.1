# CustomNPCs Scoreboard Compat

NeoForge 1.21.1 compatibility mod for `CustomNPCs-Unofficial-NeoForge-1.21.1.20251230`.

This mod fixes the scoreboard condition crashes and client sync issues observed with CustomNPCs when NPC dialogs, dialog options, quests, or marks use scoreboard availability.

It also includes an optional compatibility patch for CNPC-Gecko-Addon animation sync packets on the same Minecraft/NeoForge target.

It also fixes two CustomNPCs scoreboard scripting API regressions on the affected build: setting scores for offline or fake scoreboard names no longer crashes, and deleting a player score no longer removes the player from their scoreboard team.

## Target

- Minecraft `1.21.1`
- NeoForge `21.1.230`
- CustomNPCs `CustomNPCs-Unofficial-NeoForge-1.21.1.20251230` (testing with another CustomNPCs version is at the user's own risk)
- optional: CNPC-Gecko-Addon `1.21.1.20251029`

## Problem

On the affected CustomNPCs build, scoreboard-based availability conditions can break in several ways:

1. a duplicate objective packet is sent to the client
2. login can fail with a client disconnect that looks like `Invalid player data`
3. a scoreboard update can throw an exception even though the score value was still changed
4. mark data can fail to resync to the client after reconnecting to a dedicated server
5. scripted scoreboard writes can crash when the target name is not an online player
6. scripted score deletion can remove the player from their team instead of deleting the score

In practice, this made scoreboard conditions unsafe for dialogs and similar logic.

## Typical Symptoms

- client crash with an error similar to:
  - `An objective with the name 'cnpc_test' already exists!`
- disconnect text or join failure reported as:
  - `Invalid player data`
- command feedback error after:
  - `/scoreboard players set @s cnpc_test 2`
- NPC availability still changing even when the command printed an error
- marks or child dialog options not reappearing on the client after the scoreboard condition becomes true
- marks disappearing from the NPC editor after reconnecting to a dedicated server
- mark scoreboard conditions only reacting again after removing and re-adding the condition
- mark and child dialog scoreboard conditions not reacting after reconnect until the condition is edited again
- NPC scripts using `world.getScoreboard().setPlayerScore(...)` crashing for offline or fake scoreboard names
- NPC scripts using `world.getScoreboard().deletePlayerScore(...)` leaving the score intact and removing the player from their team

## Reproduction

The bug can be reproduced on a clean world with CustomNPCs installed:

```mcfunction
/scoreboard objectives add cnpc_test dummy "CNPC Test"
/scoreboard players set @s cnpc_test 1
```

Then:

1. create or edit an NPC
2. add a scoreboard availability condition
3. use objective `cnpc_test`
4. compare with `=` and value `1`
5. save the NPC
6. leave and rejoin the world, or change the score again

Observed behavior before this compat:

- rejoin could crash because CustomNPCs resent an objective creation packet for an objective that the client already knew
- score updates could throw due to `Optional.of(null)` while building scoreboard sync packets
- dedicated-server clients could reopen an NPC editor with empty mark data after reconnecting

## Root Cause

The original scoreboard crash issue was narrowed to three CustomNPCs code paths:

- `noppes.npcs.controllers.data.Availability.initScore`
- `noppes.npcs.ServerTickHandler.playerLogin`
- `noppes.npcs.CustomNpcs.lambda$serverstart$2`

The failures came from two concrete problems:

1. redundant `ClientboundSetObjectivePacket` sending for already-known objectives
2. `Optional.of(...)` being used with nullable values while sending scoreboard packets

A later mark-specific issue came from the server-side mark menu path updating and broadcasting runtime mark data without saving it back to the NPC persistent data, plus missing mark data sync when a client starts seeing or editing an NPC after reconnecting.

The scripting API issue came from `ScoreboardWrapper` resolving score holders through `getPlayerByName(...)`, which returns `null` for offline players and fake scoreboard names. Its score deletion path also called the team removal API instead of the score reset API.

## What This Compat Changes

This mod patches the affected scoreboard sync paths and the affected CustomNPCs mark data paths.

It does the following:

- starts scoreboard objective tracking from `Availability.initScore` only when the objective is not already tracked
- turns CustomNPCs manual score update objective sync into idempotent server tracking
- replaces `Optional.of(...)` with `Optional.ofNullable(...)` in the two scoreboard sync paths that were crashing
- refreshes login objective sync per-player so rejoining clients receive scoreboard state for availability checks without duplicate objective crashes
- saves mark menu changes back to NPC persistent data
- resends existing mark data when an NPC starts being seen by a player
- resends mark data when a player opens the NPC editor
- uses scoreboard name holders for CustomNPCs scoreboard scripting API score access
- makes CustomNPCs scoreboard scripting API score deletion reset the requested score instead of changing team membership
- registers CNPC-Gecko-Addon animation sync payloads when that addon is installed
- corrects the CNPC-Gecko-Addon animation sync payload IDs returned at runtime
- restores CustomNPCs mark rendering above NPCs that use a Gecko model

## What This Compat Does Not Change

This mod does not change scoreboard semantics.

It does not:

- create missing objectives for you
- change how CustomNPCs compares scoreboard values
- validate Gecko animation names or model animation data
- alter dialog, quest, faction, scripting, AI, rendering, GUI, or any unrelated CustomNPCs logic
- change which scripts are allowed to edit scoreboards

Scoreboard condition behavior remains:

- empty objective name: treated as no scoreboard restriction
- missing objective: condition evaluates to `false`
- existing objective without player score: condition evaluates to `false`

This mod is also not a data migration. If you configure NPC dialogs or quests with scoreboard conditions and then remove this compat mod while keeping those same CustomNPCs data and scoreboards, the original CustomNPCs crashes can come back on the affected build.

## Gecko Addon Compatibility

When CNPC-Gecko-Addon is installed, Gecko NPC scripts can trigger animation sync with:

```js
function interact(event) {
    var builder = event.API.createAnimBuilder()
    builder.thenPlay("wave")
    event.npc.syncAnimationsFor(event.player, builder)
}
```

On the affected addon build, the packet used by `syncAnimationsFor(...)` is missing NeoForge play-to-client payload registration and returns a payload type ID that does not match the channel it is meant to use. That can disconnect the client with errors such as:

- `Payload cnpcgeckoaddon:packetsyncanimation may not be sent to the client`
- `Failed to encode packet 'clientbound/minecraft:custom_payload'`

This compat registers the two Gecko animation sync payloads at runtime and corrects their returned payload IDs. The patch is inactive when CNPC-Gecko-Addon is not installed.

It also restores CustomNPCs mark rendering for NPCs whose normal renderer is replaced by CNPC-Gecko-Addon. Marks still use CustomNPCs' own mark data and availability checks.

## Scope and Risk

The patch is intentionally narrow.

Only the paths listed above are touched, and only around scoreboard objective tracking, login scoreboard refresh, scoreboard packet sync, null handling, mark persistence, mark packet resync, CustomNPCs scoreboard scripting API score access, and the optional CNPC-Gecko-Addon animation sync and mark rendering compatibility paths. That keeps the blast radius small, but this is still a runtime patch on other mods, so the usual warning applies: if a future CustomNPCs or CNPC-Gecko-Addon build changes those internals, this compat may need to be updated.

## Installation

This mod is not strictly server-side only.

On dedicated servers, it is server-required and client-optional for scoreboard-only use. Clients can join without this jar because `displayTest="IGNORE_SERVER_VERSION"` is set, but the jar is still built for both sides and includes optional client compatibility code.

### Dedicated Server

- install this mod on the server
- install CustomNPCs on the server
- the client is allowed to join without this compat mod for scoreboard-only use

If you use CNPC-Gecko-Addon animation sync scripts, install this compat mod in the same runtime as CNPC-Gecko-Addon. In practice, Gecko model rendering usually means both the server and the client have CNPC-Gecko-Addon, so both sides should also have this compat mod for that specific fix.

### Singleplayer / LAN

- install this mod on the client instance
- install CustomNPCs on the client instance
- install CNPC-Gecko-Addon in the same instance only if you need Gecko model animation sync

Singleplayer still runs an integrated server, so the patch must be present in that runtime too.

## Removal Warning

If this mod is removed while your world or server still contains CustomNPCs content using scoreboard availability conditions, the original CustomNPCs behavior returns.

On the affected CustomNPCs build, that means you may reintroduce:

- duplicate objective packet crashes on join or reload
- fake `Invalid player data` disconnects caused by the broken scoreboard sync path
- scoreboard update exceptions
- missing mark data after reconnecting to a dedicated server

In short: if the compat is removed but the scoreboard-driven NPC setup remains, the crashes can come back.

## Docs

- `docs/REDISTRIBUTION_DESCRIPTION.md`
  - public-facing release description for CurseForge or similar platforms
- `docs/TESTING.md`
  - manual validation checklist for this compatibility patch
- `docs/changelogs/CHANGELOG_1.0.0.md`
  - initial release notes
- `docs/changelogs/CHANGELOG_1.0.1.md`
  - previous release notes
- `docs/changelogs/CHANGELOG_1.0.2.md`
  - previous release notes
- `docs/changelogs/CHANGELOG_1.0.3.md`
  - previous release notes
- `docs/changelogs/CHANGELOG_1.0.4.md`
  - current release notes

## Useful Commands

- `./gradlew build`
- `./gradlew runClient`
- `./gradlew runServer`

The built jar is generated in `build/libs/`.
