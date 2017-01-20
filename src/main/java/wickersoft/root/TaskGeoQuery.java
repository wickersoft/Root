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
    private final boolean detailed;
    private final Consumer<Map<Object, Object>> callback;

    public TaskGeoQuery(UserData data, boolean detailed, Consumer<Map<Object, Object>> callback) {
        this.data = data;
        this.ip = data.getIp();
        this.detailed = detailed;
        this.callback = callback;
    }

    @Override
    public void run() {
        Map<Object, Object> resultData = new HashMap<>();

        try {
            HTTPResponse queryResponse = HTTP.http("http://www.geoplugin.net/php.gp?ip=" + URLEncoder.encode(ip, "UTF-8"), 3000);
            String geoResponse = new String(queryResponse.content);
            SerializedPhpParser serializedPhpParser = new SerializedPhpParser(geoResponse);

            resultData.putAll((Map<Object, Object>) serializedPhpParser.parse());

            String latitude = (String) resultData.get("geoplugin_latitude");
            String longitude = (String) resultData.get("geoplugin_longitude");

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
