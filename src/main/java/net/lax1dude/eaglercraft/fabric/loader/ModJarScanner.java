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

                String id = readField(json, "id");
                String name = readField(json, "name");
                String version = readField(json, "version");
                String mainEntrypoint = readEntrypoint(json, "main");
                String clientEntrypoint = readEntrypoint(json, "client");

                ModInfo info = new ModInfo(
                        id,
                        name,
                        version,
                        mainEntrypoint,
                        clientEntrypoint
                );

                System.out.println("[MOD-LOADER] ModInfo: "
                        + info.id + " " + info.version);

                printSection(json, "dependencies");
                printSection(json, "entrypoints");
            }

        } catch (Exception e) {
            System.out.println("[MOD-LOADER] Failed to read: "
                    + file.getName());
        }
    }

    private static void printEntrypointClasses(String json) {
        String key = "\"main\"";
        int start = json.indexOf(key);

        if (start < 0) {
            System.out.println("[MOD-LOADER] main entrypoint: <none>");
            return;
        }

        int bracket = json.indexOf('[', start);
        int end = json.indexOf(']', bracket);

        if (bracket < 0 || end < 0) {
            System.out.println("[MOD-LOADER] main entrypoint: <invalid>");
            return;
        }

        String section = json.substring(bracket + 1, end);
        int firstQuote = section.indexOf('"');
        int secondQuote = section.indexOf('"', firstQuote + 1);

        if (firstQuote < 0 || secondQuote < 0) {
            System.out.println("[MOD-LOADER] main entrypoint: <none>");
            return;
        }

        System.out.println("[MOD-LOADER] main entrypoint: "
                + section.substring(firstQuote + 1, secondQuote));
    }

    private static void printSection(String json, String field) {
        String key = "\"" + field + "\"";
        int start = json.indexOf(key);

        if (start < 0) {
            System.out.println("[MOD-LOADER] " + field + ": <none>");
            return;
        }

        int colon = json.indexOf(':', start);
        if (colon < 0) {
            System.out.println("[MOD-LOADER] " + field + ": <invalid>");
            return;
        }

        int valueStart = colon + 1;

        while (valueStart < json.length()
                && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        if (valueStart >= json.length()) {
            System.out.println("[MOD-LOADER] " + field + ": <invalid>");
            return;
        }

        char opening = json.charAt(valueStart);

        if (opening != '{' && opening != '[') {
            System.out.println("[MOD-LOADER] " + field + ": <invalid>");
            return;
        }

        char closing = opening == '{' ? '}' : ']';
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;

        for (int i = valueStart; i < json.length(); i++) {
            char c = json.charAt(i);

            if (escaped) {
                escaped = false;
                continue;
            }

            if (c == '\\' && inString) {
                escaped = true;
                continue;
            }

            if (c == '"') {
                inString = !inString;
                continue;
            }

            if (inString) {
                continue;
            }

            if (c == opening) {
                depth++;
            } else if (c == closing) {
                depth--;

                if (depth == 0) {
                    System.out.println("[MOD-LOADER] " + field + ": "
                            + json.substring(valueStart, i + 1).trim());
                    return;
                }
            }
        }

        System.out.println("[MOD-LOADER] " + field + ": <invalid>");
    }

    private static String readEntrypoint(String json, String type) {
        String key = "\"" + type + "\"";
        int start = json.indexOf(key);

        if (start < 0) {
            return null;
        }

        int bracket = json.indexOf('[', start);
        int end = json.indexOf(']', bracket);

        if (bracket < 0 || end < 0) {
            return null;
        }

        String section = json.substring(bracket + 1, end);
        int firstQuote = section.indexOf('"');
        int secondQuote = section.indexOf('"', firstQuote + 1);

        if (firstQuote < 0 || secondQuote < 0) {
            return null;
        }

        return section.substring(firstQuote + 1, secondQuote);
    }

    private static String readField(String json, String field) {
        String key = "\"" + field + "\"";
        int start = json.indexOf(key);

        if (start < 0) {
            return null;
        }

        int colon = json.indexOf(':', start);
        int firstQuote = json.indexOf('"', colon + 1);
        int secondQuote = json.indexOf('"', firstQuote + 1);

        if (firstQuote < 0 || secondQuote < 0) {
            return null;
        }

        return json.substring(firstQuote + 1, secondQuote);
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
