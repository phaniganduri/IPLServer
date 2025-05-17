# IPLServer
IPL TCP Server in Java — A multithreaded TCP server providing IPL teams, schedules, and player info via JSON commands.

# IPL TCP Server in Java

This project implements a multithreaded TCP server in Java that provides IPL (Indian Premier League) related data such as teams, match schedules, and player information. The server communicates using JSON-formatted commands and responses over TCP sockets.

## Features

- Multithreaded server using Java ExecutorService for efficient client handling.
- JSON-based protocol for commands and responses.
- Supports commands:
  - `getTeams` — Returns a list of IPL teams.
  - `getSchedule` — Returns upcoming match schedules.
  - `getPlayer` — Returns detailed info about a specified player.
  - `exit` — Closes the client connection.
- Simple, modular, and extensible code structure.

## Usage

1. Start the server by running `IPLServerMain.java`.
2. Connect using Telnet or any TCP client:
   ```bash
   telnet localhost 12345
3.Send JSON commands, for example:
**{"command":"getTeams"}**
4.Example Commands
Get all teams:
**{"command":"getTeams"}**
