package com.shop.order.mq;

import java.util.List;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.shop.common.result.CommonResult;
import com.shop.common.utils.JacksonUtil;
import com.shop.order.controller.OrderItemController;
import com.shop.order.model.Order;
import com.shop.order.model.OrderItem;

import lombok.extern.slf4j.Slf4j;

/**
 * MQ消息接受者
 */
@Service
@Slf4j
public class LazyOrderReceiver implements ChannelAwareMessageListener {
	@Autowired
	OrderItemController orderItemController;
	
	@RabbitListener(
	        bindings = @QueueBinding(
	                value = @Queue(value = MQConfig.LAZY_ORDER_QUEUE, durable = "true"),
	                exchange = @Exchange(value = MQConfig.LAZY_ORDER_EXCHANGE, type = "topic", durable = "true", ignoreDeclarationExceptions = "true"),
	                key = MQConfig.LAZY_ORDER_KEY
	        )
	)
	@RabbitHandler
	@Override
    public void onMessage(Message message, Channel channel) throws Exception {
		log.debug("消费的主题消息来自：" + message.getMessageProperties().getConsumerQueue());
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        
        try {
        	Jackson2JsonMessageConverter a = new Jackson2JsonMessageConverter();
        	String jsonObject = (String) a.fromMessage(message);
        	Order order = JacksonUtil.parseObject(jsonObject, Order.class);
        	List<OrderItem> orderItemList = order.getOrderItemList();
        	for (OrderItem orderItem : orderItemList) {
        		CommonResult result = orderItemController.removeOrderItemById(orderItem.getCustomerId(), orderItem.getOrderItemId());
	        	if (result.getCode() != 200) {
	        		if (result.getData() != null) {
	        			throw new Exception(result.getData().toString());
	        		} else {
	        			throw new Exception(result.getMessage());
	        		}
	        	}
        	}
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            channel.basicReject(deliveryTag, false);
            e.printStackTrace();
        }
    }	
}
