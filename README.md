# HuHoWhiteList

[![GitHub Release](https://img.shields.io/github/v/release/HuHoBot/HuHoMonitor?style=for-the-badge)](https://github.com/HuHoBot/HuHoWhiteList/releases)
[![License](https://img.shields.io/github/license/HuHoBot/HuHoMonitor?style=for-the-badge)](https://github.com/HuHoBot/HuHoWhiteList/blob/main/LICENSE)
[![Build Status](https://img.shields.io/github/actions/workflow/status/HuHoBot/HuHoMonitor/release.yml?style=for-the-badge)](https://github.com/HuHoBot/HuHoWhiteList/actions)

## HuHoBot 拓展插件
HuHoBot 拓展插件，用于群内绑定QQ后可自助申请白名单

##  使用方法
1. 安装插件
    - 下载插件并上传至服务器的plugins文件夹内。
2. 使用`/认证 <qq号>`进行绑定QQ
3. 在群内发出`/申请白名单 白名单`来申请白名单（可修改config内的keyWord更改关键词）

## 配置文件
```yaml
keyWord:
   apply: "申请白名单"
   delete: "取消白名单"

whiteList:
   add: "whitelist add {name}"
   del: "whitelist remove {name}"

language:
   noBind: "未查询到您的绑定QQ信息，请使用/认证 <qq>来进行绑定QQ"
   noPlayerName: "玩家名有误，请重试"
   alreadyBind: "您已经绑定过该账号"
   execute: "提交绑定成功,返回值如下:\n{ret}"
   deleteNotFound: "未找到{data}的有效信息"
   deleteSuccess: "已删除玩家 {player} (QQ: {qq}) 的白名单"
   deleteFail: "删除操作失败，请检查日志"
   noParam:  "缺少参数"
   noAdmin: "您不是管理员"
```