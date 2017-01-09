package wickersoft.root;

import java.net.URLEncoder;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import syn.root.user.UserData;
import wickersoft.root.HTTP.HTTPResponse;

public class TaskGeoQuery extends BukkitRunnable {

    private final UserData data;
    private final String ip;
    private final boolean broadcast;

    public TaskGeoQuery(UserData data, boolean isFirstJoin) {
        this.data = data;
        this.ip = data.getIp();
        this.broadcast = isFirstJoin;
    }

    @Override
    public void run() {
        String geoLocation;
        try {
            HTTPResponse queryResponse = HTTP.http("http://www.geoplugin.net/php.gp?ip=" + URLEncoder.encode(ip, "UTF-8"), 3000);
            String geoResponse = new String(queryResponse.content);
            SerializedPhpParser serializedPhpParser = new SerializedPhpParser(geoResponse);

            Map<Object, Object> qqq = (Map<Object, Object>) serializedPhpParser.parse();
            geoLocation = (String) qqq.get("geoplugin_regionName") 
                    + ", " +(String) qqq.get("geoplugin_countryName");
        } catch (Exception ex) {
            System.err.println("Exception making GeoQuery for Player " + data.getName() + ":");
            geoLocation = "Unknown: IO Error";
            ex.printStackTrace();
        }

        final String finalGeoLocation = geoLocation;
        Bukkit.getScheduler().scheduleSyncDelayedTask(Root.instance(), () -> {
            data.setGeoLocation(finalGeoLocation);
            if (broadcast) {
                Bukkit.broadcast(ChatColor.BLUE + data.getName() + ChatColor.GRAY + ": " 
                        + ChatColor.BLUE + ip + ChatColor.GRAY + " / " 
                        + ChatColor.BLUE + finalGeoLocation, "root.notify.firstjoin");
            }
        });
    }
}
