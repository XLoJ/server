package cn.xlor.xloj.polygon.dto

import cn.xlor.xloj.model.ClassicProblemCode
import java.time.Instant

data class DetailClassicProblem(
  val id: Long,
  val parent: Long,
  val name: String,
  var status: Int,
  var checker: ClassicProblemCode?,
  var validator: ClassicProblemCode?,
  var solution: ClassicProblemCode?,
  var testcases: String,
  var version: Int,
  val createTime: Instant,
  val updateTime: Instant
)
