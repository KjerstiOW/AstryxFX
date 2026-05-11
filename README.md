# AstryxFX

A desktop application for automated esports statistics, built with JavaFX, Discord4J, and Python.

## Overview

Astryx is a Discord bot combined with an API and SQL manager, GUI input, and statistics creator.
The application automatically updates a master database of matches, maps, vetoes, and statistics every 15 minutes, or on-demand using the program commands.

The Discord bot allows teams to retrieve statistics.
For instance, a team may want statistics on an opponent for various strategic decisions.
Since this process is fully automated, there is no middleman and each team gets the up-to-date statistics immediately.

### Program
<img width="783" height="590" alt="image" src="https://github.com/user-attachments/assets/7ed4822b-ecc7-4ed5-8b36-a2406f902223" />

### Example output
<img width="998" height="589" alt="image" src="https://github.com/user-attachments/assets/0afa6a25-0964-41d1-8d85-8067fe694f8e" />

## Components
### Desktop GUI
A JavaFX interface built with FXML and CSS that lets users manage bot behaviour without writing or restarting code.
Has numerous features, such as autocomplete.

### Discord Bot Control
Connects to a Discord bot using Discord4J's reactive API, handling events and interactions.

### Annotations and Reflections
Easily usable annotations makes it easy to define bot commands and program commands.

### SQL Manager
Stores settings and records in a MySQL database via the official MySQL Connector/J.

### External API Integration
Makes outbound HTTP requests using OkHttp, with JSON responses handled through Jackson and org.json

### Configuration
Bot and application settings are managed through readable TOML config files

### Logging
Uses Log4j2 with SLF4J bridging to produce structured logs, stored in a dedicated logs directory

### Python
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
Kierstyn "Kjersti" McMillin, for usage at Maryville University and Fluffy Aimers.
