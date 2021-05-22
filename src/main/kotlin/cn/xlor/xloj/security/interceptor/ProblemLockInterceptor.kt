package cn.xlor.xloj.security.interceptor

import cn.xlor.xloj.ProblemAttributeKey
import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.security.ProblemLockService
import cn.xlor.xloj.security.makeBadRequestResponse
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ProblemLockInterceptor(
  private val problemLockService: ProblemLockService
) : HandlerInterceptor {
  companion object {
    const val LockKey = "lock_key"
  }

  override fun preHandle(
    request: HttpServletRequest,
    response: HttpServletResponse,
    handler: Any
  ): Boolean {
    val split = request.requestURI.split("/")
    // Skip /polygon/problem/{pid}/build
    if (split.size >= 5 && split[4] == "build") return true
    return if (request.method == "POST" || request.method == "PUT" || request.method == "DELETE") {
      val problem =
        request.getAttribute(ProblemAttributeKey) as Problem
      val flag = problemLockService.lock(problem)
      if (flag) {
        request.setAttribute(LockKey, true)
      } else {
        makeBadRequestResponse(response, "Fail to obtain lock")
      }
      flag
    } else {
      true
    }
  }

  override fun afterCompletion(
    request: HttpServletRequest,
    response: HttpServletResponse,
    handler: Any,
    ex: Exception?
  ) {
    val key = request.getAttribute(LockKey) as Boolean?
    if (key != null && key) {
      val problem =
        request.getAttribute(ProblemAttributeKey) as Problem
      problemLockService.unlock(problem)
    }
  }
}
