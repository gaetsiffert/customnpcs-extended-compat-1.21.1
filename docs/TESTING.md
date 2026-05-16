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
9. confirm there is no crash and the world still loads

### 2. Dedicated server without the client jar

1. install CustomNPCs and this compat mod on the dedicated server
2. do not install this compat mod on the client
3. join the server
4. repeat the scoreboard condition scenario

Expected result:

- join succeeds
- scoreboard condition still works
- no duplicate objective crash

### 3. Missing objective behavior

Create a scoreboard condition pointing to an objective that does not exist.

Expected result:

- no crash
- condition evaluates to `false`
- NPC content guarded by that condition remains unavailable

### 4. Removal regression check

This is only relevant if you want to verify the documented limitation.

1. keep a world or server where CustomNPCs scoreboard availability is configured
2. remove this compat mod
3. keep CustomNPCs and the same scoreboard/NPC data
4. start the game or rejoin the world/server

Expected result on the affected CustomNPCs build:

- the original crashes or join failures may return

This mod fixes runtime behavior while installed. It does not rewrite CustomNPCs data to make the upstream bug disappear permanently.

## Regression boundaries

This compat mod should only affect the following CustomNPCs scoreboard sync paths:

- `Availability.initScore`
- `ServerTickHandler.playerLogin`
- `CustomNpcs.lambda$serverstart$2`

Anything outside those paths is out of scope and should behave exactly as before.

## Build

```powershell
./gradlew.bat build
```
