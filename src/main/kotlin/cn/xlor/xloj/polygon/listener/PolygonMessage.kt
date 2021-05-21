package cn.xlor.xloj.polygon.listener

data class PolygonMessage(
  var index: Int = -1,
  var problem: String = "",
  var version: Int = -1,
  var action: String = "",
  var name: String = "",
  var message: String = "",
  var from: String = "",
  var timestamp: String = ""
) {
  companion object {
    val START = "start"
    val DOWNLOAD = "download"
    val COMPILE = "compile"
    val END = "end"
    val ERROR = "error"
  }
}

