package cn.xor7.xiaohei.sdtbu.utils

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

private val encoder: PasswordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

fun hash(rawPassword: String): String = encoder.encode(rawPassword)

fun verify(rawPassword: String, encodedPassword: String): Boolean {
    if (encodedPassword.isBlank()) return false

    return try {
        encoder.matches(rawPassword, encodedPassword)
    } catch (_: Exception) {
        false
    }
}