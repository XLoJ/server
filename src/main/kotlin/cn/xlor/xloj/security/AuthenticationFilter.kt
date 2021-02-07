package cn.xlor.xloj.security

import cn.xlor.xloj.utils.LoggerDelegate
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.math.log

@Component
@Order(1)
class AuthenticationFilter : Filter {
  private val logger by LoggerDelegate()

  private val userMap = mutableMapOf<String, String>()

  override fun doFilter(
    request: ServletRequest,
    response: ServletResponse,
    chain: FilterChain
  ) {
    // 1. skip public url
    // 2. check JWT Token
    // 3. Go to cache (memory or redis)
    // 4. Go to database
    val req = request as HttpServletRequest
    val auth = req.getHeader("Authorization")

    if (auth != null) {
      userMap[auth] = "user"
      request.setAttribute("name", auth)
      chain.doFilter(request, response)
    } else {
      val res = response as HttpServletResponse
      res.status = HttpStatus.UNAUTHORIZED.value()
    }
  }
}
