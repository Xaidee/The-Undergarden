package quek.undergarden;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModList;

public class UndergardenConfig {

    private static final String config = UndergardenMod.MODID + ".config.";

    public static ForgeConfigSpec.BooleanValue disableOthersideTeleport;

    public static class CommonConfig {
        public CommonConfig(ForgeConfigSpec.Builder builder) {
            //builder.push("mod_compatibility");

            //builder.pop();

            disableOthersideTeleport = builder
                    .translation(config + "disable_otherside_teleport")
                    .comment("Set to true to disable The Otherside's void teleportation when entities are below y 0")
                    .define("disableOthersideTeleportation", false);
        }
    }

    public static class ClientConfig {
        public ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.push("client");

            builder.pop();
        }
    }

}
