package cn.xlor.xloj.polygon.listener

data class PolygonMessage(
  var index: Int = -1,
  var problem: String = "",
  var version: Int = -1,
  var action: String = "",
  var name: String = "",
  var message: String = "",
  var from: String = "",
  var timestamp: String = "",
  var code: Map<String, Any> = emptyMap(),
  var testcase: Map<String, Any> = emptyMap()
) {
  companion object {
    val START = "start"
    val COMPILE = "compile"
    val COMPILE_ERROR = "compile_error"
    val DOWNLOAD = "download"
    val GEN_IN = "gen_in"
    val VALIDATE = "validate"
    val GEN_ANS = "gen_ans"
    val UPLOAD = "upload"
    val EXAMPLE = "example"
    val END = "end"
    val ERROR = "error"
  }
}

