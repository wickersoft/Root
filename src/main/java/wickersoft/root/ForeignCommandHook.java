/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import syn.root.user.UserData;
import syn.root.user.UserDataProvider;

/**
 *
 * @author Dennis
 */
public abstract class ForeignCommandHook {

    private static final List<ForeignCommandHook> HOOKS = new LinkedList<>();

    public static void onCommand(PlayerCommandPreprocessEvent evt) {
        for (ForeignCommandHook hook : HOOKS) {
            if (hook.getPattern().matcher(evt.getMessage()).find()
                    && evt.getPlayer().hasPermission(hook.getPermission())) {
                hook.consume(evt);
            }
        }
    }

    public static void load() {
        HOOKS.clear();
        try {
            for (Class clazz : ForeignCommandHook.class.getClasses()) {
                ForeignCommandHook hook = (ForeignCommandHook) clazz.newInstance();
                HOOKS.add(hook);
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            System.err.println("Foreign command hooks are broken!");
            ex.printStackTrace();
        }
    }

    protected abstract Pattern getPattern();

    protected abstract String getPermission();

    protected abstract void consume(PlayerCommandPreprocessEvent evt);

    public static class BanHook extends ForeignCommandHook {

        private final Pattern p = Pattern.compile("\\/(temp)?ban (\\w+)");

        @Override
        public Pattern getPattern() {
            return p;
        }

        @Override
        public String getPermission() {
            return "essentials.ban";
        }

        @Override
        protected void consume(PlayerCommandPreprocessEvent evt) {
            Matcher m = p.matcher(evt.getMessage());
            if (!m.find()) {
                return;
            }
            String playerName = m.group(2);
            if (playerName.equals("")) {
                return;
            }
            UserData data = UserDataProvider.getUser(playerName);
            if (data == null) {
                return;
            }
            String ip = data.getIp();
            if (ip.equals("") || ip.equals("Unknown")) {
                return;
            }
            Storage.WARN_IPS.put(ip, data.getName());
        }
    }

    public static class RgDefineHook extends ForeignCommandHook {

        private final Pattern p = Pattern.compile("^\\/(rg|region) (define|overlap)");

        @Override
        public Pattern getPattern() {
            return p;
        }

        @Override
        public String getPermission() {
            return "worldguard.region.define";
        }

        @Override
        protected void consume(PlayerCommandPreprocessEvent evt) {
            Matcher m = p.matcher(evt.getMessage());
            if (!m.find()) {
                return;
            }
            
            List<String> regionNames = Util.getRegionsCollidingWithPlayerSelection(evt.getPlayer());

            if (!regionNames.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append(ChatColor.BLUE);
                sb.append("Root: ");
                sb.append(ChatColor.GRAY);
                sb.append("This region overlaps with neighboring regions: ");
                sb.append(ChatColor.RED);
                sb.append(regionNames.remove(0));
                while (!regionNames.isEmpty()) {
                    sb.append(ChatColor.GRAY).append(", ").append(ChatColor.RED).append(regionNames.remove(0));
                }
                evt.getPlayer().sendMessage(sb.toString());
                return;
            }
            
            if(m.group(2).equals("overlap")) {
                evt.setCancelled(true);
                if(regionNames.isEmpty()) {
                    evt.getPlayer().sendMessage(ChatColor.BLUE + "Root: " + ChatColor.GRAY + "Your selection does not overlap woth any WorldGuard regions");
                }
            }
        }
    }
}
