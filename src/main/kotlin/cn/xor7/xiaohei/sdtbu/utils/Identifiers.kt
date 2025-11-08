package cn.xor7.xiaohei.sdtbu.utils

import net.minecraft.resources.ResourceLocation

const val pluginNamespace = "sdtbu_login"
val loginPacketId = Identifier.of("login")
val offlineRegisterPacketId = Identifier.of("offline_register")
val onlineRegisterPacketId = Identifier.of("online_register")
val cancelPacketId = Identifier.of("cancel")

object Identifier {
    fun of(name: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(pluginNamespace, name)
    }
}