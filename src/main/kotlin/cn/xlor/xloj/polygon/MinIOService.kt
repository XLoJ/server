package cn.xlor.xloj.polygon

import cn.xlor.xloj.model.ClassicProblem
import cn.xlor.xloj.model.ClassicProblemCode
import cn.xlor.xloj.utils.MinIOUtils
import org.springframework.stereotype.Service

@Service
class MinIOService(
  private val minIOUtils: MinIOUtils
) {
  fun uploadCodeToMinIO(
    classicProblem: ClassicProblem,
    classicProblemCode: ClassicProblemCode
  ) {

  }
}
