@file:Suppress("SpellCheckingInspection")

package cn.xor7.xiaohei.sdtbu

import cn.xor7.xiaohei.sdtbu.database.login
import cn.xor7.xiaohei.sdtbu.dialogs.LOGIN_PASSWORD_INPUT_ID
import cn.xor7.xiaohei.sdtbu.dialogs.buildDialogPacket
import cn.xor7.xiaohei.sdtbu.utils.*
import io.netty.channel.Channel
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.kyori.adventure.key.Key
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket
import net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket
import net.minecraft.network.protocol.configuration.ServerboundFinishConfigurationPacket
import net.minecraft.network.protocol.login.ServerboundHelloPacket
import net.minecraft.server.network.ServerCommonPacketListenerImpl
import java.util.*

class LoginChannelHandler(private val channel: Channel) : ChannelDuplexHandler() {
    private lateinit var name: String
    private lateinit var uuid: UUID
    private val connection = channel.pipeline().get(BASE_HANDLER_NAME) as? Connection
        ?: throw IllegalStateException("Connection handler not found in pipeline")

    @Volatile
    private var loginSucceeded: Boolean = false

    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
        if (msg !is ClientboundFinishConfigurationPacket) {
            super.write(ctx, msg, promise)
            return
        }
        val packetListener = connection.getPacketListener() ?: run {
            ctx.kick(internalError)
            return
        }
        serverPacketListenerClosedField.set(packetListener, false)
        ctx.writeAndFlush(buildDialogPacket())
        runTaskLater(600) {
            ctx.kick(loginTimeout)
        }
    }

    override fun channelRead(ctx: ChannelHandlerContext, packet: Any) {
        val continueProcess = when (packet) {
            is ServerboundHelloPacket -> processServerboundHelloPacket(packet)
            is ServerboundCustomClickActionPacket -> processServerboundCustomClickActionPacket(ctx, packet)
            is ServerboundFinishConfigurationPacket -> loginSucceeded

            else -> true
        }
        if (continueProcess) super.channelRead(ctx, packet)
    }

    private fun processServerboundHelloPacket(packet: ServerboundHelloPacket): Boolean {
        this.name = packet.name
        this.uuid = packet.profileId
        return true
    }

    private fun processServerboundCustomClickActionPacket(
        ctx: ChannelHandlerContext,
        packet: ServerboundCustomClickActionPacket,
    ): Boolean {
        when (packet.id) {
            loginPacketId if packet.payload.isPresent -> {
                val tag = packet.payload.get() as? CompoundTag ?: run {
                    ctx.kick(internalError)
                    return false
                }
                val password = tag.getStringOr(LOGIN_PASSWORD_INPUT_ID, "")
                val studentId = login(uuid, name, password)
                if (studentId != null) {
                    ctx.writeAndFlush(ClientboundFinishConfigurationPacket.INSTANCE)
                    loginSucceeded = true
                    channel.pipeline().remove(HANDLER_NAME)
                } else {
                    ctx.kick(wrongPassword)
                }
                return true
            }

            cancelPacketId -> {
                ctx.kick(loginCanceled)
                return false
            }

            else -> {
                return true
            }
        }
    }

    companion object {
        const val HANDLER_NAME = "login_channel_handler"
        const val BASE_HANDLER_NAME = "packet_handler"
        val listenerKey = Key.key(pluginNamespace, HANDLER_NAME)
        private val serverPacketListenerClosedField = accessField<Boolean>(
            ServerCommonPacketListenerImpl::class,
            "closed",
        )
    }
}