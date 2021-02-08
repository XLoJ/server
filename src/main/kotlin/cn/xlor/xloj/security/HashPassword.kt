package cn.xlor.xloj.security

import org.mindrot.jbcrypt.BCrypt

const val Workload = 12

fun hashPassword(plain_password: String): String {
  val salt = BCrypt.gensalt(Workload)
  return BCrypt.hashpw(plain_password, salt)
}

fun checkPassword(plain_password: String, hashed_password: String): Boolean {
  if (!hashed_password.startsWith("$2a$")) {
    throw IllegalArgumentException("Invalid hash provided for comparison")
  }
  return BCrypt.checkpw(plain_password, hashed_password)
}
