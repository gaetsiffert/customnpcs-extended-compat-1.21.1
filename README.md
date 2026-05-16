# CustomNPCs Scoreboard Compat

NeoForge 1.21.1 compatibility mod for CustomNPCs scoreboard conditions.

This project exists to patch the scoreboard sync issues observed with `CustomNPCs-Unofficial-NeoForge-1.21.1.20251230`, especially duplicate objective packets and null `Optional` handling during login and score updates.

## Requirements

- JDK 21

## Useful commands

- `./gradlew build`
- `./gradlew runClient`
- `./gradlew runServer`

The built jar is generated in `build/libs/`.
