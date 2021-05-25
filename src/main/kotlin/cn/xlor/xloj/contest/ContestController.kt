package cn.xlor.xloj.contest

import cn.xlor.xloj.UserAttributeKey
import cn.xlor.xloj.contest.dto.ContestWithWriter
import cn.xlor.xloj.contest.dto.CreateContestDto
import cn.xlor.xloj.contest.dto.DetailContest
import cn.xlor.xloj.contest.dto.UpdateContestDto
import cn.xlor.xloj.model.Contest
import cn.xlor.xloj.model.UserProfile
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid


@RestController
@RequestMapping("/contest")
class ContestController(
  private val contestService: ContestService
) {
  @PostMapping("/create")
  fun createContest(
    @RequestAttribute user: UserProfile,
    @Valid @RequestBody createContestDto: CreateContestDto
  ): DetailContest {
    return contestService.createContest(createContestDto.name, user)
  }

  @PostMapping("/admin/{cid}")
  fun updateContest(
    @RequestAttribute contest: Contest,
    @Valid @RequestBody updateContestDto: UpdateContestDto
  ): Contest {
    return contestService.updateContest(contest, updateContestDto)
  }

  @PostMapping("/admin/{cid}/public")
  fun updateContestPublic(
    @RequestAttribute contest: Contest,
    @RequestParam(
      value = "public",
      required = false,
      defaultValue = "false"
    ) public: Boolean
  ): Contest {
    return contestService.updateContestPublic(contest, public)
  }

  @GetMapping
  fun findAllPublicContests(request: HttpServletRequest): List<ContestWithWriter> {
    val user = request.getAttribute(UserAttributeKey)
    return if (user == null) {
      contestService.findAllPublicContests()
    } else {
      contestService.findAllUserContests(user as UserProfile)
    }
  }

  @GetMapping("/{cid}")
  fun findDetailContest(
    @PathVariable cid: Long,
    request: HttpServletRequest
  ): DetailContest {
    val user = request.getAttribute(UserAttributeKey)
    return if (user == null) {
      contestService.findPublicDetailContest(cid)
    } else {
      contestService.findDetailContestWithUser(cid, user as UserProfile)
    }
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
