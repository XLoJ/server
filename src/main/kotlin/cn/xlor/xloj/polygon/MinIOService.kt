package cn.xlor.xloj.polygon

import cn.xlor.xloj.ProblemBucketName
import cn.xlor.xloj.model.ClassicProblem
import cn.xlor.xloj.model.ClassicProblemCode
import cn.xlor.xloj.utils.MinIOUtils
import org.springframework.stereotype.Service
import java.io.BufferedReader

@Service
class MinIOService(
  private val minIOUtils: MinIOUtils
) {
  fun problemFolderName(pid: Long, classicProblem: ClassicProblem) =
    "$pid-${classicProblem.name}"

  // PID-CPName/version-type-CodeName
  fun codeFilename(
    pid: Long,
    classicProblem: ClassicProblem,
    classicProblemCode: ClassicProblemCode
  ) =
    "${
      problemFolderName(
        pid,
        classicProblem
      )
    }/${classicProblemCode.version}-${classicProblemCode.type}-${classicProblemCode.name}"

  fun staticFilename(
    pid: Long,
    classicProblem: ClassicProblem,
    filename: String
  ) = "${problemFolderName(pid, classicProblem)}/static/$filename"

  private fun testcaseFilename(
    pid: Long,
    classicProblem: ClassicProblem,
    version: Int,
    tid: Int,
    suffix: String
  ) = "${
    problemFolderName(
      pid,
      classicProblem
    )
  }/testcases/${version}/$tid.$suffix"

  fun testcaseInFilename(
    pid: Long,
    classicProblem: ClassicProblem,
    version: Int,
    tid: Int
  ) = testcaseFilename(pid, classicProblem, version, tid, "in")

  fun testcaseAnsFilename(
    pid: Long,
    classicProblem: ClassicProblem,
    version: Int,
    tid: Int
  ) = testcaseFilename(pid, classicProblem, version, tid, "ans")

  fun uploadCodeToMinIO(
    pid: Long,
    classicProblem: ClassicProblem,
    classicProblemCode: ClassicProblemCode
  ) {
    val fileName = codeFilename(pid, classicProblem, classicProblemCode)
    val file = """
      {
        "language": "${classicProblemCode.language}",
        "body": "${classicProblemCode.body}"
      }
    """.trimIndent()
    minIOUtils.uploadFile(ProblemBucketName, fileName, file.byteInputStream())
  }

  fun uploadStaticFileToMinIO(
    pid: Long,
    classicProblem: ClassicProblem,
    name: String,
    content: String
  ) {
    val fileName = staticFilename(pid, classicProblem, name)
    minIOUtils.uploadFile(
      ProblemBucketName,
      fileName,
      content.byteInputStream()
    )
  }

  fun removeStaticFileFromMinIO(
    pid: Long,
    classicProblem: ClassicProblem,
    name: String
  ) {
    val fileName = staticFilename(pid, classicProblem, name)
    return minIOUtils.removeFile(ProblemBucketName, fileName)
  }

  fun listStaticFilename(
    pid: Long,
    classicProblem: ClassicProblem
  ): List<String> {
    val prefix = "${problemFolderName(pid, classicProblem)}/static/"
    return minIOUtils.listFilename(ProblemBucketName, prefix)
  }

  fun getStaticFileSummary(
    pid: Long,
    classicProblem: ClassicProblem,
    name: String,
    length: Int = 255
  ): String {
    val fileName = staticFilename(pid, classicProblem, name)
    val stream = minIOUtils.getFileToStream(ProblemBucketName, fileName)
    val builder = StringBuilder()
    stream.use {
      var cur = it.read().toChar()
      var isBreak = false
      for (i in 0 until length) {
        builder.append(cur)
        val something = it.read()
        if (something == -1) {
          isBreak = true
          break
        }
        cur = something.toChar()
      }
      if (!isBreak) {
        builder.append('\n')
        builder.append('.')
        builder.append('.')
        builder.append('.')
        builder.append('.')
        builder.append('.')
        builder.append('.')
      }
    }
    return builder.toString()
  }

  fun downloadStaticFile(
    pid: Long,
    classicProblem: ClassicProblem,
    name: String
  ): BufferedReader {
    val fileName = staticFilename(pid, classicProblem, name)
    return minIOUtils.getFileToStream(ProblemBucketName, fileName)
  }

  fun downloadTestcaseInFile(
    pid: Long,
    classicProblem: ClassicProblem,
    version: Int,
    tid: Int
  ): BufferedReader {
    val fileName = testcaseInFilename(pid, classicProblem, version, tid)
    return minIOUtils.getFileToStream(ProblemBucketName, fileName)
  }

  fun downloadTestcaseAnsFile(
    pid: Long,
    classicProblem: ClassicProblem,
    version: Int,
    tid: Int
  ): BufferedReader {
    val fileName = testcaseAnsFilename(pid, classicProblem, version, tid)
    return minIOUtils.getFileToStream(ProblemBucketName, fileName)
  }
}
