package com.shop.order.mq;

import java.util.Date;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderSender {
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	// 推送消息会触发confirmCallback接口，返回ack和cause，成功推送则true，失败则false且可能带cause
	final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
		public void confirm(CorrelationData correlationData, boolean ack, String cause) {
			if (ack) {
				log.info(correlationData + "消息已发送到交换器中");
			} else {
				log.debug("消息没有发送到交换器中");
				// 如果是因为有拒绝原因
				if (cause != null) {
					log.debug("拒绝原因：" + cause);
				} else {
					// 重试3次，不行就抛异常
				}
			}
		}
	};
	
	// 没有推送到队列上就会触发returnCallback接口
	final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
		public void returnedMessage(Message message, int replyCode, String replyText, String exchange,
				String routingKey) {
			log.debug("返回交换器: " + exchange + ", 路由键: " + routingKey + ", 回复Code: " + replyCode + ", 回复文本: " + replyText);
		}
	};
	
	// 发送消息方法调用: 构建Message消息
	public void sendOrder(String Id, String jsonObject) throws Exception {
		rabbitTemplate.setConfirmCallback(confirmCallback); 
		rabbitTemplate.setReturnCallback(returnCallback);
		// Id + 时间戳 全局唯一
		CorrelationData correlationData = new CorrelationData(Id + new Date());
		// 这里可以不写死，由配置文件读取
		rabbitTemplate.convertAndSend(MQConfig.ORDER_EXCHANGE, MQConfig.ORDER_KEY, jsonObject, correlationData);
	}
    
	/**
	 * 发送延迟订单队列
	 * @param Id 唯一Id
	 * @param jsonObject json序列化消息
	 * @param delayTime 延迟时间（毫秒）
	 */
	public void sendLazyOrder(String Id, String jsonObject, Integer delayTime) {
		rabbitTemplate.setConfirmCallback(confirmCallback);
		rabbitTemplate.setReturnCallback(returnCallback);
		// Id + 时间戳 全局唯一
		CorrelationData correlationData = new CorrelationData(Id + new Date());
		// 发送消息时指定header延迟时间
		rabbitTemplate.convertAndSend(MQConfig.LAZY_ORDER_EXCHANGE, MQConfig.LAZY_ORDER_KEY, jsonObject,
				new MessagePostProcessor() {
					@Override
					public Message postProcessMessage(Message message) throws AmqpException {
						// 设置消息持久化
						message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
						message.getMessageProperties().setDelay(delayTime);
						return message;
					}
				}, correlationData);
	}
}
