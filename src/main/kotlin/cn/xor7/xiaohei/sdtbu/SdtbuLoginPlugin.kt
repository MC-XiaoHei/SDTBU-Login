package cn.xor7.xiaohei.sdtbu

import io.papermc.paper.network.ChannelInitializeListenerHolder
import net.minecraft.network.Connection
import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
object SdtbuLoginPlugin : JavaPlugin() {
    override fun onEnable() {
        ChannelInitializeListenerHolder.addListener(LoginChannelHandler.listenerKey) { channel ->
            val connection = channel.pipeline().get(LoginChannelHandler.BASE_HANDLER_NAME) as? Connection
                ?: throw IllegalStateException("Connection handler not found in pipeline")
            channel.pipeline().addBefore(
                LoginChannelHandler.BASE_HANDLER_NAME,
                LoginChannelHandler.HANDLER_NAME,
                LoginChannelHandler(connection),
            )
        }
    }

    override fun onDisable() {
        ChannelInitializeListenerHolder.removeListener(LoginChannelHandler.listenerKey);
    }
}
