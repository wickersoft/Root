package wickersoft.root;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import syn.root.user.UserData;
import wickersoft.root.HTTP.HTTPResponse;

public class TaskGeoQuery extends BukkitRunnable {

    private final UserData data;
    private final String ip;
    private final Consumer<Map<Object, Object>> callback;

    public TaskGeoQuery(UserData data, boolean detailed, Consumer<Map<Object, Object>> callback) {
        this.data = data;
        this.ip = data.getIp();
        this.callback = callback;
    }

    @Override
    public void run() {
        Map<Object, Object> resultData = new HashMap<>();

        try {
            String queryUrl = "http://www.geoplugin.net/csv.gp?ip=" + data.getIp();
            HTTPResponse queryResponse = HTTP.http(queryUrl, 3000);
            String geoResponse = new String(queryResponse.content);
            
            String[] lines = geoResponse.split("\n");
            for(String line : lines) {
                int commaLoc = line.indexOf(",");
                if(commaLoc != -1) {
                    if(commaLoc == line.length() - 1) {
                        resultData.put(line.substring(0, commaLoc), "Unknown");
                    } else {
                        resultData.put(line.substring(0, commaLoc), line.substring(commaLoc + 1));
                    }
                }
            }
            
            String latitude = (String) resultData.getOrDefault("geoplugin_latitude", "0");
            String longitude = (String) resultData.getOrDefault("geoplugin_longitude", "0");

            HTTPResponse mapsResponse = HTTP.http("https://maps.googleapis.com/maps/api/timezone/json?"
                    + "location=" + latitude + "," + longitude
                    + "&timestamp=" + System.currentTimeMillis() / 1000
                    + "&key=" + Storage.GOOGLE_MAPS_API_KEY,
                    3000);
            String mapsData = new String(mapsResponse.content);
            String timezone = StringUtil.extract(mapsData, "\"timeZoneId\" : \"", "\",");
            if(timezone == null) {
                timezone = "Unknown";
            }
            resultData.put("maps_timezone", timezone);
        } catch (Exception ex) {
            System.err.println("Exception making GeoQuery for Player " + data.getName() + ":");
            ex.printStackTrace();
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(Root.instance(), () -> {
            callback.accept(resultData);
        });
    }
}
