package net.lax1dude.eaglercraft.fabric.loader;

public class ModInfo {

    public final String id;
    public final String name;
    public final String version;
    public final String mainEntrypoint;
    public final String clientEntrypoint;

    public ModInfo(
            String id,
            String name,
            String version,
            String mainEntrypoint,
            String clientEntrypoint) {

        this.id = id;
        this.name = name;
        this.version = version;
        this.mainEntrypoint = mainEntrypoint;
        this.clientEntrypoint = clientEntrypoint;
    }
}
