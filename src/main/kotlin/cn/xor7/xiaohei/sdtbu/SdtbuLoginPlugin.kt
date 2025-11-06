package cn.xor7.xiaohei.sdtbu

import cn.xor7.xiaohei.sdtbu.utils.initDb
import io.papermc.paper.network.ChannelInitializeListenerHolder
import org.bukkit.plugin.java.JavaPlugin

lateinit var plugin: SdtbuLoginPlugin

class SdtbuLoginPlugin : JavaPlugin() {
    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        initDb()
        addChannelListener()
    }

    override fun onDisable() {
        removeChannelListener()
    }

    private fun addChannelListener() {
        ChannelInitializeListenerHolder.addListener(LoginChannelHandler.listenerKey) { channel ->
            channel.pipeline().addBefore(
                LoginChannelHandler.BASE_HANDLER_NAME,
                LoginChannelHandler.HANDLER_NAME,
                LoginChannelHandler(channel),
            )
        }
    }

    private fun removeChannelListener() {
        ChannelInitializeListenerHolder.removeListener(LoginChannelHandler.listenerKey)
    }
}
