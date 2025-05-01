# HuHoMonitor

[![GitHub Release](https://img.shields.io/github/v/release/HuHoBot/HuHoMonitor?style=for-the-badge)](https://github.com/HuHoBot/HuHoMonitor/releases)
[![License](https://img.shields.io/github/license/HuHoBot/HuHoMonitor?style=for-the-badge)](https://github.com/HuHoBot/HuHoMonitor/blob/main/LICENSE)
[![Build Status](https://img.shields.io/github/actions/workflow/status/HuHoBot/HuHoMonitor/release.yml?style=for-the-badge)](https://github.com/HuHoBot/HuHoMonitor/actions)

## HuHoBot 拓展插件
HuHoBot 拓展插件，用于向群内发送当前服务器状态和使用部分PlaceHolder变量。

##  使用方法
1. 安装插件
    - 下载插件并上传至服务器的plugins文件夹内。
2. 在群内发出`/执行 服务器状态`来查看当前服务器状态

## 配置文件
```yaml
#  格式化字符串
#  支持变量:
#  {CpuUsage}   CPU使用率
#  {MemoryUsage}   内存使用率
#  {DiskUsage}   磁盘使用率
formatString: "🖥 CPU使用率: {CpuUsage}\n💾 内存使用: {MemoryUsage}\n💽 磁盘空间: {DiskUsage}"
#若要支持Placeholder,请先安装PlaceHolder API并在上方的formatString中填入对应的PlaceHolder变量
```