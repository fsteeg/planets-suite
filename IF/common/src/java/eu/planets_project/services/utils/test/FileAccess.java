package eu.planets_project.services.utils.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import eu.planets_project.services.utils.FileUtils;

/**
 * Access to test files. Implemented as an enum to provide an ironclad
 * singleton. Usage: {@code TestFiles.INSTANCE.get("gif")}
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public enum FileAccess {
    INSTANCE;
    private HashMap<String, File> map;

    private FileAccess() {
        File root = new File("tests/test-files");
        map = new HashMap<String, File>();
        index(root, map);
        System.out.println("Indexed test files for " + map.keySet().size()
                + " extensions");
    }

    /**
     * @param extension The extension of the desired file
     * @return A random file from the test files pool with the given extension,
     *         or null
     */
    public File get(final String extension) {
        return map.get(extension.toLowerCase());
    }

    private static void index(File root, Map<String, File> map) {
        String[] list = root.list();
        for (String name : list) {
            File f = new File(root, name);
            if (f.isDirectory() && !f.isHidden()) {
                index(f, map);
            } else if (f.isFile() && !f.isHidden()) {
                map.put(FileUtils.getExtensionFromFile(f).toLowerCase(), f);
            }
        }
    }

}
