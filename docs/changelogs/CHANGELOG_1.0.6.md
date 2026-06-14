# Changelog 1.0.6

## Added

- added 64 available custom mark type names, `custom_mark_1` through `custom_mark_64`
- added support for rendering those custom mark textures from `customnpcs:textures/marks/custom_mark_N.png`
- added a searchable mark type selection window in the NPC marks editor
- added support for up to 64 marks on one NPC
- added support for up to 64 dialog slots on one NPC
- added scripting API support for dialog slots above the vanilla range
- added search to `GuiStringSlotNop` selection windows, including model-style selectors such as CNPC-Gecko model and animation lists
- added a client config for showing or hiding the CustomNPCs inventory tabs for factions and quests

## Fixed

- fixed mark edits being limited by the vanilla 10-mark editor layout
- fixed expanded NPC dialog slots being inaccessible from `Advanced -> Dialogs`
- fixed CustomNPCs inventory tabs being visually misaligned on player inventory screens
- fixed the vanilla inventory tab staying visible when both faction and quest inventory tabs are disabled

## Changed

- renamed the mod from CustomNPCs Scoreboard Compat to CustomNPCs Extended Compat
- changed the mod id from `customnpcs_scoreboard_compat` to `customnpcs_extended_compat`
- changed the Java package from `fr.narrnouille.customnpcsscoreboardcompat` to `fr.narrnouille.customnpcsextendedcompat`
- changed the generated jar name to `customnpcs_extended_compat`
- changed the mixin config name to `customnpcs_extended_compat.mixins.json`
- changed the resource namespace used by this compat from `customnpcs_scoreboard_compat` to `customnpcs_extended_compat`
- updated the README and redistribution description to describe the broader compatibility and quality-of-life scope

## Compatibility

- existing scoreboard fixes from previous releases are unchanged
- existing CNPC-Gecko animation, mark rendering, and armor rendering fixes are unchanged
- clients will now create/read the client config file for the new mod id, `customnpcs_extended_compat-client.toml`
- existing custom mark texture names remain simple and predictable; players can replace `custom_mark_N.png` files with their own textures
- tested with Minecraft `1.21.1`, NeoForge `21.1.230`, CustomNPCs `CustomNPCs-Unofficial-NeoForge-1.21.1.20251230`, optional CNPC-Gecko-Addon `CNPC-Gecko-Addon-NeoForge-1.21.1-1.0.1`, and optional GeckoLib `geckolib-neoforge-1.21.1-4.8.4`

## Changement Brover

Changements ajoutes depuis le clone de reference `origin/neoforge-1-21-1`, regroupes par theme.

### Dimensions et hitbox

- conversion de la taille des NPC en float pour autoriser des valeurs plus fines
- ajustement des dimensions et hitbox des NPC vanilla et Gecko
- ajout de dimensions par defaut pour les donnees de modeles Gecko
- ajout et correction des controles d'edition de taille dans les interfaces CustomNPCs

### Nametags, marques et availability

- rendu des nametags plus coherent avec opacite reduite et gestion de profondeur
- application du fond de nametag modifie seulement lorsque le contexte Iris/Oculus le necessite
- marques orientees face au joueur et positionnees selon la presence du nom et du titre
- taille des noms, titres et marques synchronisee avec la taille du NPC
- ajout d'un offset vertical configurable pour le bloc nom/titre/marque
- ajout d'un troisieme slot d'availability scoreboard

### CNPC-Gecko-Addon

- restauration de l'overlay de texture sur les modeles Gecko
- rendu des objets tenus en version Third Person au lieu du rendu Fixed
- support plus complet de la main gauche, de la main droite et des locators d'objets
- calcul de position d'objet rendu plus permissif pour les modeles Gecko
- preview de NPC Gecko en vue 3/4 face
- correction de la rotation de tete Gecko quand le bone de tete depend d'un body ou d'une chaine de bones inclinee

### Animations, dialogues et scripts

- correction de `thenWait(int)` pour transmettre la duree d'attente
- nouveau pipeline pour les suites d'animations manuelles multi-etapes
- correction du retour a l'idle quand une sequence manuelle se termine, y compris pendant un dialogue
- ajout d'un idle force pendant les dialogues `stopAndInteract`
- focus de dialogue gere localement cote client avec reprise de routine a la fermeture du dialogue
- ajout des commandes script `setDialogLookAt(...)`, `clearDialogLookAt(...)` et `stopManualAnimation(...)`
