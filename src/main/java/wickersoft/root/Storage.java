/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import com.earth2me.essentials.Essentials;
import com.griefcraft.lwc.LWCPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

/**
 *
 * @author Dennis
 */
public class Storage {

    public static final BlockFace[] CARDINAL_FACES = {BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST};
    public static final HashMap<String, String> INFO_SIGNS = new HashMap<>();
    public static final HashMap<String, String> LANGUAGE_ALIASES = new HashMap<>();
    public static final HashMap<String, Petition> PETITIONS = new HashMap<>();
    public static final HashMap<Pattern, String> SHORTCUTS = new HashMap<>();
    public static final HashSet<Entity> VEHICLES = new HashSet<>();
    public static final HashMap<String, String> WARN_IPS = new HashMap<>();
    public static final Random RANDOM = new Random();
    public static final String[] KNOWN_LANGCODES = {
        "en", "de", "da", "sv", "no", "fr", "es"
    };

    public static String UNDERCOVER_CHAT_FORMAT;
    public static String SHADOWMUTE_SEE_CHAT_FORMAT;
    public static String MYMEMORY_TRANSLATED_NET_API_KEY;
    public static String GOOGLE_MAPS_API_KEY;
    public static String BAN_APPEAL_MESSAGE;
    public static boolean INV_SAVE_AUTO_OVERWRITE;
    public static int MAX_SLURP_RANGE;
    public static int DEFAULT_SLURP_RANGE;
    public static int TRANSLATION_TIMEOUT;
    public static long XRAY_WARN_TIME;
    public static long MAX_DEATH_INV_AGE_MILLIS;
    public static boolean DEBUG;
    public static MessageDigest md5;

    public static Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    public static WorldGuardPlugin worldguard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
    public static LWCPlugin lwc = (LWCPlugin) Bukkit.getPluginManager().getPlugin("LWC");

    public static void loadData() {
        YamlConfiguration fc = YamlConfiguration.read(new File(Root.instance().getDataFolder(), "config.yml"));
        UNDERCOVER_CHAT_FORMAT = ChatColor.translateAlternateColorCodes('&',
                fc.getString("undercover-chat-format", "<%1$s> %2$s"));
        SHADOWMUTE_SEE_CHAT_FORMAT = ChatColor.translateAlternateColorCodes('&',
                fc.getString("shadowmute-see-chat-format", "&8<%1&8$s> &8%2$s"));
        MYMEMORY_TRANSLATED_NET_API_KEY = fc.getString("mymemory-translated-net-api-key", "");
        GOOGLE_MAPS_API_KEY = fc.getString("google-maps-api-key", "");
        BAN_APPEAL_MESSAGE = fc.getString("ban-appeal-message", "");
        MAX_SLURP_RANGE = fc.getInt("max-slurp-range", 100);
        DEFAULT_SLURP_RANGE = fc.getInt("default-slurp-range", 16);
        TRANSLATION_TIMEOUT = fc.getInt("translation-timeout", 2000);
        INV_SAVE_AUTO_OVERWRITE = fc.getBoolean("inv-save-auto-overwrite", true);
        XRAY_WARN_TIME = fc.getInt("xray-warn-time-millis", 900000);
        MAX_DEATH_INV_AGE_MILLIS = fc.getLong("max-death-inventory-age-days", 14) * 86400 * 1000;
        DEBUG = fc.getBoolean("debug", false);

        List<YamlConfiguration> shortcuts = fc.getSectionList("shortcuts");
        SHORTCUTS.clear();
        shortcuts.forEach((map) -> {
            if (map.containsKey("replace") && map.containsKey("with")
                    && map.get("replace") instanceof String && map.get("with") instanceof String) {
                SHORTCUTS.put(Pattern.compile((String) map.get("replace")), (String) map.get("with"));
            }
        });

        INFO_SIGNS.clear();
        YamlConfiguration.read(new File(Root.instance().getDataFolder(), "infosigns.yml")).forEach(
                (key, value) -> {
                    if (value instanceof String) {
                        INFO_SIGNS.put(key, (String) value);
                    }
                });

        PETITIONS.clear();
        YamlConfiguration petitionData = YamlConfiguration.read(new File(Root.instance().getDataFolder(), "petitions.yml"));
        petitionData.keySet().forEach((key) -> {
            List<String> signatures = petitionData.getList(key, String.class);
            PETITIONS.put(key, new Petition(key, signatures));
        });

    }

    public static void saveData() {
        YamlConfiguration yaml = YamlConfiguration.emptyConfiguration();

        PETITIONS.forEach(
                (name, petition) -> {
                    yaml.put(name, petition.getSignatures());
                });
        try {
            yaml.save(new File(Root.instance().getDataFolder(), "petitions.yml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        yaml.clear();

        INFO_SIGNS.forEach(
                (name, value) -> {
                    yaml.put(name, value);
                });
        try {
            yaml.save(new File(Root.instance().getDataFolder(), "infosigns.yml"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
        }
        LANGUAGE_ALIASES.put("english", "en");
        LANGUAGE_ALIASES.put("german", "de");
        LANGUAGE_ALIASES.put("danish", "da");
        LANGUAGE_ALIASES.put("swedish", "sv");
        LANGUAGE_ALIASES.put("norwegian", "no");
        LANGUAGE_ALIASES.put("french", "fr");
        LANGUAGE_ALIASES.put("spanish", "es");
    }
}
