package cn.huohuas001.huhomonitor;

import cn.huohuas001.huHoBot.Api.BotCustomCommand;
import cn.huohuas001.huHoBot.HuHoBot;
import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class HuHoMonitor extends JavaPlugin implements Listener {
    private Logger logger; //Logger
    private static TaskScheduler scheduler;
    private static HuHoMonitor plugin; //插件对象

    /**
     * 获取插件
     *
     * @return 插件对象
     */
    public static HuHoMonitor getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        logger = getLogger();
        plugin = this;
        scheduler = UniversalScheduler.getScheduler(this);

        if (this.getServer().getPluginManager().getPlugin("HuHoBot") == null)
        {
            this.getLogger().severe("HuHoBot is not installed. Disabling...");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Plugin huhoBot = getServer().getPluginManager().getPlugin("HuHoBot");
        if (huhoBot == null) {
            getLogger().severe("HuHoBot 未安装");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!HuHoMonitor.getPlugin().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().warning("PlaceHolder 未安装,将不会支持PlaceHolder的变量");
        }

        try {
            Class.forName("cn.huohuas001.huHoBot.Api.BotCustomCommand");
        } catch (ClassNotFoundException e) {
            logger.severe("无法加载 BotCustomCommand 类：" + e.getMessage());
        }

        this.getServer().getPluginManager().registerEvents(this, huhoBot);

        this.saveDefaultConfig();

        logger.info("HuHoMonitor Loaded. By HuoHuas001");
    }

    @EventHandler
    public void onCommandSend(BotCustomCommand event)
    {
        if(event.getCommand().equals("服务器状态")){
            event.setCancelled(true);
            this.reloadConfig();
            String formatString = this.getConfig().getString("formatString");
            scheduler.runTaskAsynchronously(()->{
                String statusReport = SystemMonitor.getSystemStatus(formatString);
                event.respone(
                        statusReport,
                        "success"
                );
            });
        }
    }

}
