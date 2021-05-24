package cn.xlor.xloj.contest

import cn.xlor.xloj.repository.ContestRepository
import org.springframework.stereotype.Service

@Service
class ContestService(
  private val contestRepository: ContestRepository
)
