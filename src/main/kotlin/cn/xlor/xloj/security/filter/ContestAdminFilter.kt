package cn.xlor.xloj.security.filter

import cn.xlor.xloj.ContestAttributeKey
import cn.xlor.xloj.UserAttributeKey
import cn.xlor.xloj.model.UserProfile
import cn.xlor.xloj.repository.ContestRepository
import cn.xlor.xloj.repository.UserRepository
import cn.xlor.xloj.security.makeNotFoundResponse
import cn.xlor.xloj.security.makeUnAuthorizeResponse
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest


class ContestAdminFilter(
  private val userRepository: UserRepository,
  private val contestRepository: ContestRepository,
) : Filter {
  override fun doFilter(
    request: ServletRequest,
    response: ServletResponse,
    chain: FilterChain
  ) {
    val req = request as HttpServletRequest
    val userProfile =
      req.getAttribute(UserAttributeKey) as UserProfile

    val requestURIList = req.requestURI.split("/")
    if (requestURIList.size > 3 && requestURIList[3].toLongOrNull() != null) {
      val contestId = requestURIList[3].toLong()
      val contest = contestRepository.findContestById(contestId)
      if (contest != null) {
        if (contestRepository.isBuiltinContest(contest)) {
          makeUnAuthorizeResponse(response, "无权修改内置比赛")
        } else if (userRepository.isUserAdmin(userProfile.id)
          || userProfile.id == contest.creator
          || contestRepository.checkUserManageContest(
            contestId,
            userProfile.id
          )
        ) {
          req.setAttribute(ContestAttributeKey, contest)
          chain.doFilter(request, response)
        } else {
          makeUnAuthorizeResponse(response, "无权访问编号为 $contestId 的比赛")
        }
      } else {
        makeNotFoundResponse(response, "没有找到编号为 $contestId 的比赛")
      }
    } else {
      makeNotFoundResponse(response, "比赛编号错误")
    }
  }
}
