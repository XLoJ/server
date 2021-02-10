package cn.xlor.xloj.polygon

import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.model.UserProfile
import org.springframework.web.bind.annotation.*

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
  fun findProblem(): String {
    return "problem"
  }
}
