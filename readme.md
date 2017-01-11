I have written a new version of Root from scratch, providing (almost) all the ones from the previous version, as well as multiple new ones.
Here I will detail the plugin's functionality including commands, use cases, permissions and configuration.

#Chat:

###Antispam:
Root currently has no automatic chat filtering or autokick.
The permission *root.chat.nodisconnectspam* allows users to send chat messages and commands quickly without being automatically kicked by the server's internal flooding protection.

###Ding:
Players can configure alerts for the occurrence of certain patterns in chat. See Commands/Ding for usage

###Firstjoin:
When a player joins for the first time, a message revealing the player's IP address and
geographical location will be broadcast to users with the permission *root.notify.firstjoin*.

###Flykick:
When a player is kicked by the server for floating too long, a warning is broadcast to
users with the permission *root.notify.flykick*.

###IPRec:
When a player is banned and joins with a different account from the same IP address, a warning is broadcast to
users with the permission *root.notify.iprec*. IP addresses are NOT remembered across server restarts.

###Marks
When a player with high priority marks joins, a warning is broadcast to users with the permission *root.notify.mark*.

###Subtitles:
Subtitles can be enabled by a command. See Commands/Sub for usage details.

###XRay:
When a player mines UNDERGROUND diamond ore (Y <= 16) at a suspiciously high rate (configurable, 20 ore blocks in less than 15 minutes by default),
a warning is broadcast to users with the permission *root.notify.xray*.


###Command Aliases and Macros

Commands for other plugins can be intercepted and altered with configurable regular expressions.
This makes it possible to define custom shortcuts for commands. Root uses this functionality on its own commands by default:
the /inv commands have aliases that provide the old /iload and /isave commands.
This same feature also provides the alias /j for /jump.


#Commands:

###/activity [player]
Displays a heatmap of a player's activity throughout times of day and days of week
Permission: *root.activity*

###/ding [pattern]
Plays a note when a chat messages matches a user-specified pattern. A basic example of a pattern is brain|brainiac. The pattern can be any valid regular expression.
Permission: *root.ding*

###/freeze [player]
Freezes a player.
Permission: *root.freeze*

###/instantsign line1|line2|line3|line4 [amount]
Creates an Instant Sign that, when placed, appears with content without editing.
Note: See section "Items" for a separate permission that enables usage of the item
Aliases: /sign
Permission: *root.instantsign*

###/inv [save/load/list/delete] [player]
Saves and loads inventories. The command now accepts a player name to load and edit someone else's inventories.
Notw: See "Chat" for why this does not mean a syntax change
Permission: *root.inventory*, *root.inventory.other*

###/kleinbottle
Creates an item that can transfer all of a Tesseract's contents instantly.
Note: See "Items" for a separate permission that enables usage of the item
Permission: *root.kleinbottle*

###/lore [set/add/del/insert/copy/paste]
Edits an item's lore. Can parse ampersand color codes.
Permission: *root.lore*

###/mark [player]
Keep notes about specific players
Permission: *root.mark*

###/name [name]
Edits an item's name. Can parse ampersand color codes.
Permission: *root.name*

###/nv
Toggles permanent night vision.
Aliases: /see
Permission: *root.nightvision*

###/player [player]
Still has its old functionality.
Note: Can now list a user's homes.
Aliases: /p
Permission: *root.player*, *root.player.homes*

###/seelwc [radius]
Makes blocks protected by the LWC plugin visible through walls
Permission: root.seelwc

###/shadowmute [player]
Makes a player's chat messages only visible to that player.
Aliases: /smute
Permission: *root.shadowmute*

###/seelwc [radius]
Highlights nearby blocks protected by the LWC plugin.
Permission: *root.seelwc*

###/slurp [radius]
Absorbs all items and XP in the given radius. Limits can be set in the configuration file.
Permission: *root.slurp*

###/sub [player] [source language / off] [sub language]
Displays machine-translated subtitles after a player's messages.
Permission: *root.sub*

