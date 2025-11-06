package cn.xor7.xiaohei.sdtbu

import io.papermc.paper.network.ChannelInitializeListenerHolder
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: SdtbuLoginPlugin

class SdtbuLoginPlugin : JavaPlugin() {
    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        ChannelInitializeListenerHolder.addListener(LoginChannelHandler.listenerKey) { channel ->

            channel.pipeline().addBefore(
                LoginChannelHandler.BASE_HANDLER_NAME,
                LoginChannelHandler.HANDLER_NAME,
                LoginChannelHandler(channel),
            )
        }
    }

    override fun onDisable() {
        ChannelInitializeListenerHolder.removeListener(LoginChannelHandler.listenerKey)
    }
}
