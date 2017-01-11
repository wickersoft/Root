package syn.root.user;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.Bukkit;
import wickersoft.root.Root;

/**
 * Provides data structures containing player-specific information.
 *
 * @author Dennis
 */
public class UserDataProvider {

    private static final HashMap<UUID, UserData> USER_MAP = new HashMap<>();
    private static final HashMap<String, UUID> UUID_RESOLUTION_MAP = new HashMap<>();

    /**
     * Provides a UserData object for the given Player. This method always
     * returns a valid UserData object, creating a YML file if none exists.
     * Objects returned by this method are cached for much faster access.
     *
     * @param player the player to query
     * @return the UserData corresponding to this player
     */
    public static synchronized UserData getOrCreateUser(Player player) {
        if (USER_MAP.containsKey(player.getUniqueId())) {
            return USER_MAP.get(player.getUniqueId());
        } else {
            UserData newUser = new UserData(player);
            if (!newUser.loadData()) {
                newUser.saveData();
            }
            USER_MAP.put(player.getUniqueId(), newUser);
            UUID_RESOLUTION_MAP.put(player.getName().toLowerCase(), player.getUniqueId());
            return newUser;
        }
    }

    /**
     * Provides a UserData object corresponding to the most recent UUID
     * associated with this name. Checks online players first for fast access
     * and error tolerance in typing, then consults the UUID resolution map. If
     * neither search is successful, null is returned.
     *
     * @param name the name to query
     * @return a UserData object if the search is successful, otherwise null
     */
    public static synchronized UserData getUser(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            return getOrCreateUser(player);
        }
        UUID uuid = UUID_RESOLUTION_MAP.get(name.toLowerCase());
        if (uuid == null) {
            return null;
        }
        return getUser(uuid);
    }

    /**
     * Provides a UserData object corresponding to the given UUID. Checks online
     * players first for fast access, then looks for a YML file on disk. If
     * neither search is successful, null is returned.
     *
     * @param uuid the uuid to query
     * @return a UserData object if the search is successful, otherwise null
     */
    public static synchronized UserData getUser(UUID uuid) {
        if (USER_MAP.containsKey(uuid)) {
            return USER_MAP.get(uuid);
        }
        UserData newUser = new UserData(uuid);
        if (!newUser.loadData()) {
            return null;
        }
        USER_MAP.put(uuid, newUser);
        return newUser;
    }

    /**
     * Attempts to find a UUID associated with the given name.
     *
     * @param name the name to query
     * @return the corresponding UUID, or null
     */
    public static synchronized UUID getUUID(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            return player.getUniqueId();
        }
        return UUID_RESOLUTION_MAP.get(name.toLowerCase());
    }

    /**
     * Saves UserData objects for offline player and removes them from RAM.
     */
    public static synchronized void garbageCollect() {
        Iterator<Entry<UUID, UserData>> userMapIt = USER_MAP.entrySet().iterator();
        while (userMapIt.hasNext()) {
            Entry<UUID, UserData> entry = userMapIt.next();
            if (!Bukkit.getPlayer(entry.getKey()).isOnline()) {
                entry.getValue().saveData();
                userMapIt.remove();
            }
        }
    }

    /**
     * Saves all data to disk.
     */
    public static synchronized void saveData() {
        USER_MAP.forEach((player, data) -> {
            data.saveData();
        });
        File resolutionMapFile = new File(Root.instance().getDataFolder(), "uuidResolutionMap.bin");
        try {
            FileOutputStream fos = new FileOutputStream(resolutionMapFile, false);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            DataOutputStream dos = new DataOutputStream(bos);
            for (Entry<String, UUID> entry : UUID_RESOLUTION_MAP.entrySet()) {
                dos.write(entry.getKey().getBytes());
                dos.write(':');
                dos.writeLong(entry.getValue().getMostSignificantBits());
                dos.writeLong(entry.getValue().getLeastSignificantBits());
                dos.write('\n');
            }
            dos.flush();
            bos.flush();
            fos.flush();
            fos.close();
        } catch (IOException ex) {
            System.err.println("Unable to save UUID Resolution table! Data was lost.");
            ex.printStackTrace();
        }
    }

    /**
     * Loads data from disk.
     */
    public static synchronized void loadData() {
        File resolutionMapFile = new File(Root.instance().getDataFolder(), "uuidResolutionMap.bin");
        if (!resolutionMapFile.exists()) {
            return;
        }
        int lineNr = 0;
        try {
            FileInputStream fis = new FileInputStream(resolutionMapFile);
            DataInputStream dis = new DataInputStream(new BufferedInputStream(fis));
            char[] nameBuffer = new char[16];
            int nameBufferIndex;
            char nextCharacter;

            while (dis.available() > 0) {

                lineNr++;
                nameBufferIndex = 0;

                while (true) {
                    // Read until colon char is reached or name exceeds 16 bytes, then skip until colon char                 
                    nextCharacter = (char) dis.readByte();
                    if (nextCharacter == ':') {
                        String name = new String(nameBuffer, 0, nameBufferIndex);
                        UUID uuid = new UUID(dis.readLong(), dis.readLong());
                        UUID_RESOLUTION_MAP.put(name, uuid);
                        break;
                    } else if (nameBufferIndex == 16) {
                        System.err.println("Error parsing UUID resolution map at line " + lineNr);
                        break;
                    } else {
                        nameBuffer[nameBufferIndex++] = nextCharacter;
                    }
                }

                do {
                } while (dis.available() > 0 && dis.readByte() != '\n');
            }
        } catch (IOException ex) {
            System.err.println("Error parsing UUID resolution map at line " + lineNr);
        }
    }
}
