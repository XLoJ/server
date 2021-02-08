package cn.xlor.xloj.user

import cn.xlor.xloj.user.dto.UserLoginDto
import cn.xlor.xloj.user.dto.UserRegisterDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/")
class UserController(
  private val userService: UserService
) {
  @PostMapping("/login")
  fun login(@Valid @RequestBody userLoginDto: UserLoginDto): UserLoginResponse {
    return userService.login(userLoginDto.username, userLoginDto.password)
  }

  @PostMapping("/register")
  fun register(@Valid @RequestBody userRegisterDto: UserRegisterDto): UserLoginResponse {
    userService.register(userRegisterDto)
    return userService.login(userRegisterDto.username, userRegisterDto.password)
  }
}
