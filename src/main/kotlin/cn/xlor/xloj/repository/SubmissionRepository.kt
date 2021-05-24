package cn.xlor.xloj.repository

import cn.xlor.xloj.model.Submission
import cn.xlor.xloj.model.Submissions
import cn.xlor.xloj.model.submissions
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.sortedByDescending
import org.ktorm.entity.toList
import org.springframework.stereotype.Repository

@Repository
class SubmissionRepository(
  private val database: Database
) {
  fun findSubmissionById(submissionId: Long): Submission? {
    return database.submissions.find { it.id eq submissionId }
  }

  fun findAllSubmissionsByContest(contestId: Long): List<Submission> {
    return database.submissions.filter { it.contest eq contestId }
      .sortedByDescending { it.id }.toList()
  }

  fun findAllSubmissionsByProblem(problemId: Long): List<Submission> {
    return database.submissions.filter { it.problem eq problemId }
      .sortedByDescending { it.id }.toList()
  }

  fun findAllSubmissionsByProblemAndUser(
    problemId: Long,
    userId: Long
  ): List<Submission> {
    return database.submissions.filter { (it.problem eq problemId) and (it.user eq userId) }
      .sortedByDescending { it.id }.toList()
  }

  /**
   * @return new [Submission] id
   */
  fun createSubmission(
    userId: Long,
    contestId: Long,
    problemId: Long,
    body: String,
    language: String
  ): Long {
    return database.insertAndGenerateKey(Submissions) {
      set(it.user, userId)
      set(it.contest, contestId)
      set(it.problem, problemId)
      set(it.body, body)
      set(it.language, language)
      set(it.verdict, Submission.Waiting)
    } as Long
  }
}
