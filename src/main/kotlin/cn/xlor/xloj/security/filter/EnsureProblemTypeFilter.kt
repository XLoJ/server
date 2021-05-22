package cn.xlor.xloj.security.filter

import cn.xlor.xloj.ProblemAttributeKey
import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.security.makeNotFoundResponse
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * [type] Target type, e.g. "classic"
 *
 * [keywordList] only filter url that has one of keywords
 */
class EnsureProblemTypeFilter(
  private val type: String,
  private val keywordList: List<String>
) : Filter {
  private fun shouldDoFilter(url: String): Boolean {
    return keywordList.any {
      url.indexOf(it, 0, true) != -1
    }
  }

  override fun doFilter(
    request: ServletRequest,
    response: ServletResponse,
    chain: FilterChain
  ) {
    val req = request as HttpServletRequest
    if (shouldDoFilter(req.requestURI)) {
      val problem =
        req.getAttribute(ProblemAttributeKey) as Problem
      if (problem.problemType == type) {
        chain.doFilter(request, response)
      } else {
        makeNotFoundResponse(response, "没有找到编号为 ${problem.id} 且类型为 $type 的题目")
      }
    } else {
      chain.doFilter(request, response)
    }
  }
}