###/undercover
Hides the user's rank badge in chat.
Permission: *root.undercover*

###/volatile
Toggles a line of lore to an item that makes it impossible to pick up for players without the permission *root.item.volatile*
Note: See Items/Volatile
Permission: *root.volatile*

###/wand [name]
Toggles one of a collection of special click actions on an item.
Creating any particular wand requires the command permission as well as the permission to use the wand.
See "Wands" for a full list of existing wands and permissions.
Permission: *root.wand*



###LS/AG jobs
have been moved into their own plugin, which can now be configured to host arbitrary job lists.
See the documentation of Tickets for usage.


#Items:

###Instant Signs
Sign items with lore which, when placed, appear with text and without an edit screen.
Ampersand color codes are stored UNPARSED in the lore and parsed on placement. See Commands/Instantsign
Permission: *root.item.instantsign*

###Klein Bottles
Bottles of dragon breath with lore. Can transfer all of a Tesseract's contents with one click. See Commands/Kleinbottle
Permission: *root.item.kleinbottle*

###Volatile
Cannot be picked up by a player who does not have a special permission.
Instead, the item will be deleted immediately. This serves to prevent admin gear from getting into the wrong hands. See Commands/Volatile
Permission: *root.item.volatile*

#Signs:

###[Boat]
Right-clicking this sign spawns a boat and places the user into the boat. The boat will disappear once the player leaves it.
Permission: *root.sign.boat*, *root.sign.boat.create*

###[Cart]
Right-clicking this sign spawns a cart and places the user into the cart. The cart will disappear once the player leaves it.
Additionally, a number can be placed in line 3 to adjust the cart's maximum speed.
Cart physics do not allow carts with altered maximum speeds to climb a slope, even on powered rails.
Permission: *root.sign.cart*, *root.sign.cart.create*

###[Info]
Displays a block of text when right-clicked. Text can be changed by sneak-right-clicking the Info sign with a book and quill.
Requires a unique name in line 2. Text content will be shared across all Info signs with the same name.
The sign will parse ampersand color codes (&) in the book text.
Permission: *root.sign.info*, *root.sign.info.create*

###[Launch]
Launches the user to the hight indicated in line 3, flies the user
horizontally to the X/Z coordinates given in lines 2 and 4, and finally descends with a firework trail.
Permission: *root.sign.launch*, *root.sign.launch.create*

###[Lift Up] [Lift Down]
Lift signs. The teleportation behavior has been improved such that the user will stay on the same X/Z coordinateswhen using a lift.
Permission: *root.sign.lift*, *root.sign.lift.create*

###[Petition]
Creates a petition users can sign to vote on public matters. Anonymity is guaranteed through salted SHA-1 hashing.
Permission: *root.sign.petition*, *root.sign.petition.create*

###[Tesseract]
Can hold practically a unlimited number (2^63 or 9.22x10<sup>18</sup>) of one kind of item without enchantments or lore.
Cannot be created outside of a WorldGuard protection or used by a player who is not a member
of the protection unless the player has the admin permission.
Permission: *root.sign.tesseract*, *root.sign.tesseract.create*, *root.sign.tesseract.admin*


#Wands:

Wands can be applied to items that are not blocks. They serve special functions when the item is used to interact with a block or entity.

###PetOwner:
Click a tameable entity to find out who owns it.
Permission: *root.wand.petowner*

###SilkDick:
Break a sign with this item to make it drop as an instant sign with its original contents.
Permission: *root.wand.silkdick*

###Stack:
Click two mobs (or players) to stack them (uses the vehicle interface that all entites have, not just carts).
Permission: *root.wand.stack*

###Strip:
Click an entity to make it drop the items it holds in its hands and the armor it is wearing.
Permission: *root.wand.strip*, *root.wand.strip.player*


#Configuration:

Root now stores all information in vaguely human-readable YAML files.
Only a few configurable parameters are available in config.yml. All other files should not be edited by humans.

