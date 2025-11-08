package cn.xor7.xiaohei.sdtbu.dialogs

import cn.xor7.xiaohei.sdtbu.utils.offlineRegisterPacketId
import net.minecraft.network.chat.Component.literal
import net.minecraft.server.dialog.*
import net.minecraft.server.dialog.action.CustomAll
import net.minecraft.server.dialog.body.DialogBody
import net.minecraft.server.dialog.input.TextInput
import java.util.Optional.empty
import java.util.Optional.of

const val OFFLINE_REGISTER_PASSWORD_INPUT_ID = "offline_register_password"
const val OFFLINE_REGISTER_REPEAT_PASSWORD_INPUT_ID = "offline_register_repeat_password"
const val OFFLINE_REGISTER_STUDENT_ID_INPUT_ID = "offline_register_student_id"

fun buildOfflineRegisterDialog() = ConfirmationDialog(
    CommonDialogData(
        literal("注册"),
        empty(),
        false,
        true,
        DialogAction.CLOSE,
        mutableListOf<DialogBody>(),
        listOf(
            Input(
                OFFLINE_REGISTER_PASSWORD_INPUT_ID,
                TextInput(200, literal("密码"), true, "", 50, empty()),
            ),
            Input(
                OFFLINE_REGISTER_REPEAT_PASSWORD_INPUT_ID,
                TextInput(200, literal("重复密码"), true, "", 50, empty()),
            ),
            Input(
                OFFLINE_REGISTER_STUDENT_ID_INPUT_ID,
                TextInput(200, literal("学号"), true, "", 10, empty()),
            ),
        ),
    ),
    ActionButton(
        CommonButtonData(literal("注册"), empty(), 150),
        of(CustomAll(offlineRegisterPacketId, empty())),
    ),
    ActionButton(
        CommonButtonData(literal("退出"), empty(), 150),
        of(CustomAll(offlineRegisterPacketId, empty())),
    ),
)