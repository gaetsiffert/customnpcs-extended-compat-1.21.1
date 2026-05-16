# Changelog 1.0.0

Initial public release.

## Added

- compatibility patch for CustomNPCs scoreboard availability on NeoForge 1.21.1
- suppression of redundant scoreboard objective sync packets in the affected CustomNPCs paths
- null-safe `Optional.ofNullable(...)` handling for the affected scoreboard sync packet builders
- dedicated documentation for installation, reproduction, and testing

## Deployment notes

- works in singleplayer, LAN, and dedicated server setups
- client-optional for dedicated server joins
- still required in the local instance for singleplayer because the integrated server runs there
