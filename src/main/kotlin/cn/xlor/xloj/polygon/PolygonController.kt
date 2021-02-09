package cn.xlor.xloj.polygon

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/polygon")
class PolygonController {
  @GetMapping("/problems")
  fun getAllProblems(): String {
    return "problems"
  }
}
