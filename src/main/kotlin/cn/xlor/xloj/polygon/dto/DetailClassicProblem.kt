package cn.xlor.xloj.polygon.dto

import cn.xlor.xloj.model.ClassicProblemCode
import java.time.Instant

data class DetailClassicProblem(
  val cpid: Long,
  val parent: Long,
  var status: Int,
  val name: String,
  val timeLimit: Int,
  val memoryLimit: Int,
  val tags: String,
  var checker: ClassicProblemCode?,
  var validator: ClassicProblemCode?,
  var solution: ClassicProblemCode?,
  var testcases: String,
  var version: Int,
  val createTime: Instant,
  val updateTime: Instant
)
