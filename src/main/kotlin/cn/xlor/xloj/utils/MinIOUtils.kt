package cn.xlor.xloj.utils

import cn.xlor.xloj.configuration.MinioConfiguration
import io.minio.*
import io.minio.messages.DeleteObject
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStream

@Component
class MinIOUtils(
  val minioConfiguration: MinioConfiguration
) {
  fun client(): MinioClient = MinioClient.builder()
    .endpoint(minioConfiguration.url)
    .credentials(minioConfiguration.accessKey, minioConfiguration.secretKey)
    .build()

  fun makeBucket(bucketName: String) {
    val bucket = MakeBucketArgs.builder().bucket(bucketName).build()
    client().makeBucket(bucket)
  }

  fun bucketExists(bucketName: String): Boolean {
    val bucket = BucketExistsArgs.builder().bucket(bucketName).build()
    return client().bucketExists(bucket)
  }

  fun uploadFile(
    bucketName: String,
    fileName: String,
    fileStream: InputStream,
    fileTags: Map<String, String> = emptyMap()
  ): ObjectWriteResponse {
    val args = PutObjectArgs.builder()
      .bucket(bucketName)
      .`object`(fileName)
      .tags(fileTags)
      .stream(fileStream, -1, 10485760)
      .build()
    return client().putObject(args)
  }

  fun getFile(bucketName: String, fileName: String): String {
    val args =
      GetObjectArgs.builder().bucket(bucketName).`object`(fileName).build()
    val getObjectResponse = client().getObject(args)
    return getObjectResponse.bufferedReader().use(BufferedReader::readText)
  }

  fun getFileTags(bucketName: String, fileName: String): Map<String, String?> {
    val args =
      GetObjectTagsArgs.builder().bucket(bucketName).`object`(fileName).build()
    val objectTags = client().getObjectTags(args)
    return objectTags.get()
  }

  fun removeFiles(bucketName: String, files: List<String>) {
    val objects = files.map { DeleteObject(it) }
    val args =
      RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build()
    client().removeObjects(args)
  }
}
