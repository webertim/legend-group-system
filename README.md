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
