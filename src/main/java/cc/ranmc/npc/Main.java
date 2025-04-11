package cc.ranmc.npc;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;

public class Main extends JavaPlugin implements Listener {

    private static final String PLUGIN = "NpcDataTranslater";

    @Getter
    private final String PREFIX = color("&b" + PLUGIN + ">>>");

    private File dataFile;
    private YamlConfiguration dataYml;

    @Override
    public void onEnable() {
        print("&e-----------------------");
        print("&b" + PLUGIN + " &dBy阿然");
        print("&b插件版本:"+getDescription().getVersion());
        print("&b服务器版本:"+getServer().getVersion());
        print("&chttps://www.ranmc.cc/");
        print("&e-----------------------");

        loadConfig();

        //注册Event
        Bukkit.getPluginManager().registerEvents(this, this);

        super.onEnable();
    }

    /**
     * 指令输入
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             Command cmd,
                             @NotNull String label,
                             String[] args) {


        if (cmd.getName().equalsIgnoreCase("bkp")) {
            if (args.length == 1 &&
                    args[0].equalsIgnoreCase("reload")){
                if(sender.hasPermission("bkp.admin")) {
                    loadConfig();
                    sender.sendMessage(PREFIX + color("&a重载成功"));
                    return true;
                } else {
                    sender.sendMessage(PREFIX + color("&c没有权限"));
                }
            }
        }

        if (!(sender instanceof Player)) {
            print("&c该指令不能在控制台输入");
            return true;
        }

        sender.sendMessage(PREFIX + color("&c未知指令"));
        return true;
    }

    /**
     * 加载配置文件
     */
    private void loadConfig(){
        saveDefaultConfig();
        reloadConfig();

        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) saveResource("data.yml", false);
        dataYml = YamlConfiguration.loadConfiguration(dataFile);

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i <= dataYml.getInt("last-created-npc-id"); i++) {
            String name = dataYml.getString("npc." + i + ".name", "");
            if (!name.isEmpty()) {
                String cmd = dataYml.getString("npc." + i + ".traits.commandtrait.commands.0.command", "");
                name = cmd.contains("talk ") ? cmd.split(" ")[1] : name;
                getConfig().set("npcs." + name + ".displayName", name.split("copy")[0]);
                String type = dataYml.getString("npc." + i + ".traits.type", "PLAYER");
                getConfig().set("npcs." + name + ".type", type);
                getConfig().set("npcs." + name + ".location.world",
                        dataYml.getString("npc." + i + ".traits.location.world"));
                getConfig().set("npcs." + name + ".location.x",
                        Float.parseFloat(
                                Objects.requireNonNull(dataYml.getString("npc." + i + ".traits.location.x"))));
                getConfig().set("npcs." + name + ".location.y",
                        Float.parseFloat(
                                Objects.requireNonNull(dataYml.getString("npc." + i + ".traits.location.y"))));
                getConfig().set("npcs." + name + ".location.z",
                        Float.parseFloat(
                                Objects.requireNonNull(dataYml.getString("npc." + i + ".traits.location.z"))));
                getConfig().set("npcs." + name + ".location.yaw", Double.parseDouble(
                        Objects.requireNonNull(dataYml.getString("npc." + i + ".traits.location.yaw"))));
                getConfig().set("npcs." + name + ".location.pitch", Double.parseDouble(
                        Objects.requireNonNull(dataYml.getString("npc." + i + ".traits.location.pitch"))));
                getConfig().set("npcs." + name + ".showInTab", false);
                getConfig().set("npcs." + name + ".spawnEntity", true);
                getConfig().set("npcs." + name + ".glowing", false);
                getConfig().set("npcs." + name + ".glowingColor", "white");
                getConfig().set("npcs." + name + ".turnToPlayer",
                        dataYml.getBoolean("npc." + i + ".traits.lookclose.enabled", true));

                String skinName = dataYml.getString("npc." + i + ".traits.skintrait.skinName", "");
                if (!skinName.isEmpty()) {
                    /*getConfig().set("npcs." + name + ".skin.mirrorSkin", false);
                    getConfig().set("npcs." + name + ".skin.identifier",
                            dataYml.getString("npc." + i + ".traits.skintrait.skinName", ""));
                    getConfig().set("npcs." + name + ".skin.variant", "AUTO");*/
                    builder.append("/npc skin ").append(name).append(" ").append(skinName).append("\n");
                }
                getConfig().set("npcs." + name + ".actions.ANY_CLICK.1.action", "player_command");
                getConfig().set("npcs." + name + ".actions.ANY_CLICK.1.value", cmd);
                getConfig().set("npcs." + name + ".name", name);
                getConfig().set("npcs." + name + ".creator", "e6bccd09-1c8a-3fcf-92b9-afa792d206a0");
                getConfig().set("npcs." + name + ".collidable", true);
                getConfig().set("npcs." + name + ".visibility_distance", -1);
                getConfig().set("npcs." + name + ".scale", 1.0);
                getConfig().set("npcs." + name + ".interactionCooldown", 0.0);
                getConfig().set("npcs." + name + ".turnToPlayerDistance", -1);
            }
            saveConfig();
        }
        Bukkit.broadcastMessage(builder.toString());
    }

    /**
     * 执行指令
     */
    public void run(String command) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    /**
     * 文本颜色
     */
    private static String color(String text){
        return text.replace("&","§");
    }

    /**
     * 后台信息
     */
    public void print(String msg){
        Bukkit.getConsoleSender().sendMessage(color(msg));
    }

    /**
     * 公屏信息
     */
    public void say(String msg){
        Bukkit.broadcastMessage(color(msg));
    }
}
