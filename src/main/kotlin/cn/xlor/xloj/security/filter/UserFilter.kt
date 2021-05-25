package cn.xlor.xloj.security.filter

import cn.xlor.xloj.UserAttributeKey
import cn.xlor.xloj.model.toUserProfile
import cn.xlor.xloj.repository.UserRepository
import cn.xlor.xloj.security.JWTService
import org.springframework.http.HttpHeaders
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class UserFilter(
  private val jwtService: JWTService,
  private val userRepository: UserRepository
) : Filter {
  override fun doFilter(
    request: ServletRequest,
    response: ServletResponse,
    chain: FilterChain
  ) {
    val req = request as HttpServletRequest
    val auth = req.getHeader(HttpHeaders.AUTHORIZATION)
    val userProfile =
      req.getAttribute(UserAttributeKey)

    if (userProfile == null && auth != null) {
      val optionalUsername = jwtService.verify(auth)
      if (optionalUsername.isPresent) {
        val username = optionalUsername.get()
        req.setAttribute(
          UserAttributeKey,
          userRepository.findOneUserByUsername(username)!!.toUserProfile()
        )
      }
    }
    chain.doFilter(request, response)
  }
}
