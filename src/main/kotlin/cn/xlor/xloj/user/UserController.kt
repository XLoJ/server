package cn.xlor.xloj.user

import cn.xlor.xloj.model.UserProfile
import cn.xlor.xloj.user.dto.UserLoginDto
import cn.xlor.xloj.user.dto.UserRegisterDto
import cn.xlor.xloj.user.dto.UserRoleProfile
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/")
class UserController(
  private val userService: UserService
) {
  /**
   * User login.
   */
  @PostMapping("/login")
  fun login(@Valid @RequestBody userLoginDto: UserLoginDto): UserLoginResponse {
    return userService.login(userLoginDto.username, userLoginDto.password)
  }

  /**
   * Register a new user.
   */
  @PostMapping("/register")
  fun register(@Valid @RequestBody userRegisterDto: UserRegisterDto): UserLoginResponse {
    userService.register(userRegisterDto)
    return userService.login(userRegisterDto.username, userRegisterDto.password)
  }

  /**
   * Get user profile.
   */
  @GetMapping("/profile")
  fun getMyProfile(@RequestAttribute user: UserProfile): UserRoleProfile {
    return UserRoleProfile(
      user.id,
      user.username,
      user.nickname,
      userService.findUserGroups(user.id)
    )
  }
}
