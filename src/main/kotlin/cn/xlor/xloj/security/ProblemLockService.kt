package cn.xlor.xloj.security

import cn.xlor.xloj.model.Problem
import cn.xlor.xloj.repository.ClassicProblemRepository
import cn.xlor.xloj.utils.LoggerDelegate
import org.springframework.integration.redis.util.RedisLockRegistry
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.TimeUnit

@Service
class ProblemLockService(
  private val redisLockRegistry: RedisLockRegistry,
  private val classicProblemRepository: ClassicProblemRepository
) {
  private val logger by LoggerDelegate()

  companion object {
    fun genKey(pid: Long) = "lock/${pid}"

    const val ExpireTime = 60 * 1000L

    const val TryTurn = 5
  }


  fun lock(problem: Problem): Boolean {
    return lock(problem.id)
  }

  fun lock(pid: Long): Boolean {
    val key = genKey(pid)
    val lock = redisLockRegistry.obtain(key)
    lock.tryLock(500, TimeUnit.MILLISECONDS)
    for (i in 0 until TryTurn) {
      val classicProblem =
        classicProblemRepository.findClassicProblemByParentId(pid)

      if (classicProblem.updateTime.toEpochMilli() + ExpireTime < Instant.now()
          .toEpochMilli()
        || classicProblem.status == 0
      ) {
        classicProblemRepository.setClassicProblemStatus(pid, 1)
        lock.unlock()
        logger.info("Problem \"$pid\" lock")
        return true
      }
    }
    lock.unlock()
    logger.info("Problem \"$pid\" fails to lock")
    return false
  }

  fun unlock(problem: Problem) {
    return unlock(problem.id)
  }

  fun unlock(pid: Long) {
    val key = genKey(pid)
    val lock = redisLockRegistry.obtain(key)
    lock.tryLock(500, TimeUnit.MILLISECONDS)
    classicProblemRepository.setClassicProblemStatus(pid, 0)
    lock.unlock()
    logger.info("Problem \"$pid\" unlock")
  }
}
