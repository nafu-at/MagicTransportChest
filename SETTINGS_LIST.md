# Magic Transport Chest (MTC) Settings List

These settings are stored in a database and shared among all connected servers.

## 1. Storage Settings

### `mtc.storage.size`

- **Description**: The size of the Magic Transport Chest.
- **Type**: Integer
- **Default**: `27` (9x3)

### `mtc.storage.max_number_per_player`

- **Description**: The maximum number of Magic Transport Chests that can be assigned to a player.
- **Type**: Integer
- **Default**: `1`

### `mtc.storage.use_storage_permission_node`

- **Description**: permission node name to use the storage.
- **Type**: String
- **Default**: `mtc.storage.use`

### `mtc.storage.enable_chest_block_assign`

- **Description**: Enable or disable the assign Magic Transport Chest to pyhsical chest.
- **Type**: Boolean
- **Default**: `true`

### `mtc.storage.enable_item_filter

- **Description**: Enable or disable the item filter.
- **Type**: Boolean
- **Default**: `false`
- **Note**: If you enable this setting, you need to set the item filter.

### `mtc.storage.filter_type`

- **Description**: The type of item filter.
- **Type**: String
- **Default**: `WHITELIST`
- **Values**: `WHITELIST`, `BLACKLIST`

### `mtc.storage.item_filter`

- **Description**: The item filter.
- **Type**: Yaml List
- **Default**: `[]`
- **Notice**: This setting is saved in a special format, so a dedicated configuration UI is provided. Please do not
  manually change this setting as it may corrupt the data. 
