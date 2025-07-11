package cn.huohuas001.huhowhitelist;

// Bukkit 相关
import com.alibaba.fastjson2.JSONObject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

// 其他第三方
import cn.huohuas001.huHoBot.Api.BotCustomCommand;
import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public final class HuHoWhiteList extends JavaPlugin implements Listener {
    private Logger logger; //Logger
    private static TaskScheduler scheduler;
    private static HuHoWhiteList plugin; //插件对象
    private File bindingsFile;
    private Map<String, String> bindings = new HashMap<>(); // QQ -> 玩家名

    /**
     * 获取插件
     *
     * @return 插件对象
     */
    public static HuHoWhiteList getPlugin() {
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

        try {
            Class.forName("cn.huohuas001.huHoBot.Api.BotCustomCommand");
        } catch (ClassNotFoundException e) {
            logger.severe("无法加载 BotCustomCommand 类：" + e.getMessage());
        }

        this.getServer().getPluginManager().registerEvents(this, huhoBot);

        this.saveDefaultConfig();
        initBindingsYaml();

        logger.info("HuHoWhiteList Loaded. By HuoHuas001");
    }

    /**
     * 获取计划对象
     *
     * @return 计划对象
     */
    public static TaskScheduler getScheduler() {
        return scheduler;
    }

    private void initBindingsYaml() {
        bindingsFile = new File(getDataFolder(), "bindings.yml");
        if (!bindingsFile.exists()) {
            try {
                getDataFolder().mkdirs();
                bindingsFile.createNewFile();
                saveBindings();
            } catch (IOException e) {
                logger.severe("无法创建 bindings.yml: " + e.getMessage());
            }
        }
        loadBindings();
    }

    private void loadBindings() {
        Yaml yaml = new Yaml();
        try (FileReader reader = new FileReader(bindingsFile)) {
            Object data = yaml.load(reader);
            bindings.clear();
            if (data instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) data;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        bindings.put(entry.getKey().toString(), entry.getValue().toString());
                    }
                }
            }
        } catch (IOException e) {
            logger.warning("读取 bindings.yml 失败: " + e.getMessage());
        }
    }

    private void saveBindings() {
        Yaml yaml = new Yaml();
        try (FileWriter writer = new FileWriter(bindingsFile)) {
            yaml.dump(bindings, writer);
        } catch (IOException e) {
            logger.warning("写入 bindings.yml 失败: " + e.getMessage());
        }
    }

    // 检查是否已绑定
    private boolean isAlreadyBound(String qq, String playerName) {
        loadBindings();
        return bindings.containsKey(qq) || bindings.containsValue(playerName);
    }

    // 存储绑定
    private void saveBinding(String qq, String playerName) {
        loadBindings();
        bindings.put(qq, playerName);
        saveBindings();
    }

    // 删除绑定
    private int deleteBinding(String identifier) {
        loadBindings();
        String removedKey = null;
        // 先按 QQ
        if (bindings.containsKey(identifier)) {
            removedKey = identifier;
        } else {
            // 再按玩家名
            for (Map.Entry<String, String> entry : bindings.entrySet()) {
                if (entry.getValue().equals(identifier)) {
                    removedKey = entry.getKey();
                    break;
                }
            }
        }
        if (removedKey != null) {
            bindings.remove(removedKey);
            saveBindings();
            return 1;
        }
        return 0;
    }

    // 根据 QQ 获取玩家名
    public String getNameByQQ(String qq) {
        loadBindings();
        return bindings.getOrDefault(qq, null);
    }

    // 根据玩家名获取 QQ
    public String getQQByName(String playerName) {
        loadBindings();
        for (Map.Entry<String, String> entry : bindings.entrySet()) {
            if (entry.getValue().equals(playerName)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // 获取绑定信息
    public Map<String, String> getBindingInfo(String identifier) {
        loadBindings();
        Map<String, String> result = new HashMap<>();
        if (bindings.containsKey(identifier)) {
            result.put("qq", identifier);
            result.put("playerName", bindings.get(identifier));
            return result;
        }
        for (Map.Entry<String, String> entry : bindings.entrySet()) {
            if (entry.getValue().equals(identifier)) {
                result.put("qq", entry.getKey());
                result.put("playerName", entry.getValue());
                return result;
            }
        }
        return null;
    }


    private String getLanguage(String LanguageKey){
        String lang = this.getConfig().getString("language."+LanguageKey);
        if(lang == null || lang.isEmpty()){
            logger.severe("HuHoWhiteList语言文件配置错误.");
            return "语言文件配置错误.";
        }
        lang.replace("\\n", "\n");
        return lang;
    }

    @EventHandler
    public void onCommandSend(BotCustomCommand event)
    {
        this.reloadConfig();
        String applyKeyWord = this.getConfig().getString("keyWord.apply");
        String deleteKeyWord = this.getConfig().getString("keyWord.delete");

        if(applyKeyWord != null && event.getCommand().equals(applyKeyWord)){ //申请白名单
            event.setCancelled(true);

            String addCommand = this.getConfig().getString("whiteList.add");
            if(addCommand == null){
                logger.severe("配置出错，请修改config.yml后重试：添加白名单指令为空");
                event.respone("配置出错，请修改config.yml后重试：添加白名单指令为空", "error");
                return;
            }

            String playerName = event.getParam().get(0);
            JSONObject data = event.getData();
            String qqNum = null;

            if(data != null && data.containsKey("author")) {
                JSONObject author = data.getJSONObject("author");
                if(author != null && author.containsKey("bindQQ")) {
                    qqNum = author.getString("bindQQ");
                }
            }

            if(qqNum == null || qqNum.isEmpty()) {
                logger.warning("无法获取 QQ 绑定信息");
                event.respone(getLanguage("noBind"), "error");
                return;
            }
            //logger.info(event.getData().toString());

            if(playerName != null && !playerName.isEmpty()){
                //检测该账号是否绑定过
                if(isAlreadyBound(qqNum, playerName)){
                    event.respone(getLanguage("alreadyBind"), "error");
                    return;
                }
                addCommand = addCommand.replace("{name}", event.getParam().get(0));
                String ret = ServerManager.sendCmd(addCommand, true, true);
                event.respone(getLanguage("execute").replace("{ret}",ret), "success");
                saveBinding(qqNum, playerName);
            }else{
                event.respone(getLanguage("noPlayerName"), "error");
            }
        }
        else if(deleteKeyWord != null && event.getCommand().equals(deleteKeyWord)){//取消白名单
            event.setCancelled(true);

            if(!event.isRunByAdmin()){
                event.respone(getLanguage("noAdmin"), "error");
            }

            String deleteCommand = this.getConfig().getString("whiteList.del");
            if(deleteCommand == null){
                logger.severe("配置出错，请修改config.yml后重试：删除白名单指令为空");
                event.respone("配置出错，请修改config.yml后重试：删除白名单指令为空", "error");
                return;
            }

            // 获取参数
            String param = event.getParam().size() > 0 ? event.getParam().get(0) : null;
            if(param == null || param.isEmpty()){
                event.respone(getLanguage("noParam"), "error");
                return;
            }

            // 获取绑定信息
            Map<String, String> binding = getBindingInfo(param);
            if(binding == null){
                event.respone(getLanguage("deleteNotFound").replace("{data}", param), "error");
                return;
            }

            // 执行删除（同时支持QQ或玩家名作为条件）
            int deleted = deleteBinding(param);
            if(deleted > 0){
                // 同步删除服务器白名单（使用玩家名）
                String cmd = deleteCommand.replace("{name}", binding.get("playerName"));
                ServerManager.sendCmd(cmd, true, false);

                // 返回详细信息
                event.respone(getLanguage("deleteSuccess")
                        .replace("{player}", binding.get("playerName"))
                        .replace("{qq}", binding.get("qq")), "success");
            }else{
                event.respone(getLanguage("deleteFail"), "error");
            }
        }
    }

}
