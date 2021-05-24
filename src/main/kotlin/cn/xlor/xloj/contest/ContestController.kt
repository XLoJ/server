package cn.xlor.xloj.contest

import cn.xlor.xloj.model.UserProfile
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/contest")
class ContestController(
  private val contestService: ContestService
) {
  @GetMapping
  fun findAllContests() {

  }

  @GetMapping("/{cid}")
  fun findDetailContest(@PathVariable cid: Long) {

  }

  @GetMapping("/{cid}/problems")
  fun findContestProblemList(@PathVariable cid: Long) {

  }

  @GetMapping("/{cid}/{pid}")
  fun findProblem(@PathVariable cid: Long, @PathVariable pid: Long) {

  }

  @PostMapping("/{cid}/submit")
  fun submitCode(
    @RequestAttribute user: UserProfile,
    @PathVariable cid: Long,
    @RequestParam problem: Int
  ) {

  }
}
