package cn.xor7.xiaohei.sdtbu.dialogs

import cn.xor7.xiaohei.sdtbu.utils.cancelPacketId
import cn.xor7.xiaohei.sdtbu.utils.loginPacketId
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component.literal
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket
import net.minecraft.server.dialog.*
import net.minecraft.server.dialog.action.CustomAll
import net.minecraft.server.dialog.body.DialogBody
import net.minecraft.server.dialog.input.TextInput
import java.util.Optional.empty
import java.util.Optional.of

fun buildDialogPacket() = ClientboundShowDialogPacket(Holder.direct(buildLoginDialog()))

private fun buildLoginDialog() = ConfirmationDialog(
    CommonDialogData(
        literal("登录"),
        empty(),
        false,
        true,
        DialogAction.CLOSE,
        mutableListOf<DialogBody>(),
        listOf(
            Input(
                "password",
                TextInput(200, literal("密码"), true, "", 200, empty()),
            ),
        ),
    ),
    ActionButton(
        CommonButtonData(literal("登录"), empty(), 150),
        of(CustomAll(loginPacketId, empty())),
    ),
    ActionButton(
        CommonButtonData(literal("退出"), empty(), 150),
        of(CustomAll(cancelPacketId, empty())),
    ),
)