# Changelog 1.0.5

## Fixed

- restored vanilla armor rendering for humanoid CNPC-Gecko NPC models
- attached rendered armor parts to the matching Gecko bones so armor follows model animations
- synced CustomNPCs armor slots onto the CNPC-Gecko rendered entity before the Gecko renderer runs

## Changed

- adds a Gecko armor render layer to CNPC-Gecko's `RenderCustomModel`
- renders fixed-size vanilla armor parts on matching Gecko bones instead of scaling them to Gecko cube dimensions
- guards CNPC-Gecko-specific mixins behind runtime class checks

## Compatibility

- keeps CNPC-Gecko support optional and inactive when CNPC-Gecko-Addon is absent
- CNPC-Gecko-Addon armor support depends on GeckoLib being present in the same runtime
- GeckoLib is compile-only for this compat mod and is not bundled or required for scoreboard-only CustomNPCs use
- scoreboard-only use remains server-required and client-optional on dedicated servers
- CNPC-Gecko-Addon rendering fixes require this compat mod on the client
- tested with Minecraft `1.21.1`, NeoForge `21.1.230`, CustomNPCs `CustomNPCs-Unofficial-NeoForge-1.21.1.20251230`, optional CNPC-Gecko-Addon `CNPC-Gecko-Addon-NeoForge-1.21.1-1.0.1`, and optional GeckoLib `geckolib-neoforge-1.21.1-4.8.4`
- scoreboard behavior is unchanged
