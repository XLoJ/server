package cn.xlor.xloj.security

import cn.xlor.xloj.UserAttributeKey
import cn.xlor.xloj.model.UserProfile
import cn.xlor.xloj.repository.UserRepository
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class PolygonAuthFilter(
  private val userRepository: UserRepository
) : Filter {
  override fun doFilter(
    request: ServletRequest,
    response: ServletResponse,
    chain: FilterChain
  ) {
    val req = request as HttpServletRequest
    val userProfile =
      req.getAttribute(UserAttributeKey) as UserProfile
    val userGroups = userRepository.findUserGroups(userProfile.id)
    val isFindPolygonAuth =
      userGroups.any { it.group.id == userRepository.polygonGroup().id }
    if (isFindPolygonAuth) {
      chain.doFilter(request, response)
    } else {
      makeUnAuthorizeResponse(response, "您没有 Polygon 权限", userProfile.username)
    }
  }
}
