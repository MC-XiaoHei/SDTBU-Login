package cn.xor7.xiaohei.sdtbu.database

import cn.xor7.xiaohei.sdtbu.utils.hash
import cn.xor7.xiaohei.sdtbu.utils.verify
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*

@JvmInline
value class StudentId(val value: String)

object Users : UUIDTable("users") {
    val username = varchar("username", 32)
    val passwordHash = varchar("password_hash", 255)
    val studentId = varchar("student_id", 32)
}

private val registered = mutableSetOf<UUID>()

fun initRegisteredUserCache() = transaction {
    Users.selectAll().forEach {
        registered.add(it[Users.id].value)
    }
}

fun UUID.isRegistered(): Boolean = registered.contains(this)

fun register(
    uuid: UUID,
    username: String,
    password: String,
    studentId: String,
) = transaction {
    Users.insert {
        it[Users.id] = uuid
        it[Users.username] = username
        it[Users.passwordHash] = hash(password)
        it[Users.studentId] = studentId
    }
    registered.add(uuid)
}

fun login(
    uuid: UUID,
    username: String,
    password: String,
): StudentId? = transaction {
    val user = Users
        .selectAll()
        .where { (Users.id eq uuid) and (Users.username eq username) }
        .firstOrNull()
        ?: return@transaction null

    val storedHash = user[Users.passwordHash]
    if (verify(password, storedHash)) {
        StudentId(user[Users.studentId])
    } else {
        null
    }
}