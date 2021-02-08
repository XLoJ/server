package cn.xlor.xloj.security

import cn.xlor.xloj.model.toUserProfile
import cn.xlor.xloj.repository.UserRepository
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

class UserAuthFilter(
  private val jwtService: JWTService,
  private val userRepository: UserRepository
) : Filter {
  private val logger by LoggerDelegate()

  override fun doFilter(
    request: ServletRequest,
    response: ServletResponse,
    chain: FilterChain
  ) {
    val req = request as HttpServletRequest
    val auth = req.getHeader("Authorization")

    if (auth != null) {
      val optionalUsername = jwtService.verify(auth)
      if (optionalUsername.isPresent) {
        val username = optionalUsername.get()
        logger.info("$username is visiting...")
        req.setAttribute("user", userRepository.findOneUserByUsername(username)!!.toUserProfile())
        chain.doFilter(request, response)
      } else {
        val res = response as HttpServletResponse
        res.status = HttpStatus.UNAUTHORIZED.value()
        // TODO: add UNAUTHORIZED message
      }
    } else {
      val res = response as HttpServletResponse
      res.status = HttpStatus.UNAUTHORIZED.value()
    }
  }
}
