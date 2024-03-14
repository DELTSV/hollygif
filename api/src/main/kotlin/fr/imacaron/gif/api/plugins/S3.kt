package fr.imacaron.gif.api.plugins

import fr.imacaron.gif.shared.infrastrucutre.S3File
import io.ktor.server.application.*

fun Application.buildS3(series: String): S3File = S3File(
		environment.config.property("s3.access").getString(),
		environment.config.property("s3.secret").getString(),
		environment.config.property("s3.url").getString(),
		environment.config.property("s3.region").getString(),
		series
	)