# Changelog 1.0.3

## Fixed

- fixed CNPC-Gecko-Addon's missing NeoForge payload registration for Gecko animation sync packets
- fixed incorrect payload type IDs returned by CNPC-Gecko-Addon's animation sync packets
- fixed Gecko NPC scripts disconnecting when calling `syncAnimationsFor(...)`
- fixed CustomNPCs marks not rendering above NPCs using a Gecko model

## Changed

- Gecko animation sync payloads are registered at runtime when CNPC-Gecko-Addon is present
- Gecko animation sync payload type IDs are corrected through targeted packet mixins
- CustomNPCs mark rendering is restored for NPC render passes replaced by CNPC-Gecko-Addon

## Compatibility

- CNPC-Gecko-Addon remains optional
- Gecko animation sync payloads are registered as optional play-to-client payloads
- the Gecko payload registration uses reflection so this compat mod does not require CNPC-Gecko-Addon at compile time
- scoreboard behavior is unchanged
