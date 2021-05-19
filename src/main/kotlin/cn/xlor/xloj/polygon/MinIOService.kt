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
  // PID-CPName/version-type-CodeName
  fun codeFilename(
    pid: Long,
    classicProblem: ClassicProblem,
    classicProblemCode: ClassicProblemCode
  ) =
    "$pid-${classicProblem.name}/${classicProblemCode.version}-${classicProblemCode.type}-${classicProblemCode.name}"

  fun uploadCodeToMinIO(
    pid: Long,
    classicProblem: ClassicProblem,
    classicProblemCode: ClassicProblemCode
  ) {
    val fileName = codeFilename(pid, classicProblem, classicProblemCode)
    val file = """
      {
        "language": "${classicProblemCode.lanuage}",
        "body": "${classicProblemCode.body}"
      }
    """.trimIndent()
    minIOUtils.uploadFile(ProblemBucketName, fileName, file.byteInputStream())
  }
}
