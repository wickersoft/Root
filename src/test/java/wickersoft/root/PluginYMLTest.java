/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wickersoft.root;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import static junit.framework.Assert.assertTrue;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author Dennis
 */
public class PluginYMLTest extends TestCase {

    private final HashSet<String> declaredPermissions = new HashSet<>();

    @Test
    public void testPermissions() throws IOException {
        YamlConfiguration permBase = YamlConfiguration.read(new File("src/main/resources/plugin.yml")).getOrCreateSection("permissions");
        boolean complete = true;
        for (String key : permBase.keySet()) {
            String escapedKey = key.replace(".", "\\.");
            if (permBase.containsKey(escapedKey + ".children")) {
                for (String innerKey : permBase.getOrCreateSection(escapedKey + ".children").keySet()) {
                    String escapedInnerKey = innerKey.replace(".", "\\.");
                    if (innerKey.endsWith(".*")) {
                        if ((!permBase.containsKey(escapedInnerKey))) {

                            System.err.println("Plugin.yml: " + innerKey + " is not defined!");

                        } else if (!permBase.getOrCreateSection(escapedInnerKey)
                                .containsKey("children."
                                        + escapedInnerKey.substring(0, escapedInnerKey.length() - 3))) {
                            System.err.println("Plugin.yml: " + innerKey + " does not declare superkey!");
                            complete = false;
                        }
                    } else {
                        declarePermission(innerKey);
                    }
                }
            }
        }
        for(String key : declaredPermissions) {
            if(!permBase.containsKey(key)) {
                System.err.println("Missing superkey " + key.replace("\\.", "."));
                complete = false;
            }
        }
        assertTrue(complete);
    }

    private void declarePermission(String permission) {
        String[] components = permission.split("\\.");
        for (int i = 0; i < components.length - 1; i++) {
            String superPermission = "";
            for (int j = 0; j <= i; j++) {
                superPermission += components[j] + "\\.";
            }
            superPermission += "*";
            declaredPermissions.add(superPermission);
        }
    }
}
