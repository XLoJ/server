package cn.xlor.xloj.security

import cn.xlor.xloj.utils.LoggerDelegate
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JWTService {

  private val logger by LoggerDelegate()

  @Value("\${secret}")
  lateinit var secret: String

  val key: SecretKey get() = Keys.hmacShaKeyFor(secret.encodeToByteArray())

  fun create(username: String): String {
    return Jwts.builder()
      .setSubject(username)
      .setIssuedAt(Date())
      .setExpiration(Date(Date().time + 60000))
      .signWith(key)
      .compact()
  }

  fun verify(token: String): Optional<String> {
    return try {
      val claimsJws =
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
      Optional.of(claimsJws.body.subject)
    } catch (ex: JwtException) {
      logger.error(ex.message)
      Optional.empty()
    }
  }
}
