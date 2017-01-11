package wickersoft.root;

import wickersoft.root.command.CommandDelegator;
import java.io.File;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import syn.root.user.UserDataProvider;

/**
 *
 * @author Dennis
 */
public class Root extends JavaPlugin {

    private static Root instance;
    public static final String logo = ChatColor.LIGHT_PURPLE + "[" + ChatColor.DARK_AQUA + "Root" + ChatColor.LIGHT_PURPLE + "] ";
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return CommandDelegator.onCommand(sender, cmd.getLabel().toLowerCase(), args);
    }

    @Override
    public void onDisable() {
        UserDataProvider.saveData();
        Storage.saveData();
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        new File("plugins/Root").mkdir();
        new File("plugins/Root/users").mkdir();
        Storage.loadData();
        UserDataProvider.loadData();
        ForeignCommandHook.load();
        Wand.load();
        getServer().getPluginManager().registerEvents(WatcherChat.instance(), this);
        getServer().getPluginManager().registerEvents(WatcherPlayer.instance(), this);
        getServer().getPluginManager().registerEvents(WatcherSign.instance(), this);
        getServer().getPluginManager().registerEvents(WatcherWand.instance(), this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, MFEffects.instance(), 6000, 6000);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, ELFEffects.instance(), 20, 20);
    }
    
    public static Root instance() {
        return instance;
    }

}
