package cn.xlor.xloj.security

import cn.xlor.xloj.ProblemAttributeKey
import cn.xlor.xloj.UserAttributeKey
import cn.xlor.xloj.model.UserProfile
import cn.xlor.xloj.repository.ProblemRepository
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class ProblemAuthFilter(
  private val problemRepository: ProblemRepository
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

    if (requestURIList.size == 3 || requestURIList[3] == "") {
      // skip POST /polygon/problem
      chain.doFilter(request, response)
    } else {
      if (requestURIList[3].toLongOrNull() != null) {
        val problemId = requestURIList[3].toLong()
        val userProblemList =
          problemRepository.findUserProblemList(userProfile.id)
        val index = userProblemList.indexOfFirst { it.id == problemId }
        if (index != -1) {
          req.setAttribute(ProblemAttributeKey, userProblemList[index])
          chain.doFilter(request, response)
        } else {
          makeNotFoundResponse(response, "没有找到编号为 $problemId 的题目")
        }
      } else {
        makeNotFoundResponse(response, "题目编号错误")
      }
    }
  }
}
