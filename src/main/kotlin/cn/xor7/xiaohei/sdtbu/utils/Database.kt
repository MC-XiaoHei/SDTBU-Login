package cn.xor7.xiaohei.sdtbu.utils

import cn.xor7.xiaohei.sdtbu.database.OfflineUsers
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File

fun JavaPlugin.initDb() {
    val dbFile = getDbFile().also(File::ensureExists)
    val config = buildHikariConfig(dbFile.absolutePath)
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)
    initSchema()
}

private fun JavaPlugin.getDbFile() = dataFolder.resolve("data.db")

private fun File.ensureExists() {
    if (exists()) return
    parentFile?.mkdirs()
    createNewFile()
}

private fun buildHikariConfig(dbPath: String) = HikariConfig().apply {
    driverClassName = "org.sqlite.JDBC"
    jdbcUrl = "jdbc:sqlite:$dbPath?foreign_keys=on"
    maximumPoolSize = 3
    isAutoCommit = false
    transactionIsolation = "TRANSACTION_SERIALIZABLE"
}

private fun initSchema() = transaction {
    SchemaUtils.create(
        OfflineUsers,
    )
}