package syn.root.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.bukkit.block.Block;
import wickersoft.root.Mark;
import wickersoft.root.Root;
import wickersoft.root.StringUtil;
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
    private final ArrayList<Mark> marks = new ArrayList<>();
    private String subtitleLangPair = "";
    private String lastKnownIp = "Unknown";
    private String lastKnownName = "Unknown";
    private boolean undercover = false;
    private final int[] weeklyMetrics = new int[24 * 7];
    private final int[] yearlyMetrics = new int[13];

    // Transient
    private Pattern dingPattern;
    private long spamScore = 0;
    private long spamTestTime = 0;
    private int xrayTestPointer = 0;
    private final long[] xrayTestTimes = new long[20];
    private boolean dirty = true;

    protected UserData(Player base) {
        this.playerInstance = base;
        this.uuid = base.getUniqueId();
        dataFile = new File(Root.instance().getDataFolder(), "users/" + uuid + "/playerdata.yml");
    }

    protected UserData(UUID uuid) {
        this.playerInstance = null;
        this.uuid = uuid;
        dataFile = new File(Root.instance().getDataFolder(), "users/" + uuid + "/playerdata.yml");
    }
    
    public int addMark(Mark mark) {
        touch();
        marks.add(mark);
        return marks.size();
    }

    public String getDingPattern() {
        return dingPatternString;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public String getIp() {
        return lastKnownIp;
    }
    
    public List<Mark> getMarks() {
        return marks;
    }
    
    public int[] getWeeklyMetrics() {
        return weeklyMetrics;
    }

    public int[] getYearlyMetrics() {
        return yearlyMetrics;
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

    public void incrementActivityStatistic(int hour, int dayOfWeek, int month) {
        if (month != yearlyMetrics[12]) {
            yearlyMetrics[month] = 0;
            yearlyMetrics[12] = month;
        }
        weeklyMetrics[24 * dayOfWeek + hour]++;
        yearlyMetrics[month]++;
        touch();
    }

    public boolean isDirty() {
        return dirty;
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
        return dingPattern != null 
                && dingPattern.matcher(message).find();
    }
    
    public void removeMark(int markIndex) {
        touch();
        marks.remove(markIndex);
    }

    public void resetXrayWarnTime() {
        for (int i = 0; i < 20; i++) {
            xrayTestTimes[i] = 0;
        }
    }

    public boolean setDingPattern(String patternString) {
        touch();
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
        touch();
    }

    public void setGeoLocation(String geoLocation) {
        if (!this.geoLocation.equals(geoLocation)) {
            touch();
            this.geoLocation = geoLocation;
        }
    }

    public void setName(String newName) {
        if (!this.lastKnownName.equals(newName)) {
            touch();
            this.lastKnownName = newName;
        }
    }

    public void setIp(String newIp) {
        if (!this.lastKnownIp.equals(newIp)) {
            touch();
            this.lastKnownIp = newIp;
        }
    }

    public void setShadowmuted(boolean shadowmuted) {
        touch();
        this.shadowmuted = shadowmuted;
    }

    public void setSubtitleLangPair(String subtitleLangPair) {
        touch();
        this.subtitleLangPair = subtitleLangPair;
    }

    public void setUndercover(boolean undercover) {
        touch();
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

    public void touch() {
        dirty = true;
    }

    public boolean loadData() {
        if (!dataFile.exists()) {
            return false;
        }
        YamlConfiguration yaml = YamlConfiguration.read(dataFile);
        frozen = yaml.getBoolean("frozen", false);
        geoLocation = yaml.getString("geolocation", "Unknown");
        lastKnownName = yaml.getString("name", "Unknown");
        lastKnownIp = yaml.getString("ip", "Unknown");
        shadowmuted = yaml.getBoolean("shadowmuted", false);
        undercover = yaml.getBoolean("undercover", false);
        if (playerInstance != null) {
            lastKnownName = playerInstance.getName();
        }
        String patternString = yaml.getString("dingPattern", "");
        if (!setDingPattern(patternString)) {
            dingPatternString = "";
            dingPattern = null;
        }

        String weekString = yaml.getString("weeklyActivity", "");
        if (weekString.length() == 24 * 7 * 2) {
            char[] weekChars = weekString.toCharArray();
            for (int i = 0; i < 24 * 7; i++) {
                weeklyMetrics[i] = (int) StringUtil.getBase64Bits(weekChars, 2 * i, 2 * i + 2);
            }
        }

        String yearString = yaml.getString("yearlyActivity", "");
        if (weekString.length() == 13 * 3) {
            char[] yearChars = yearString.toCharArray();
            for (int i = 0; i < 24 * 7; i++) {
                weeklyMetrics[i] = (int) StringUtil.getBase64Bits(yearChars, 3 * i, 3 * i + 3);
            }
        }
        List<YamlConfiguration> markSectionList = yaml.getSectionList("marks");
        for (YamlConfiguration section : markSectionList) {
            marks.add(new Mark(section));
        }
        dirty = false;
        return true;
    }

    protected void saveData() {
        if(!dirty) {
            return;
        }
        YamlConfiguration yaml = YamlConfiguration.emptyConfiguration();
        yaml.set("dingPattern", dingPatternString);
        yaml.set("name", lastKnownName);
        yaml.set("ip", lastKnownIp);
        yaml.set("geolocation", geoLocation);
        yaml.set("frozen", frozen);
        yaml.set("shadowmuted", shadowmuted);
        yaml.set("undercover", undercover);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 24 * 7; i++) {
            sb.append(StringUtil.toBase64(weeklyMetrics[i], 2));
        }
        yaml.set("weeklyActivity", sb.toString());

        sb = new StringBuilder();
        for (int i = 0; i < 13; i++) {
            sb.append(StringUtil.toBase64(yearlyMetrics[i], 3));
        }
        yaml.set("yearlyActivity", sb.toString());

        List<YamlConfiguration> markSections = new LinkedList<>();
        for (Mark mark : marks) {
            markSections.add(mark.toYaml());
        }
        yaml.set("marks", markSections);

        try {
            dataFile.getParentFile().mkdir();
            yaml.save(dataFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Unable to save data for {0}! Data was lost.", uuid);
            e.printStackTrace();
        }
        dirty = false;
    }
}
