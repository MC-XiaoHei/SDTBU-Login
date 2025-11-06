package cn.xor7.xiaohei.sdtbu.utils

import cn.xor7.xiaohei.sdtbu.SdtbuLoginPlugin
import org.bukkit.Bukkit

fun runTaskLater(delayTicks: Long, task: () -> Unit) {
    Bukkit.getServer().scheduler.runTaskLater(SdtbuLoginPlugin, task, delayTicks)
}