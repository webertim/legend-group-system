# Legend Group System

Simple group system built for the application process of the Playlegend network.

## Requirements
This plugin depends on ProtocolLib. If you are using docker compose to run this project, ProtocolLib will already be available.

## Run

### Manual
This plugin can simply be tested by building the jar with:

```bash
mvn build
```

and copying it to a 1.19.2 Paper Server. In the plugin config a MySQL database connection has to be specified.

### Docker Compose
Alternatively one can simply run:

```bash
docker compose up
```

which will start a 1.19.2 Spigot Server and a MySQL Database. The server will include an accepted eula.txt and the most recent ProtocolLib.jar.
The MySQL Database will be setup so that the default config of the plugin works.

After the docker compose startup the plugin can be 'installed' by executing:

```bash
mvn install
```

This will build the .jar file and copy it to the plg folder which is mounted into the server container. To load the plugin simply run `/reload confirm` ingame or restart the server.

## Features

This plugin fulfills all minimal requirements aswell as most of the bonus requirements.

### Missing:
- The tablist is not sorted by group
- Multiple languages are supported but only global. Player level languages could be easily implemented since a method which takes a language key as well as a message key already exists.
  1. Add a language attribute to the PlayerInfo class
  2. Change all calls of getMessage(String messageKey) to getMessage(String languageKey, String messageKey) and pass the value of language from the calling players PlayerInfo

## Commands

- /group: Manage groups
  - create <groupId> <prefix> <groupName>: Create a group
  - update <prefix|name> <groupId> <newValue>: Update a group name or prefix
  - delete <groupId>: Delete a group
  - default <groupId?>: Set a new default group or reset the default group if no groupId is provided
  - permission <add|remove> <permission>: Add/remove a permission to/from the group
- /player: Manage players
  - add <playerName> <groupId> <duration?>: Add a player to the group with the provided group id. If a duration is added, the player well be removed from the group after the provided time
  - remove <playerName>: Remove the group of a player
- /groupinfo: Get group info
- /groupsign: Manage special group signs
  - add: Mark your current position as a group sign position. If a sign is places here, or if a new sign is placed here in the future, it will display play specific group information
  - remove: Remove the mark of a group sign at your current positon. 
