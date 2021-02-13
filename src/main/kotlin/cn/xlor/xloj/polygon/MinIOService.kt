package cn.xlor.xloj.polygon

import cn.xlor.xloj.ProblemBucketName
import cn.xlor.xloj.model.ClassicProblem
import cn.xlor.xloj.model.ClassicProblemCode
import cn.xlor.xloj.utils.MinIOUtils
import org.springframework.stereotype.Service

@Service
class MinIOService(
  private val minIOUtils: MinIOUtils
) {
  fun uploadCodeToMinIO(
    pid: Long,
    classicProblem: ClassicProblem,
    classicProblemCode: ClassicProblemCode
  ) {
    val fileName =
      "$pid-${classicProblem.name}/${classicProblemCode.version}-${classicProblemCode.type}"
    val file = """
      {
        "language": "${classicProblemCode.lanuage}",
        "body": "${classicProblemCode.body}"
      }
    """.trimIndent()
    println(file)
    minIOUtils.uploadFile(ProblemBucketName, fileName, file.byteInputStream())
  }
}
