package cn.xlor.xloj.problem.listener

data class ClassicJudgeMessage(
  var index: Int = 0,
  var from: String = "",
  var timestamp: String = "",
  var id: Long = 0,
  var verdict: Int = 0,
  var time: Double = 0.0,
  var memory: Double = 0.0,
  var pass: Int = 0,
  var stdout: String = "",
  var checkerOut: String = "",
  var message: String = ""
)
