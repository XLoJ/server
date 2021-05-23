package cn.xlor.xloj.repository

import org.ktorm.database.Database
import org.springframework.stereotype.Repository

@Repository
class SubmissionRepository(
  private val database: Database
)
