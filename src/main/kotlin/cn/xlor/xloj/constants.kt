package cn.xlor.xloj

/**
 * Set this attribute in [cn.xlor.xloj.security.filter.UserAuthFilter]
 *
 * Value type: [cn.xlor.xloj.model.UserProfile]
 */
const val UserAttributeKey = "user"

/**
 * Set this attribute in [cn.xlor.xloj.security.filter.ProblemAuthFilter]
 *
 * Value type: [cn.xlor.xloj.model.Problem]
 */
const val ProblemAttributeKey = "problem"

const val ProblemBucketName = "problems"

const val PolygonQueuName = "Polygon"

const val PolygonMessageQueueName = "PolygonMessage"

const val ClassicJudgeQueueName = "Judge"

const val ClassicJudgeMessageQueueName = "JudgeMessage"
