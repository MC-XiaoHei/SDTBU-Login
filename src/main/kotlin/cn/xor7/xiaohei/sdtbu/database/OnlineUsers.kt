package cn.xor7.xiaohei.sdtbu.database

import cn.xor7.xiaohei.sdtbu.utils.StudentId
import net.minecraft.network.Connection
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

object OnlineUsers : UUIDTable("online_users") {
    val studentId = varchar("student_id", 32)
}

private val registered = mutableSetOf<UUID>()

fun initRegisteredOnlineUserCache() = transaction {
    OnlineUsers.selectAll().forEach {
        registered.add(it[OnlineUsers.id].value)
    }
}

fun Connection.isOnline(): Boolean = hostname.startsWith("online")

fun UUID.isRegisteredOnline(): Boolean = registered.contains(this)

fun registerOnline(
    uuid: UUID,
    studentId: String,
) = transaction {
    val user = OnlineUsers.insert {
        it[OnlineUsers.id] = uuid
        it[OnlineUsers.studentId] = studentId
    }
    registered.add(uuid)
    StudentId(user[OnlineUsers.studentId])
}