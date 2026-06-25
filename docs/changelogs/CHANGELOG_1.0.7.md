# Changelog 1.0.7

Changes added since the commit that introduced `CHANGELOG_1.0.6.md` (`38c89d2`).

## Added

- added a CustomNPCs custom GUI default background toggle
- exposed NPC display visibility availability through a compat access bridge
- added runtime development dependencies for optional CNPC-Gecko-Addon testing
- added float-based NPC size scaling so NPC display size can use finer values than the old integer-only scale
- added persistent custom size storage while keeping the legacy `Size` value synchronized for compatibility
- added display GUI controls for float NPC size and vertical label offset editing
- added CustomNPCs and CNPC-Gecko dimension bridges so vanilla NPCs and Gecko NPCs use coherent render dimensions and hitboxes
- added default CNPC-Gecko model dimensions when model data does not provide them
- added scaling support for CNPC-Gecko custom models
- added a custom nametag, title, and mark render path with smoother label positioning
- added depth-aware nametag rendering and shader shadow-pass state tracking
- added player-facing custom mark rendering with positioning based on the NPC name and title state
- added a third scoreboard availability slot to CustomNPCs availability controls
- added GUI support for editing the third scoreboard availability slot
- added CNPC-Gecko overlay texture restoration for Gecko NPC models
- added CNPC-Gecko held-item rendering through a dedicated held-item layer
- added support for left-hand and right-hand Gecko item locators, including locator offsets and rotations
- added CNPC-Gecko model preview adjustments so dialog previews render in a clearer three-quarter view
- added a Gecko animation codec for multi-stage manual animation sequences
- added manual animation sequence control with waits, transitions, and clean idle restoration
- added client/server payloads for dialog look override and targeted manual animation stop handling
- added local client dialog focus tracking so dialog look behavior can run during dialog interactions and clear when the dialog ends
- added script API methods on NPC wrappers:
  - `setDialogLookAt(IPlayer, double, double, double)`
  - `setDialogLookAt(IPlayer, double, double, double, float)`
  - `setDialogLookAt(IPlayer, double, double, double, double)`
  - `clearDialogLookAt(IPlayer)`
  - `stopManualAnimation()`
  - `stopManualAnimation(IPlayer)`
- added Gecko dialog preview client helpers
- added mixin plugin guards for optional CNPC-Gecko and GeckoLib classes
- added documentation and manual test coverage for Brover compatibility changes

## Fixed

- fixed custom GUI wrappers always using the default CustomNPCs background behavior
- fixed visibility availability data not being reachable from the compat layer
- fixed NPC render size, hitbox size, and GUI size controls drifting apart
- fixed Gecko NPC render dimensions not following CustomNPCs display size consistently
- fixed Gecko custom model scale not matching the NPC size scale
- fixed nametag, title, and mark scaling so labels follow NPC size more consistently
- fixed mark orientation so marks face the player more reliably
- fixed mark vertical placement when NPCs have a name, a title, both, or neither
- fixed nametag opacity and depth behavior for clearer labels
- fixed the modified nametag background behavior being applied outside the intended Iris/Oculus shadow-pass context
- fixed nametag rendering order with a dedicated depth renderer hotfix
- fixed name/title background depth rendering during entity shadow passes
- fixed CustomNPCs availability controls being limited to the previous scoreboard slot count
- fixed CNPC-Gecko overlay textures not rendering on Gecko NPCs
- fixed Gecko held items using an unsuitable fixed transform instead of a third-person-style transform
- fixed Gecko held-item placement for models using left/right hand locators
- fixed Gecko equipment rendering hooks so Gecko armor and equipment compatibility can run together
- fixed Gecko head rotation when the head bone is parented under a body or an inclined bone chain
- fixed `thenWait(int)` not preserving the requested wait duration in Gecko animation sequences
- fixed multi-step manual Gecko animations losing later stages
- fixed manual animations failing to return cleanly to idle after a sequence ends
- fixed manual animation idle restoration while an NPC is in a `stopAndInteract` dialog
- fixed dialog focus getting stuck after a dialog closes
- fixed child dialog and quest completion checks so manual animation and dialog focus state are cleaned up at the right time

## Changed

- changed NPC size handling from the old integer-only model size path to a clamped float scale bridge
- changed CustomNPCs display serialization to store the compat float size and mirror it back to the legacy integer size
- changed NPC display GUI layout to include float size and vertical label offset controls
- changed name/title/mark rendering to use the compat renderer instead of relying only on the original CustomNPCs render path
- changed CNPC-Gecko held items to render closer to vanilla third-person item presentation
- changed Gecko dialog previews to use a front-biased preview angle instead of the previous default view
- changed manual Gecko animation sync to preserve ordered stage data and additional wait ticks
- changed dialog look behavior to be handled client-side during active dialogs, with normal behavior restored after the interaction
- changed README, redistribution notes, and testing notes to describe the Brover compatibility scope

## Compatibility

- scoreboard fixes and 1.0.6 expanded marks/dialog/search features remain unchanged
- optional CNPC-Gecko compatibility remains guarded so the compat does not require CNPC-Gecko-Addon for scoreboard-only use
- GeckoLib and CNPC-Gecko-Addon are available as runtime development dependencies for local validation
- CNPC-Gecko rendering changes affect clients that have the compat installed
- dialog look and manual animation stop payloads are only used by the compat paths that need them
- tested target remains Minecraft `1.21.1`, NeoForge `21.1.230`, CustomNPCs `CustomNPCs-Unofficial-NeoForge-1.21.1.20251230`, optional CNPC-Gecko-Addon `CNPC-Gecko-Addon-NeoForge-1.21.1-1.0.1`, and optional GeckoLib `geckolib-neoforge-1.21.1-4.8.4`
