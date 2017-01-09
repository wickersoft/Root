package syn.root.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.bukkit.block.Block;
import wickersoft.root.Root;
import wickersoft.root.YamlConfiguration;

public class UserData {

    // Internal
    private final File dataFile;
    private final Player playerInstance;
    private final UUID uuid;

    // Persistent
    private String dingPatternString = "";
    private boolean frozen = false;
    private boolean shadowmuted = false;
    private String geoLocation = "Unknown";
    private final ArrayList<String> inbox = new ArrayList<>();
    private String subtitleLangPair = "";
    private String lastKnownIp = "Unknown";
    private String lastKnownName = "Unknown";
    private boolean undercover = false;

    // Transient
    private Object clipboard = null;
    private Pattern dingPattern;

    private long spamScore = 0;
    private long spamTestTime = 0;
    private int xrayTestPointer = 0;
    private final long[] xrayTestTimes = new long[20];

    public UserData(Player base) {
        this.playerInstance = base;
        this.uuid = base.getUniqueId();
        dataFile = new File(Root.instance().getDataFolder(), "users/" + uuid + "/playerdata.yml");
    }

    public UserData(UUID uuid) {
        this.playerInstance = null;
        this.uuid = uuid;
        dataFile = new File(Root.instance().getDataFolder(), "users/" + uuid + "/playerdata.yml");
    }

    public Object getClipboard() {
        return clipboard;
    }

    public String getDingPattern() {
        return dingPatternString;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public List<String> getInbox() {
        return inbox;
    }

    public String getIp() {
        return lastKnownIp;
    }

    public String getName() {
        return lastKnownName;
    }

    public Player getPlayerInstance() {
        return playerInstance;
    }

    public String getSubtitleLangPair() {
        return subtitleLangPair;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean isFrozen() {
        return frozen;
    }
    
    public boolean isShadowmuted() {
        return shadowmuted;
    }

    public boolean isUndercover() {
        return undercover;
    }

    public boolean matchDingPattern(String message) {
        return dingPattern != null && dingPattern.matcher(message).matches();
    }

    public void resetXrayWarnTime() {
        for (int i = 0; i < 20; i++) {
            xrayTestTimes[i] = 0;
        }
    }

    public void setClipboard(Object clipboard) {
        this.clipboard = clipboard;
    }

    public boolean setDingPattern(String patternString) {
        if (patternString == null || patternString.equals("")) {
            dingPatternString = "";
            dingPattern = null;
            return true;
        }
        try {
            Pattern tempPattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
            dingPatternString = patternString;
            dingPattern = tempPattern;
        } catch (PatternSyntaxException ex) {
            return false;
        }
        return true;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }

    public void setName(String newName) {
        lastKnownName = newName;
    }

    public void setIp(String newIp) {
        lastKnownIp = newIp;
    }
    
    public void setShadowmuted(boolean shadowmuted) {
        this.shadowmuted = shadowmuted;
    }

    public void setSubtitleLangPair(String subtitleLangPair) {
        this.subtitleLangPair = subtitleLangPair;
    }

    public void setUndercover(boolean undercover) {
        this.undercover = undercover;
    }

    public long testSpamWarnTime() {
        long newTime = xrayTestTimes[xrayTestPointer] = System.currentTimeMillis();
        xrayTestPointer = (xrayTestPointer + 1) % 20;
        return newTime - xrayTestTimes[xrayTestPointer];
    }

    public long testXrayWarnTime() {
        long newTime = xrayTestTimes[xrayTestPointer] = System.currentTimeMillis();
        xrayTestPointer = (xrayTestPointer + 1) % 20;
        return newTime - xrayTestTimes[xrayTestPointer];
    }

    public boolean loadData() {
        if (!dataFile.exists()) {
            return false;
        }
        YamlConfiguration fc = YamlConfiguration.read(dataFile);

        String patternString = fc.getString("dingPattern", "");
        if (!setDingPattern(patternString)) {
            dingPatternString = "";
            dingPattern = null;
        }
        frozen = fc.getBoolean("frozen", false);
        geoLocation = fc.getString("geolocation", "Unknown");
        List<String> _inbox = fc.getList("inbox", String.class);
        if (_inbox != null) {
            inbox.addAll(_inbox);
        }
        lastKnownName = fc.getString("name", "Unknown");
        lastKnownIp = fc.getString("ip", "Unknown");
        shadowmuted = fc.getBoolean("shadowmuted", false);
        undercover = fc.getBoolean("undercover", false);
        if (playerInstance != null) {
            lastKnownName = playerInstance.getName();
        }
        return true;
    }

    public void saveData() {
        YamlConfiguration yaml = YamlConfiguration.emptyConfiguration();
        yaml.set("dingPattern", dingPatternString);
        yaml.set("name", lastKnownName);
        yaml.set("inbox", inbox);
        yaml.set("ip", lastKnownIp);
        yaml.set("geolocation", geoLocation);
        yaml.set("frozen", frozen);
        yaml.set("shadowmuted", shadowmuted);
        yaml.set("undercover", undercover);

        try {
            dataFile.getParentFile().mkdir();
            yaml.save(dataFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Unable to save data for {0}! Data was lost.", uuid);
            e.printStackTrace();
        }
    }
}
