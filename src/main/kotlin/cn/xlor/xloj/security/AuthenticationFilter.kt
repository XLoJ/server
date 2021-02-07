package cn.xlor.xloj.security

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

@Component
@Order(1)
class AuthenticationFilter : Filter {
  override fun doFilter(
    request: ServletRequest,
    response: ServletResponse,
    chain: FilterChain
  ) {
    // 1. skip public url
    // 2. check JWT Token
    // 3. Go to cache (memory or redis)
    // 4. Go to database
    request.setAttribute("name", "abc")
    chain.doFilter(request, response)
  }
}
