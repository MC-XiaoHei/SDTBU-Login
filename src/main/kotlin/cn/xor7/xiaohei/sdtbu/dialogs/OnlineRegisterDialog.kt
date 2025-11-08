package cn.xor7.xiaohei.sdtbu.dialogs

import cn.xor7.xiaohei.sdtbu.utils.onlineRegisterPacketId
import cn.xor7.xiaohei.sdtbu.utils.registerCancelPacketId
import net.minecraft.network.chat.Component.literal
import net.minecraft.server.dialog.*
import net.minecraft.server.dialog.action.CustomAll
import net.minecraft.server.dialog.body.DialogBody
import net.minecraft.server.dialog.input.TextInput
import java.util.Optional.empty
import java.util.Optional.of

const val ONLINE_REGISTER_STUDENT_ID_INPUT_ID = "online_register_student_id_input"

fun buildOnlineRegisterDialog() = ConfirmationDialog(
    CommonDialogData(
        literal("注册"),
        empty(),
        false,
        true,
        DialogAction.CLOSE,
        mutableListOf<DialogBody>(),
        listOf(
            Input(
                ONLINE_REGISTER_STUDENT_ID_INPUT_ID,
                TextInput(200, literal("学号"), true, "", 10, empty()),
            ),
        ),
    ),
    ActionButton(
        CommonButtonData(literal("注册"), empty(), 150),
        of(CustomAll(onlineRegisterPacketId, empty())),
    ),
    ActionButton(
        CommonButtonData(literal("退出"), empty(), 150),
        of(CustomAll(registerCancelPacketId, empty())),
    ),
)