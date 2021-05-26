package cn.xlor.xloj.contest

import cn.xlor.xloj.UserAttributeKey
import cn.xlor.xloj.contest.dto.*
import cn.xlor.xloj.model.Contest
import cn.xlor.xloj.model.ContestProblem
import cn.xlor.xloj.model.Submission
import cn.xlor.xloj.model.UserProfile
import cn.xlor.xloj.problem.dto.ClassicSubmissionDto
import cn.xlor.xloj.problem.dto.DetailClassicSubmission
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

  @PostMapping("/admin/{cid}/problem")
  fun pushContestProblem(
    @RequestAttribute user: UserProfile,
    @RequestAttribute contest: Contest,
    @RequestParam problem: Long
  ): ContestProblem {
    return contestService.pushContestProblem(user, contest, problem)
  }

  @PutMapping("/admin/{cid}/problem/visible")
  fun setContestProblemVisible(
    @RequestAttribute contest: Contest,
    @RequestParam contestProblem: Long,
    @RequestParam visible: Boolean
  ): ContestProblem {
    return contestService.setContestProblemVisible(
      contest,
      contestProblem,
      visible
    )
  }

  @DeleteMapping("/admin/{cid}/problem")
  fun removeContestProblem(
    @RequestAttribute contest: Contest,
    @RequestParam contestProblem: Long
  ) {
    return contestService.removeContestProblem(contest, contestProblem)
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
  fun findContestProblemList(
    @PathVariable cid: Long,
    request: HttpServletRequest
  ): List<ContestProblem> {
    return findDetailContest(cid, request).problems
  }

  @GetMapping("/{cid}/problem/{index}")
  fun findProblem(
    @PathVariable cid: Long,
    @PathVariable index: Int,
    request: HttpServletRequest
  ): ContestProblem {
    val user = request.getAttribute(UserAttributeKey) as UserProfile?
    return contestService.findContestProblemByIndex(user, cid, index)
  }

  @GetMapping("/{cid}/contestproblem/{pid}")
  fun findProblem(
    @PathVariable cid: Long,
    @PathVariable pid: Long,
    request: HttpServletRequest
  ): ContestProblem {
    val user = request.getAttribute(UserAttributeKey) as UserProfile?
    return contestService.findContestProblemByCpid(user, cid, pid)
  }

  @PostMapping("/{cid}/submit")
  fun submitCode(
    @RequestAttribute user: UserProfile,
    @PathVariable cid: Long,
    @RequestParam problem: Long,
    @RequestBody submission: ClassicSubmissionDto
  ): Submission {
    return contestService.submitCode(user, cid, problem, submission)
  }

  @GetMapping("/{cid}/submission/{sid}")
  fun findDetailSubmisison(
    @PathVariable cid: Long,
    @PathVariable sid: Long,
    request: HttpServletRequest
  ): DetailClassicSubmission {
    val user = request.getAttribute(UserAttributeKey) as UserProfile?
    return contestService.findUserSubmissionDetail(user, cid, sid)
  }

  @GetMapping("/{cid}/submissions/my")
  fun findMySubmisisons(
    @RequestAttribute user: UserProfile,
    @PathVariable cid: Long,
  ): List<SubmissionSummary> {
    return contestService.findUserSubmissions(user, cid)
  }

  @GetMapping("/{cid}/submissions")
  fun findAllSubmisisons(
    @PathVariable cid: Long,
    request: HttpServletRequest
  ): List<SubmissionSummary> {
    val user = request.getAttribute(UserAttributeKey) as UserProfile?
    return contestService.findAllContestSubmissions(user, cid)
  }
}
