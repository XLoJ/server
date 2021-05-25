package cn.xlor.xloj.contest

import cn.xlor.xloj.contest.dto.ContestWithWriter
import cn.xlor.xloj.contest.dto.DetailContest
import cn.xlor.xloj.model.UserProfile
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/contest")
class ContestController(
  private val contestService: ContestService
) {
  @PostMapping("/admin")
  fun createContest() {

  }

  @GetMapping
  fun findAllPublicContests(): List<ContestWithWriter> {
    return contestService.findAllPublicContests()
  }

  @GetMapping("/{cid}")
  fun findDetailContest(@PathVariable cid: Long): DetailContest {
    return contestService.findDetailContest(cid)
  }

  @GetMapping("/{cid}/problems")
  fun findContestProblemList(@PathVariable cid: Long) {

  }

  @GetMapping("/{cid}/problem/{pid}")
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
