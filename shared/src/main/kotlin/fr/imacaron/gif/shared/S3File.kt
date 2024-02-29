package fr.imacaron.gif.shared

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.http.ContentStreamProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.ListObjectsRequest
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.InputStream
import java.net.URI
import kotlin.jvm.Throws

/**
 * @author TURBIEZ Denis
 * @constructor Construct a [S3File] with some needed parameters
 * @param accessKey The S3 access key
 * @param secretKey The S3 secret key
 * @param url The S3 base url, you can use `<region>` to use a placeholder for the region
 * @param region The S3 region. If the placeholder `<region>` is used in url, this value will replace the placeholder
 * @param bucket The S3 Bucket name
 */
class S3File(accessKey: String, secretKey: String, url: String, region: String, private val bucket: String) {
    private val s3: S3Client = S3Client.builder()
        .endpointOverride(URI(url.replace("<region>", region)))
        .credentialsProvider { AwsBasicCredentials.create(accessKey, secretKey) }
        .region(Region.of(region))
        .build()

    /**
     * Get a file from the S3 instance
     * @author TURBIEZ Denis
     * @param directory The prefix (directory) to search in
     * @param key The file name to get
     * @return The file in form of [ByteArray]
     * @throws NoSuchKeyException When the given [directory]/[key] is not found in the bucket
     */
    @Throws(NoSuchKeyException::class)
    fun getFile(directory: String, key: String): ByteArray =
        s3.getObject(
            GetObjectRequest.builder()
                .bucket(bucket)
                .key("$directory/$key")
                .build()
        ).readAllBytes()

    /**
     * Put a file on the S3 instance
     * @author TURBIEZ Denis
     * @param directory The prefix (directory) to put in
     * @param key The file name to put
     * @param data The [ByteArray] to use to put file
     * @return **true** if the put was successful, false otherwise
     */
    fun putFile(directory: String, key: String, data: ByteArray): Boolean =
        putObject(directory, key, RequestBody.fromBytes(data))

    /**
     * Put a file on the S3 instance
     * @author TURBIEZ Denis
     * @param directory The prefix (directory) to put in
     * @param key The file name to put
     * @param data The input Stream to use to put file
     * @param length The length of the stream
     * @return **true** if the put was successful, false otherwise
     */
    fun putFile(directory: String, key: String, data: InputStream): Boolean =
        putObject(directory, key, RequestBody.fromContentProvider( { data }, "image/gif"))

    private fun putObject(directory: String, key: String, requestBody: RequestBody): Boolean {
        if (s3.listObjects(ListObjectsRequest.builder().bucket(bucket).prefix("$directory/$key").build())
                .contents().size != 0
        ) {
            return false
        }
        s3.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key("$directory/$key")
                .build(),
            requestBody
        )
        return true
    }

    /**
     * Delete a file on the S3 instance
     * @author TURBIEZ Denis
     * @param directory The prefix (directory) to delete from
     * @param key The file name to delete
     * @return **true** if the delete was successful, false otherwise
     */
    fun deleteFile(directory: String, key: String): Boolean {
        if (s3.listObjects(ListObjectsRequest.builder().bucket(bucket).prefix("$directory/$key").build())
                .contents().size == 0
        ) {
            return false
        }
        s3.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(bucket)
                .key("$directory/$key")
                .build()
        )
        return true
    }
}
