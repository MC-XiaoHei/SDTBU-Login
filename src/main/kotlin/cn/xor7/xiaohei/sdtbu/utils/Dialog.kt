package cn.xor7.xiaohei.sdtbu.utils

import io.netty.channel.ChannelHandlerContext
import net.minecraft.core.Holder
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket
import net.minecraft.server.dialog.Dialog

fun ChannelHandlerContext.showDialog(dialog: Dialog) {
    this.writeAndFlush(ClientboundShowDialogPacket(Holder.direct(dialog)))
}