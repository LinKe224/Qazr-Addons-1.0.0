# Qazr Addons 1.0.0

这是 `Qazr-Addons-1.0.0` 的 Recaf 反编译主类源码。

## 基本信息

- Minecraft: 1.21.11
- Java: 21
- Mod Loader: Fabric
- Client: Meteor Client
- 原始入口: `com.macekill.addon.MaceKillAddon`
- 原始 JAR SHA-256: `31A0273A080B1859B131457ADCAD3274C506D427588C6141F8C4D09C0CCB8A77`

## 包含模块

- AutoFuck
- AutoGG
- AutoMine
- CreativeGive
- CustomPotion
- MaceDMG
- MaceKill

## 说明

仓库保留了 9 个顶层 Java 文件，内部枚举和内部类位于对应主文件中。

这些文件来自字节码反编译，不是原作者的原始工程。Minecraft 引用仍使用 Fabric intermediary 名称，部分泛型信息也可能在编译过程中丢失。仓库未包含 Gradle 配置、Minecraft/Meteor 依赖和映射配置，因此源码需要进一步整理后才能重新编译。
