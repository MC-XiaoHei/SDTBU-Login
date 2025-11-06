package cn.xor7.xiaohei.sdtbu

import cn.xor7.xiaohei.sdtbu.database.initRegisteredUserCache
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
        initRegisteredUserCache()
        addChannelListener()
    }

    override fun onDisable() {
        removeChannelListener()
    }

    private fun addChannelListener() {
        ChannelInitializeListenerHolder.addListener(SigninChannelHandler.listenerKey) { channel ->
            channel.pipeline().addBefore(
                SigninChannelHandler.BASE_HANDLER_NAME,
                SigninChannelHandler.HANDLER_NAME,
                SigninChannelHandler(channel),
            )
        }
    }

    private fun removeChannelListener() {
        ChannelInitializeListenerHolder.removeListener(SigninChannelHandler.listenerKey)
    }
}
