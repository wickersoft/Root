/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Dennis
 */
public class Mark {

    private String authorName;
    private String message;
    private long created;
    private long expires;
    private int priority;

    public Mark(YamlConfiguration section) {
        authorName = section.getString("authorName", "Unknwon");
        message = section.getString("message", "<no message>");
        created = section.getLong("created", 0);
        expires = section.getLong("expires", 0);
        priority = section.getInt("priority", 0);
    }

    public Mark(CommandSender author, String message) {
        if(author instanceof Player) {
            authorName = author.getName();
        } else {
            authorName = "Console";
        }
        this.message = message;
        created = System.currentTimeMillis();
        priority = 0;
    }

    public String format() {
        return "";
    }
    
    public String getAuthor() {
        return authorName;
    }
    
    public String getMessage() {
        return message;
    }

    public int getPriority() {
        return priority;
    }

    public long getExpiryTime() {
        return expires;
    }

    public void setExpiryTime(long expiryTime) {
        expires = expiryTime;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public YamlConfiguration toYaml() {
        YamlConfiguration yaml = YamlConfiguration.emptyConfiguration();
        yaml.set("authorName", authorName);
        yaml.set("message", message);
        yaml.set("creates", created);
        yaml.set("expires", expires);
        yaml.set("priority", priority);
        return yaml;
    }

}
