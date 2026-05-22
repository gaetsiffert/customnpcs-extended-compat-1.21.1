# Changelog 1.0.2

## Fixed

- fixed NPC marks disappearing from clients after reconnecting to a dedicated server
- fixed mark menu saves not being persisted back to the NPC persistent data
- fixed mark scoreboard conditions acting missing after reconnect until the condition was removed and re-added
- fixed mark and child dialog scoreboard conditions not receiving login scoreboard state after reconnect
- fixed duplicate objective crashes when the login scoreboard state was already present on the client

## Changed

- mark data is resent when an NPC starts being seen by a player
- mark data is resent when a player opens the NPC editor
- login scoreboard objective sync is refreshed per-player while score updates remain idempotent

## Compatibility

- the compat mod remains client-optional on dedicated servers
- mark resync uses CustomNPCs' own mark data packet
- scoreboard resync uses vanilla scoreboard packets
- no compat-specific client packet is required
