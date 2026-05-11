# AstryxFX

A command-line applicaton built to manage API data requests from Faceit using JavaFX and Maven.

## Overview

Astryx is a Discord bot combined with an API and SQL manager, command line input, and statistics creator.
The application automatically updates a master database of matches, maps, vetoes, and statistics every 15 minutes, or on-demand using the inline commands.

The Discord bot allows teams to retreive statistics immediately.
For instance, a team may want statistics on an opponent for various strategic decisions.
Since this process is fully automated, there is no middleman and each team gets the up-to-date statistics immediately.

## Components
**Desktop GUI**
A JavaFX interface built with FXML and CSS that lets users manage bot behaviour without writing or restarting code.
Has numerous features, such as autocomplete.

**Discord Bot Control**
Connects to a Discord bot using Discord4J's reactive API, handling events and interactions.

**Annotations and Reflections**
Easily usable annotations makes it easy to define bot commands and in-line commands.

**SQL Manager**
Stores settings and records in a MySQL database via the official MySQL Connector/J.

**External API Integration**
Makes outbound HTTP requests using OkHttp, with JSON responses handled through Jackson and org.json

**Configuration**
Bot and application settings are managed through readable TOML config files

**Logging**
Uses Log4j2 with SLF4J bridging to produce structured logs, stored in a dedicated logs directory

**Python**
We create the charts sent using the Discord commands using matplotlib.

## Tech Stack

### Java 22
- Maven (build system)
- JavaFX 22 (UI framework)
- Discord4J (Discord integration)
- Toml4J (configuration parsing)
- MySQL Connector/J (database connectivity)
- JSON processing (data serialization)
- Log4j (logging framework)
- OkHttp3 (HTTP client)
- Reflections (runtime classpath scanning)
- Jackson (advanced JSON processing)

### Python 3.10
- pandas (data analysis)
- numpy (numerical computing)
- matplotlib (data visualization)
- PyMySQL (MySQL database access)
- JSON (data handling)
- sys (system-level operations)
- traceback (error handling and debugging)

## Author
Kierstyn "Kjersti" McMillin
