# CustomNPCs Scoreboard Compat

NeoForge 1.21.1 compatibility mod for `CustomNPCs-Unofficial-NeoForge-1.21.1.20251230`.

This mod fixes the scoreboard condition crashes observed with CustomNPCs when an NPC dialog or quest availability uses a scoreboard objective.

## Target

- Minecraft `1.21.1`
- NeoForge `21.1.230`
- CustomNPCs `CustomNPCs-Unofficial-NeoForge-1.21.1.20251230`

## Problem

On the affected CustomNPCs build, scoreboard-based availability conditions can break in three ways:

1. a duplicate objective packet is sent to the client
2. login can fail with a client disconnect that looks like `Invalid player data`
3. a scoreboard update can throw an exception even though the score value was still changed

In practice, this made scoreboard conditions unsafe for dialogs and similar logic.

## Typical Symptoms

- client crash with an error similar to:
  - `An objective with the name 'cnpc_test' already exists!`
- disconnect text or join failure reported as:
  - `Invalid player data`
- command feedback error after:
  - `/scoreboard players set @s cnpc_test 2`
- NPC availability still changing even when the command printed an error

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

## Root Cause

The issue was narrowed to three CustomNPCs code paths:

- `noppes.npcs.controllers.data.Availability.initScore`
- `noppes.npcs.ServerTickHandler.playerLogin`
- `noppes.npcs.CustomNpcs.lambda$serverstart$2`

The failures came from two concrete problems:

1. redundant `ClientboundSetObjectivePacket` sending for already-known objectives
2. `Optional.of(...)` being used with nullable values while sending scoreboard packets

## What This Compat Changes

This mod only patches those three paths.

It does the following:

- suppresses the redundant objective sync triggered from `Availability.initScore`
- suppresses redundant `ClientboundSetObjectivePacket` sends during CustomNPCs login sync
- replaces `Optional.of(...)` with `Optional.ofNullable(...)` in the two scoreboard sync paths that were crashing

## What This Compat Does Not Change

This mod does not change scoreboard semantics.

It does not:

- create missing objectives for you
- change how CustomNPCs compares scoreboard values
- alter dialogs, quests, factions, scripting, AI, rendering, GUI, or any unrelated CustomNPCs system

Scoreboard condition behavior remains:

- empty objective name: treated as no scoreboard restriction
- missing objective: condition evaluates to `false`
- existing objective without player score: condition evaluates to `false`

This mod is also not a data migration. If you configure NPC dialogs or quests with scoreboard conditions and then remove this compat mod while keeping those same CustomNPCs data and scoreboards, the original CustomNPCs crashes can come back on the affected build.

## Scope and Risk

The patch is intentionally narrow.

Only the three methods listed above are touched, and only around scoreboard packet sync and null handling. That keeps the blast radius small, but this is still a runtime patch on another mod, so the usual warning applies: if a future CustomNPCs build changes those internals, this compat may need to be updated.

## Installation

### Dedicated Server

- install this mod on the server
- install CustomNPCs on the server
- the client is allowed to join without this compat mod

This project sets `displayTest="IGNORE_SERVER_VERSION"` so the compat mod is client-optional for dedicated server joins.

### Singleplayer / LAN

- install this mod on the client instance
- install CustomNPCs on the client instance

Singleplayer still runs an integrated server, so the patch must be present in that runtime too.

## Removal Warning

If this mod is removed while your world or server still contains CustomNPCs content using scoreboard availability conditions, the original CustomNPCs behavior returns.

On the affected CustomNPCs build, that means you may reintroduce:

- duplicate objective packet crashes on join or reload
- fake `Invalid player data` disconnects caused by the broken scoreboard sync path
- scoreboard update exceptions

In short: if the compat is removed but the scoreboard-driven NPC setup remains, the crashes can come back.

## Docs

- `docs/REDISTRIBUTION_DESCRIPTION.md`
  - public-facing release description for CurseForge or similar platforms
- `docs/TESTING.md`
  - manual validation checklist for this compatibility patch
- `docs/changelogs/CHANGELOG_1.0.0.md`
  - initial release notes

## Useful Commands

- `./gradlew build`
- `./gradlew runClient`
- `./gradlew runServer`

The built jar is generated in `build/libs/`.
