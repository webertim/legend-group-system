# Legend Group System

Simple group system built for the application process of the Playlegend network.

## Run
This plugin can simply be tested by building the jar with:

```bash
mvn build
```

and copying it to a 1.19.2 Paper Server. In the plugin config a Postgres database connection has to be specified.

Alternatively one can simply run:

```bash
docker compose up
```

which will start a 1.19.2 Spigot Server and a Postgres Database. Afterwards the plugin can be 'installed' by executing:

```bash
mvn install
```

This will build the .jar file and copy it to the server/plugins folder. To load the plugin simply run `/reload confirm` ingame or restart the server.
