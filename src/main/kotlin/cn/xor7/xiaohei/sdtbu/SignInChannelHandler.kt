@file:Suppress("SpellCheckingInspection")

package cn.xor7.xiaohei.sdtbu

import cn.xor7.xiaohei.sdtbu.database.*
import cn.xor7.xiaohei.sdtbu.dialogs.*
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
import kotlin.jvm.optionals.getOrNull

class SignInChannelHandler(private val channel: Channel) : ChannelDuplexHandler() {
    private lateinit var name: String
    private lateinit var uuid: UUID
    private val connection = channel.pipeline().get(BASE_HANDLER_NAME) as? Connection
        ?: throw IllegalStateException("Connection handler not found in pipeline")

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

        if (connection.isOnline() && uuid.isRegisteredOnline()) {
            ctx.passSignIn()
            return
        }
        val dialog = if (connection.isOnline()) {
            println("[SDTBU-Login] Online registration for user $name ($uuid)")
            buildOnlineRegisterDialog()
        } else if (uuid.isRegisteredOffline()) {
            println("[SDTBU-Login] Offline login for user $name ($uuid)")
            buildLoginDialog()
        } else {
            println("[SDTBU-Login] Offline registration for user $name ($uuid)")
            buildOfflineRegisterDialog()
        }
        ctx.showDialog(dialog)
        runTaskLater(20 * 60) {
            ctx.kick(loginTimeout)
        }
    }

    override fun channelRead(ctx: ChannelHandlerContext, packet: Any) {
        val continueProcess = when (packet) {
            is ServerboundHelloPacket -> processServerboundHelloPacket(packet)
            is ServerboundCustomClickActionPacket -> processServerboundCustomClickActionPacket(ctx, packet)
            is ServerboundFinishConfigurationPacket -> false
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
    ): Boolean = when (packet.id) {
        loginPacketId if packet.payload.isPresent -> processLoginPacket(ctx, packet)
        offlineRegisterPacketId if packet.payload.isPresent -> processOfflineRegisterPacket(ctx, packet)
        onlineRegisterPacketId if packet.payload.isPresent -> processOnlineRegisterPacket(ctx, packet)
        loginCancelPacketId -> processLoginCancelPacket(ctx)
        registerCancelPacketId -> processRegisterCancelPacket(ctx)
        else -> true
    }

    private fun processLoginPacket(
        ctx: ChannelHandlerContext,
        packet: ServerboundCustomClickActionPacket,
    ): Boolean {
        val tag = packet.payload.get() as? CompoundTag ?: run {
            ctx.kick(internalError)
            return false
        }
        val password = tag.getString(LOGIN_PASSWORD_INPUT_ID).getOrNull() ?: run {
            ctx.kick(emptyPassword)
            return false
        }
        val studentId = loginOffline(uuid, name, password)
        if (studentId != null) {
            ctx.passSignIn()
        } else {
            ctx.kick(wrongPassword)
        }
        return true
    }

    private fun processOfflineRegisterPacket(
        ctx: ChannelHandlerContext,
        packet: ServerboundCustomClickActionPacket,
    ): Boolean {
        val tag = packet.payload.get() as? CompoundTag ?: run {
            ctx.kick(internalError)
            return false
        }
        val password = tag.getString(OFFLINE_REGISTER_PASSWORD_INPUT_ID).getOrNull()
        val repeatPassword = tag.getString(OFFLINE_REGISTER_REPEAT_PASSWORD_INPUT_ID).getOrNull()
        if (password != repeatPassword) {
            ctx.kick(passwordMismatch)
            return false
        }
        if (password == null) {
            ctx.kick(emptyPassword)
            return false
        }
        val studentId = tag.getString(OFFLINE_REGISTER_STUDENT_ID_INPUT_ID).getOrNull() ?: run {
            ctx.kick(studentIdRequired)
            return false
        }
        if (!isValidStudentId(studentId)) {
            ctx.kick(invalidStudentId)
            return false
        }
        registerOffline(uuid, name, password, studentId)
        ctx.passSignIn()
        return true
    }

    private fun processOnlineRegisterPacket(
        ctx: ChannelHandlerContext,
        packet: ServerboundCustomClickActionPacket,
    ): Boolean {
        val tag = packet.payload.get() as? CompoundTag ?: run {
            ctx.kick(internalError)
            return false
        }
        val studentId = tag.getString(ONLINE_REGISTER_STUDENT_ID_INPUT_ID).getOrNull() ?: run {
            ctx.kick(studentIdRequired)
            return false
        }
        if (!isValidStudentId(studentId)) {
            ctx.kick(invalidStudentId)
            return false
        }
        registerOnline(uuid, studentId)
        ctx.passSignIn()
        return true
    }

    private fun processLoginCancelPacket(ctx: ChannelHandlerContext): Boolean {
        ctx.kick(loginCanceled)
        return false
    }

    private fun processRegisterCancelPacket(ctx: ChannelHandlerContext): Boolean {
        ctx.kick(registerCanceled)
        return false
    }

    private fun isValidStudentId(studentId: String): Boolean {
        return studentId.length == 10 && studentId.all { it.isDigit() }
    }

    private fun ChannelHandlerContext.passSignIn() {
        val packetListener = connection.getPacketListener() ?: run {
            kick(internalError)
            return
        }
        serverPacketListenerClosedField.set(packetListener, true)
        writeAndFlush(ClientboundFinishConfigurationPacket.INSTANCE)
        channel.pipeline().remove(HANDLER_NAME)
    }

    companion object {
        const val HANDLER_NAME = "signin_channel_handler"
        const val BASE_HANDLER_NAME = "packet_handler"
        val listenerKey = Key.key(pluginNamespace, HANDLER_NAME)
        private val serverPacketListenerClosedField = accessField<Boolean>(
            ServerCommonPacketListenerImpl::class,
            "closed",
        )
    }
}