package net.lax1dude.eaglercraft.fabric.loader;

import java.io.File;
import java.io.InputStream;
import java.util.jar.JarFile;

public class ModJarScanner {

    public static void main(String[] args) {
        scan();
    }

    public static void scan() {
        File modsDir = new File("../mods");

        if (!modsDir.exists() || !modsDir.isDirectory()) {
            System.out.println("[MOD-LOADER] mods/ directory not found.");
            return;
        }

        File[] jars = modsDir.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".jar"));

        if (jars == null || jars.length == 0) {
            System.out.println("[MOD-LOADER] No mod JARs found.");
            return;
        }

        for (File jar : jars) {
            scanJar(jar);
        }
    }

    private static void scanJar(File file) {
        System.out.println("[MOD-LOADER] Found JAR: " + file.getName());

        try (JarFile jar = new JarFile(file)) {
            if (jar.getJarEntry("fabric.mod.json") == null) {
                System.out.println("[MOD-LOADER] No fabric.mod.json: " + file.getName());
                return;
            }

            try (InputStream input = jar.getInputStream(
                    jar.getJarEntry("fabric.mod.json"))) {

                String json = new String(input.readAllBytes(),
                        java.nio.charset.StandardCharsets.UTF_8);

                System.out.println("[MOD-LOADER] Found fabric.mod.json: "
                        + file.getName());

                printField(json, "id");
                printField(json, "name");
                printField(json, "version");
                printSection(json, "dependencies");
                printSection(json, "entrypoints");
            }

        } catch (Exception e) {
            System.out.println("[MOD-LOADER] Failed to read: "
                    + file.getName());
        }
    }
    private static void printSection(String json, String field) {
        String key = "\"" + field + "\"";
        int start = json.indexOf(key);

        if (start < 0) {
            System.out.println("[MOD-LOADER] " + field + ": <none>");
            return;
        }

        int colon = json.indexOf(':', start);
        int end = json.indexOf('\n', colon);

        if (end < 0) {
            end = json.length();
        }

        System.out.println("[MOD-LOADER] " + field + ": "
                + json.substring(colon + 1, end).trim());
    }

    private static void printField(String json, String field) {
        String key = "\"" + field + "\"";
        int start = json.indexOf(key);

        if (start < 0) {
            System.out.println("[MOD-LOADER] " + field + ": <missing>");
            return;
        }

        int colon = json.indexOf(':', start);
        int firstQuote = json.indexOf('"', colon + 1);
        int secondQuote = json.indexOf('"', firstQuote + 1);

        if (firstQuote < 0 || secondQuote < 0) {
            System.out.println("[MOD-LOADER] " + field + ": <invalid>");
            return;
        }

        System.out.println("[MOD-LOADER] " + field + ": "
                + json.substring(firstQuote + 1, secondQuote));
    }

}
