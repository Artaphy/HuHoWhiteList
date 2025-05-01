package cn.huohuas001.huhomonitor;

import cn.huohuas001.huHoBot.HuHoBot;
import me.clip.placeholderapi.PlaceholderAPI;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import java.text.DecimalFormat;
import java.util.stream.Collectors;

public class SystemMonitor {
    private static final SystemInfo SYSTEM_INFO = new SystemInfo();
    private static final CentralProcessor PROCESSOR = SYSTEM_INFO.getHardware().getProcessor();
    private static long[] prevTicks = PROCESSOR.getSystemCpuLoadTicks();
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#0.0%");

    public static String setPlaceholder(String oriText) {
        if (!HuHoMonitor.getPlugin().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return oriText;
        }
        return PlaceholderAPI.setPlaceholders(null, oriText);
    }

    public static String getSystemStatus(String formatString) {
        String cpu = getCpuUsage().isEmpty() ? "N/A" : getCpuUsage();
        String memory = getMemoryUsage().isEmpty() ? "N/A" : getMemoryUsage();
        String disk = getDiskUsage().isEmpty() ? "N/A" : getDiskUsage();
        String formatedString = formatString.replace("\\n","\n")
                        .replace("{CpuUsage}", cpu)
                        .replace("{MemoryUsage}", memory)
                        .replace("{DiskUsage}", disk);
        return setPlaceholder(formatedString);
    }

    @Deprecated
    public static String getSystemStatus(){
        String defaultString = "🖥 CPU使用率: {CpuUsage}\n💾 内存使用: {MemoryUsage}\n💽 磁盘空间: {DiskUsage}";
        return getSystemStatus(defaultString);
    }

    private static String getCpuUsage() {// 创建SystemInfo实例
        SystemInfo si = new SystemInfo();
        // 获取硬件抽象层
        HardwareAbstractionLayer hal = si.getHardware();// 获取中央处理器
        CentralProcessor processor = hal.getProcessor();// 获取 CPU 使用率（单位是百分比）
        double cpuLoad = processor.getSystemCpuLoad(1000) * 100; // 获取系统CPU负载并转化为百分比
        // 输出CPU使用率
        return String.format("%.2f%%", cpuLoad);
    }


    private static String getMemoryUsage() {
        GlobalMemory memory = SYSTEM_INFO.getHardware().getMemory();
        long used = memory.getTotal() - memory.getAvailable();
        return formatBytes(used) + "/" + formatBytes(memory.getTotal());
    }

    private static String getDiskUsage() {
        String os = System.getProperty("os.name").toLowerCase();
        String result = SYSTEM_INFO.getOperatingSystem().getFileSystem().getFileStores().stream()
            .filter(fs -> {
                // 类型排除
                String type = fs.getType().toLowerCase();
                if (type.contains("network") || type.contains("virtual")) return false;

                // 路径匹配
                if (os.contains("win")) {
                    return fs.getMount().matches("^[A-Za-z]:\\\\$"); // 精确匹配 C:\
                }
                return fs.getMount().equals("/");
            })
            .map(fs -> {
                long used = fs.getTotalSpace() - fs.getFreeSpace();
                String mountPoint = os.contains("win") ?
                    fs.getMount().replace("\\", "") : fs.getMount();
                return mountPoint + ": "
                     + formatBytes(used) + "/" + formatBytes(fs.getTotalSpace())
                     + " (" + PERCENT_FORMAT.format(used * 1.0 / fs.getTotalSpace()) + ")";
            })
            .collect(Collectors.joining("\n            "));


        return result.isEmpty() ? "N/A" : result; // 直接处理空值
    }

    private static long sumTicks(long[] ticks) {
        long sum = 0;
        for (long tick : ticks) sum += tick;
        return sum;
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int unitIndex = (int) (Math.log(bytes) / Math.log(1024));
        String unit = "KMGTPE".charAt(unitIndex - 1) + "B";
        return String.format("%.1f %s", bytes / Math.pow(1024, unitIndex), unit);
    }
}

