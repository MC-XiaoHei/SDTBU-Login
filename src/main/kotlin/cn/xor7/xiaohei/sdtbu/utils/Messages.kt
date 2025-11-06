package cn.xor7.xiaohei.sdtbu.utils

import io.netty.channel.ChannelHandlerContext
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Component.literal
import net.minecraft.network.protocol.common.ClientboundClearDialogPacket
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket

val internalError: Component = literal("内部错误")
val loginCanceled: Component = literal("取消登录")
val loginTimeout: Component = literal("登录超时")
val wrongPassword: Component = literal("密码错误")

fun ChannelHandlerContext.kick(message: Component) {
    writeAndFlush(ClientboundClearDialogPacket.INSTANCE)
    runTaskLater(1) {
        writeAndFlush(ClientboundDisconnectPacket(message))
        disconnect()
    }
}