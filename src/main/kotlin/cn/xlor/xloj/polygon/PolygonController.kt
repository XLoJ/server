package cn.xlor.xloj.polygon

import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.model.UserProfile
import cn.xlor.xloj.polygon.dto.UpdateProblemDto
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/polygon")
class PolygonController(
  private val polygonService: PolygonService
) {
  @GetMapping("/problems")
  fun getAllProblems(@RequestAttribute user: UserProfile): List<Problem> {
    return polygonService.findUserProblemList(user.id)
  }

  @PostMapping("/problem")
  fun createProblem() {

  }

  @GetMapping("/problem/{pid}")
  fun findProblem(@RequestAttribute problem: Problem): Problem {
    return problem
  }

  @PutMapping("/problem/{pid}")
  fun updateProblem(
    @RequestAttribute problem: Problem,
    @Valid @RequestBody updateProblemDto: UpdateProblemDto
  ): Problem {
    return polygonService.updateProblemInfo(problem, updateProblemDto)
  }
}
