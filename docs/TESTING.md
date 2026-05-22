# Testing Notes

This project is a compatibility patch, so the important checks are behavioral rather than feature breadth.

## Main scenarios

### 1. Singleplayer scoreboard condition

1. start a clean singleplayer world with CustomNPCs and this mod installed
2. run:

```mcfunction
/scoreboard objectives add cnpc_test dummy "CNPC Test"
/scoreboard players set @s cnpc_test 1
```

3. create an NPC scoreboard availability condition using:
   - objective: `cnpc_test`
   - operator: `=`
   - value: `1`
4. save the NPC
5. confirm the dialog is available
6. change the score:

```mcfunction
/scoreboard players set @s cnpc_test 2
```

7. confirm the dialog is no longer available
8. leave and rejoin the world
9. confirm there is no duplicate objective crash and the world still loads

### 2. Client-side scoreboard refresh

Use an existing `cnpc_test` objective or create it:

```mcfunction
/scoreboard objectives add cnpc_test dummy "CNPC Test"
/scoreboard players set @s cnpc_test 0
```

#### Mark availability

1. create or edit an NPC
2. add a visible mark in `Advanced -> Marks`
3. set the mark availability scoreboard condition to:
   - objective: `cnpc_test`
   - operator: `=`
   - value: `1`
4. save the NPC and confirm the mark is hidden while the score is `0`
5. run:

```mcfunction
/scoreboard players set @s cnpc_test 1
```

6. confirm the mark reappears above the NPC

#### Child dialog availability

1. create a source dialog that opens from the NPC
2. add a child dialog option from that source dialog
3. set the child dialog availability scoreboard condition to:
   - objective: `cnpc_test`
   - operator: `=`
   - value: `1`
4. set the score to `0` and confirm the child option is hidden
5. set the score to `1` and reopen the source dialog
6. confirm the child option is visible again
7. disconnect and reconnect
8. change the score between `0` and `1`
9. confirm the child option updates without editing the condition again

Expected result:

- client-side mark availability updates after score changes
- client-side child dialog options update after score changes
- mark and child dialog availability still update after reconnecting
- no duplicate objective crash

### 3. Dedicated server without the client jar

1. install CustomNPCs and this compat mod on the dedicated server
2. do not install this compat mod on the client
3. join the server
4. repeat the scoreboard condition scenario
5. create or edit an NPC
6. add a visible mark in `Advanced -> Marks`
7. save the NPC, disconnect, and reconnect
8. confirm the mark is still visible above the NPC
9. reopen the NPC editor and confirm the mark is still listed
10. set the mark availability scoreboard condition to:
    - objective: `cnpc_test`
    - operator: `=`
    - value: `1`
11. save, disconnect, and reconnect again
12. change the score between `0` and `1`
13. confirm the mark updates without removing and re-adding the condition

Expected result:

- join succeeds
- scoreboard condition still works
- no duplicate objective crash
- mark data is preserved and resynced after reconnecting

### 4. Missing objective behavior

Create a scoreboard condition pointing to an objective that does not exist.

Expected result:

- no crash
- condition evaluates to `false`
- NPC content guarded by that condition remains unavailable

### 5. Removal regression check

This is only relevant if you want to verify the documented limitation.

1. keep a world or server where CustomNPCs scoreboard availability is configured
2. remove this compat mod
3. keep CustomNPCs and the same scoreboard/NPC data
4. start the game or rejoin the world/server

Expected result on the affected CustomNPCs build:

- the original crashes or join failures may return

This mod fixes runtime behavior while installed. It does not rewrite CustomNPCs data to make the upstream bug disappear permanently.

### 6. Optional CNPC-Gecko-Addon animation sync

This is only relevant when CNPC-Gecko-Addon is installed.

1. create or open an NPC using a Gecko model with an animation named `wave`
2. add this CustomNPCs script to the NPC:

```js
function interact(event) {
    var builder = event.API.createAnimBuilder()
    builder.thenPlay("wave")
    event.npc.syncAnimationsFor(event.player, builder)
}
```

3. start the game with this compat mod and CNPC-Gecko-Addon installed
4. interact with the NPC

Expected result:

- the player is not disconnected
- the script does not throw `Payload cnpcgeckoaddon:packetsyncanimation may not be sent to the client`
- the script does not throw `Failed to encode packet 'clientbound/minecraft:custom_payload'`
- visible marks still render above the Gecko model NPC

## Regression boundaries

This compat mod should only affect the following CustomNPCs scoreboard sync paths:

- `Availability.initScore`
- `ServerTickHandler.playerLogin`
- `CustomNpcs.lambda$serverstart$2`
- mark save handling in `SPacketMenuSave.handle`
- mark data sync when an NPC starts being seen by a player or is opened in the editor
- optional CNPC-Gecko-Addon animation sync payload registration and payload type correction
- optional CNPC-Gecko-Addon NPC mark render restoration

Anything outside those paths is out of scope and should behave exactly as before.

## Build

```powershell
./gradlew.bat build
```
