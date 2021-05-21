package cn.xlor.xloj.configuration

import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitmqConfiguration {
  @Bean
  fun rabbitmqJsonMessageConverter(): MessageConverter {
    return Jackson2JsonMessageConverter()
  }

  @Bean
  fun jsonRabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
    val rabbitTemplate = RabbitTemplate(connectionFactory)
    rabbitTemplate.messageConverter = Jackson2JsonMessageConverter()
    return rabbitTemplate
  }
}
