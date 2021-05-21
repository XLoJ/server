package cn.xlor.xloj.polygon

import cn.xlor.xloj.exception.BadRequestException
import cn.xlor.xloj.exception.NotFoundException
import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.polygon.dto.StaticFileSummary
import cn.xlor.xloj.polygon.dto.UploadStaticFileDto
import cn.xlor.xloj.repository.ClassicProblemRepository
import org.springframework.stereotype.Service
import java.io.BufferedReader

@Service
class StaticFileService(
  private val minIOService: MinIOService,
  private val classicProblemRepository: ClassicProblemRepository
) {
  fun getAllStaticFilename(problem: Problem): List<String> {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)
    return minIOService.listStaticFilename(problem.id, classicProblem)
      .map {
        it.split('/').last()
      }
  }

  fun getAllStaticFileSummary(problem: Problem): List<StaticFileSummary> {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)
    return getAllStaticFilename(problem)
      .map {
        StaticFileSummary(
          it,
          minIOService.getStaticFileSummary(
            problem.id,
            classicProblem,
            it
          )
        )
      }
  }

  fun downloadFile(problem: Problem, filename: String): BufferedReader {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)
    val filenames = minIOService.listStaticFilename(problem.id, classicProblem)
      .map { it.split('/').last() }
    if (filenames.contains(filename)) {
      return minIOService.downloadStaticFile(
        problem.id,
        classicProblem,
        filename
      )
    } else {
      throw NotFoundException("未找到名为 \"${filename}\" 的静态文件")
    }
  }

  fun uploadStaticFile(
    problem: Problem,
    uploadStaticFileDto: UploadStaticFileDto
  ) {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)
    val filenames = minIOService.listStaticFilename(problem.id, classicProblem)
      .map { it.split('/').last() }
    if (!filenames.contains(uploadStaticFileDto.filename)) {
      return minIOService.uploadStaticFileToMinIO(
        problem.id,
        classicProblem,
        uploadStaticFileDto.filename,
        uploadStaticFileDto.body
      )
    } else {
      throw BadRequestException("\"${uploadStaticFileDto.filename}\" 的静态文件已经存在")
    }
  }

  fun removeStaticFile(problem: Problem, filename: String) {
    val classicProblem =
      classicProblemRepository.findClassicProblemByParentId(problem.id)
    val filenames = minIOService.listStaticFilename(problem.id, classicProblem)
      .map { it.split('/').last() }
    if (filenames.contains(filename)) {
      minIOService.removeStaticFileFromMinIO(
        problem.id,
        classicProblem,
        filename
      )
    } else {
      throw NotFoundException("未找到名为 \"${filename}\" 的静态文件")
    }
  }
}
