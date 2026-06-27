# OPlus Auto DC

## 说明

一加 15 存在 `经典低频闪（高频 PWM）` `全亮度低频闪（类 DC）` 两种调光模式  
类 DC 调光只支持 1-120Hz 刷新率，故游戏等 144-165Hz 场景会自动切回高频 PWM 调光

但是系统在恢复到 1-120Hz 刷新率后不会自动切换回类 DC 调光，故本模块来补全此逻辑

## 功能

144Hz 及以上场景交给系统自己切到高频 PWM 调光；退出高刷、回到 120Hz 及以下后，如果系统没有恢复类 DC 调光，模块再自动切换回类 DC 调光。

## 原理

开机时：创建一个 `app_process` 进程，利用 `IDisplayManager` 调用系统 `DisplayManagerService` 注册刷新率变化回调  
当刷新率把变化时：判断是不是 1-120Hz 场景，和检查当前调光状态，满足条件就写入安全设置 `display_single_pulse_eyeprotection_switch` 项，切换到类 DC 调光

## 下载

- [GitHub Releases](https://github.com/yangFenTuoZi/OPlus-AutoDC-Module/releases)

## 鸣谢

方案来源于 [二词元Token@酷安](https://www.coolapk.com/u/34193857) 的 [欧加真智能调光](https://www.coolapk.com/feed/72183531?s=ZWY3MjRmOWNlNTZiMmFnNmEzZmFkZGN6a1632) 模块
