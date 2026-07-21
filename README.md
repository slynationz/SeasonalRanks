# SeasonalRanks

A lightweight, robust Minecraft plugin for **Paper** and **Purpur** (1.21+) that hooks into **LuckPerms** to assign, track, and cleanly reset seasonal ranks and permissions.

---

## 🏷️ Tags & Topics
`minecraft-plugin` `paper-plugin` `purpur-plugin` `luckperms-integration` `seasonal-reset` `minecraft-ranks` `minecraft-1-21`

---

## ✨ Features
* **LuckPerms Command Bridging**: Seamlessly maps player ranks and permissions directly into LuckPerms.
* **Smart Isolation**: Only tracks and removes ranks/permissions added through the `/season` commands—permanent player configurations are left completely untouched.
* **Offline Player Login Queue**: If a player is offline and has never joined the server, commands are queued and automatically applied when they connect using their correct UUID, avoiding offline UUID discrepancies.
* **Robust Offline UUID Handling**: Tracks players by UUID internally, preventing profile issues due to username changes.
* **Local YAML Database**: Optimized storage inside `data.yml` to keep track of all seasonal allocations.

---

## 🎮 Commands & Permissions

All commands require the `seasonalranks.admin` permission (defaults to OP).

| Command | Action | Description |
| :--- | :--- | :--- |
| `/season rank add {player} {rank}` | `lp user {uuid} parent add {rank}` | Adds a seasonal rank to a player and tracks it. |
| `/season perm add {player} {permission}` | `lp user {uuid} permission set {permission} true` | Adds a seasonal permission to a player and tracks it. |
| `/newseason` | *Warning Prompt* | Prompts the administrator to confirm the season reset. |
| `/newseason confirm` | `lp user {uuid} parent remove {rank}` <br> `lp user {uuid} permission unset {permission}` | Resets the season, removing all seasonal ranks and permissions from all tracked players, and wipes the database. |

---

## ⚙️ Installation & Requirements
1. Ensure your server is running **Paper** or **Purpur** (1.21+).
2. Install **LuckPerms** (5.0+) in your server's `plugins/` directory.
3. Drop the `SeasonalRanks.jar` file into the `plugins/` directory.
4. Start/Restart the server.
