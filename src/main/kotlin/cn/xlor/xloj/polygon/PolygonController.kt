package cn.xlor.xloj.polygon

import cn.xlor.xloj.exception.NotFoundException
import cn.xlor.xloj.model.ClassicProblem
import cn.xlor.xloj.model.ClassicProblemCode
import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.model.UserProfile
import cn.xlor.xloj.polygon.dto.*
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/polygon")
class PolygonController(
  private val polygonService: PolygonService,
  private val codeService: CodeService
) {
  @GetMapping("/problems")
  fun getAllProblems(@RequestAttribute user: UserProfile): List<ProblemListItem> {
    return polygonService.findUserProblemList(user.id)
  }

  @PostMapping("/problem")
  fun createProblem(
    @RequestAttribute user: UserProfile,
    @Valid @RequestBody createProblemDto: CreateProblemDto
  ): Problem {
    val type = createProblemDto.type.toLowerCase()
    return when (type) {
      "classic" -> {
        polygonService.createClassicProblem(createProblemDto.name, user.id)
      }
      "hdu" -> {
        throw NotFoundException("不支持题目类型 \"$type\"")
      }
      else -> {
        throw NotFoundException("不支持题目类型 \"$type\"")
      }
    }
  }

  @GetMapping("/problem/{pid}")
  fun findProblem(@RequestAttribute problem: Problem): Problem {
    return problem
  }

  @GetMapping("/problem/{pid}/classic")
  fun findClassicProblem(@RequestAttribute problem: Problem): DetailClassicProblem {
    return polygonService.findDetailClassicProblem(problem)
  }

  @PutMapping("/problem/{pid}")
  fun updateProblem(
    @RequestAttribute problem: Problem,
    @Valid @RequestBody updateProblemDto: UpdateProblemDto
  ): Problem {
    return polygonService.updateProblemInfo(problem, updateProblemDto)
  }

  @GetMapping("/problem/{pid}/checker")
  fun findChecker(@RequestAttribute problem: Problem): ClassicProblemCode {
    return codeService.findChecker(problem)
  }

  @PostMapping("/problem/{pid}/checker")
  fun uploadChecker(
    @RequestAttribute problem: Problem,
    @Valid @RequestBody uploadCodeDto: UploadCodeDto
  ): ClassicProblemCode {
    return codeService.uploadChecker(problem, uploadCodeDto)
  }

  @GetMapping("/problem/{pid}/validator")
  fun findValidator(@RequestAttribute problem: Problem): ClassicProblemCode {
    return codeService.findValidator(problem)
  }

  @PostMapping("/problem/{pid}/validator")
  fun uploadValidator(
    @RequestAttribute problem: Problem,
    @Valid @RequestBody uploadCodeDto: UploadCodeDto
  ): ClassicProblemCode {
    return codeService.uploadValidator(problem, uploadCodeDto)
  }

  @GetMapping("/problem/{pid}/solution")
  fun findSolution(@RequestAttribute problem: Problem): ClassicProblemCode {
    return codeService.findSolution(problem)
  }

  @PostMapping("/problem/{pid}/solution")
  fun uploadSolution(
    @RequestAttribute problem: Problem,
    @Valid @RequestBody uploadCodeDto: UploadCodeDto
  ): ClassicProblemCode {
    return codeService.uploadSolution(problem, uploadCodeDto)
  }

  @PostMapping("/problem/{pid}/generator")
  fun uploadGenerator(
    @RequestAttribute problem: Problem,
    @Valid @RequestBody uploadCodeDto: UploadCodeDto
  ): ClassicProblemCode {
    return codeService.uploadGenerator(problem, uploadCodeDto)
  }

  @PutMapping("/problem/{pid}/generator/{cid}")
  fun updateGenerator(
    @RequestAttribute problem: Problem,
    @Valid @RequestBody uploadCodeDto: UploadCodeDto,
    @RequestParam cid: Long
  ): ClassicProblemCode {
    return codeService.updateGenerator(problem, cid, uploadCodeDto)
  }

  @DeleteMapping("/problem/{pid}/generator/{cid}")
  fun removeGenerator(
    @RequestAttribute problem: Problem,
    @RequestParam cid: Long
  ) {
    return codeService.removeGenerator(problem, cid)
  }

  @PostMapping("/problem/{pid}/testcases")
  fun setTestcases(
    @RequestAttribute problem: Problem,
    @Valid @RequestBody updateTestcasesDto: UpdateTestcasesDto
  ): ClassicProblem {
    return polygonService.updateClassicProblemTestcases(
      problem,
      updateTestcasesDto.testcases
    )
  }

  @GetMapping("/problem/{pid}/static")
  fun findStaticFile(@RequestAttribute problem: Problem) {

  }

  @PostMapping("/problem/{pid}/static")
  fun uploadStaticFile(@RequestAttribute problem: Problem) {

  }

  @DeleteMapping("/problem/{pid}/static")
  fun removeStaticFile(@RequestAttribute problem: Problem) {

  }

  @PostMapping("/problem/{pid}/build")
  fun buildProblem(@RequestAttribute problem: Problem) {

  }
}
