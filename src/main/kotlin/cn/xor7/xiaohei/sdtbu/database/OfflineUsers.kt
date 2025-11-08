package cn.xor7.xiaohei.sdtbu.database

import cn.xor7.xiaohei.sdtbu.utils.StudentId
import cn.xor7.xiaohei.sdtbu.utils.hash
import cn.xor7.xiaohei.sdtbu.utils.verify
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

object OfflineUsers : UUIDTable("offline_users") {
    val username = varchar("username", 32)
    val passwordHash = varchar("password_hash", 255)
    val studentId = varchar("student_id", 32)
}

private val registered = mutableSetOf<UUID>()

fun initRegisteredOfflineUserCache() = transaction {
    OfflineUsers.selectAll().forEach {
        registered.add(it[OfflineUsers.id].value)
    }
}

fun UUID.isRegisteredOffline(): Boolean = registered.contains(this)

fun registerOffline(
    uuid: UUID,
    username: String,
    password: String,
    studentId: String,
) = transaction {
    val user = OfflineUsers.insert {
        it[OfflineUsers.id] = uuid
        it[OfflineUsers.username] = username
        it[OfflineUsers.passwordHash] = hash(password)
        it[OfflineUsers.studentId] = studentId
    }
    registered.add(uuid)
    StudentId(user[OfflineUsers.studentId])
}

fun loginOffline(
    uuid: UUID,
    username: String,
    password: String,
): StudentId? = transaction {
    val user = OfflineUsers
        .selectAll()
        .where { (OfflineUsers.id eq uuid) and (OfflineUsers.username eq username) }
        .firstOrNull()
        ?: return@transaction null

    val storedHash = user[OfflineUsers.passwordHash]
    if (verify(password, storedHash)) {
        StudentId(user[OfflineUsers.studentId])
    } else {
        null
    }
}