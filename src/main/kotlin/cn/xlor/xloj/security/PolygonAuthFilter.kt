package cn.xlor.xloj.security

import cn.xlor.xloj.model.UserProfile
import cn.xlor.xloj.repository.UserRepository
import org.springframework.http.HttpStatus
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class PolygonAuthFilter(
  private val userRepository: UserRepository
): Filter {
  override fun doFilter(
    request: ServletRequest,
    response: ServletResponse,
    chain: FilterChain
  ) {
    val req = request as HttpServletRequest
    val userProfile = req.getAttribute(UserAuthFilter.userRequestAttributeKey) as UserProfile
    val userGroups = userRepository.findUserGroups(userProfile.id)
    val isFindPolygonAuth = userGroups.any { it.group.id == userRepository.polygonGroup().id }
    if (isFindPolygonAuth) {
      chain.doFilter(request, response)
    } else {
      makeUnAuthorizeResponse(response, "您没有 Polygon 权限", userProfile.username)
    }
  }
}
