package cn.xlor.xloj.user

import cn.xlor.xloj.exception.IncorrectPasswordException
import cn.xlor.xloj.exception.UnknownUserException
import cn.xlor.xloj.model.UserProfile
import cn.xlor.xloj.model.toUserProfile
import cn.xlor.xloj.repository.UserRepository
import cn.xlor.xloj.security.JWTService
import cn.xlor.xloj.security.checkPassword
import cn.xlor.xloj.user.dto.UserRegisterDto
import org.springframework.stereotype.Service

data class UserLoginResponse(
  val user: UserProfile,
  val access_token: String
)

@Service
class UserService(
  private val userRepository: UserRepository,
  private val jwtService: JWTService
) {
  fun login(username: String, password: String): UserLoginResponse {
    val user = userRepository.findOneUserByUsername(username)
    if (user != null) {
      if (checkPassword(password, user.password)) {
        val accessToken = jwtService.create(username)
        return UserLoginResponse(user.toUserProfile(), accessToken)
      } else {
        throw IncorrectPasswordException(username)
      }
    } else {
      throw UnknownUserException(username)
    }
  }

  fun register(userRegisterDto: UserRegisterDto) {
    userRepository.createUser(userRegisterDto)
  }
}
