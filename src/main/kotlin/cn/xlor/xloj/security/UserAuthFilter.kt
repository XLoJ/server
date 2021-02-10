package cn.xlor.xloj.security

import cn.xlor.xloj.model.toUserProfile
import cn.xlor.xloj.repository.UserRepository
import cn.xlor.xloj.utils.LoggerDelegate
import org.springframework.http.HttpHeaders
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class UserAuthFilter(
  private val jwtService: JWTService,
  private val userRepository: UserRepository
) : Filter {
  companion object {
    const val userRequestAttributeKey = "user"
  }

  private val logger by LoggerDelegate()

  override fun doFilter(
    request: ServletRequest,
    response: ServletResponse,
    chain: FilterChain
  ) {
    val req = request as HttpServletRequest
    val auth = req.getHeader(HttpHeaders.AUTHORIZATION)

    if (auth != null) {
      val optionalUsername = jwtService.verify(auth)
      if (optionalUsername.isPresent) {
        val username = optionalUsername.get()
        logger.info("\"$username\" is visiting...")
        req.setAttribute(
          userRequestAttributeKey,
          userRepository.findOneUserByUsername(username)!!.toUserProfile()
        )
        chain.doFilter(request, response)
      } else {
        makeUnAuthorizeResponse(response, "Token 错误")
      }
    } else {
      makeUnAuthorizeResponse(response, "用户不存在")
    }
  }
}
