package fr.imacaron.gif.api.plugins

import com.mchange.v2.c3p0.ComboPooledDataSource
import io.ktor.server.application.*
import org.ktorm.database.Database
import org.ktorm.support.mysql.MySqlDialect

fun Application.configureDatabase(): Database {
	val cpds = ComboPooledDataSource().apply {
		driverClass = "org.mariadb.jdbc.Driver"
		jdbcUrl = environment.config.property("db.url").getString()
		user = environment.config.property("db.user").getString()
		password = environment.config.property("db.password").getString()
		minPoolSize = 5
		acquireIncrement = 5
		maxPoolSize = 10
	}

	return Database.connect(cpds, dialect = MySqlDialect())
}