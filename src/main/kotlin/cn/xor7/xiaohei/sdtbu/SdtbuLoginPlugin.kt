package cn.xor7.xiaohei.sdtbu

import cn.xor7.xiaohei.sdtbu.database.initRegisteredOfflineUserCache
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
        initRegisteredOfflineUserCache()
        addChannelListener()
    }

    override fun onDisable() {
        removeChannelListener()
    }

    private fun addChannelListener() {
        ChannelInitializeListenerHolder.addListener(SignInChannelHandler.listenerKey) { channel ->
            channel.pipeline().addBefore(
                SignInChannelHandler.BASE_HANDLER_NAME,
                SignInChannelHandler.HANDLER_NAME,
                SignInChannelHandler(channel),
            )
        }
    }

    private fun removeChannelListener() {
        ChannelInitializeListenerHolder.removeListener(SignInChannelHandler.listenerKey)
    }
}
