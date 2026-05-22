# Changelog 1.0.1

## Fixed

- restored client-side scoreboard availability refresh for marks
- restored client-side scoreboard availability refresh for child dialog options
- changed CustomNPCs objective sync handling from broad packet suppression to idempotent server objective tracking
- kept duplicate objective creation packets suppressed once Minecraft is already tracking the objective

## Documentation

- documented mark and child dialog option scoreboard regression checks
- clarified that the compat remains client-optional on dedicated servers
