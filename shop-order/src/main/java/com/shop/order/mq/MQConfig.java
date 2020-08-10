package com.shop.order.mq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
	public static final String ORDER_EXCHANGE = "ORDER-EXCHANGE-1";
	public static final String ORDER_QUEUE = "ORDER-QUEUE-1";
	public static final String ORDER_KEY = "order.#";
	public static final String LAZY_ORDER_EXCHANGE = "LAZY-ORDER-EXCHANGE-1";
	public static final String LAZY_ORDER_QUEUE = "LAZY-ORDER-QUEUE-1";
    public static final String LAZY_ORDER_KEY = "lazyOrder.#";
    
    @Autowired
    OrderReceiver orderReceiver;
	/*
	 * @Bean public ConnectionFactory connectionFactory() { //定义连接工厂
	 * ConnectionFactory factory = new ConnectionFactory(); //设置服务地址
	 * factory.setHost("localhost"); //端口 factory.setPort(5672);
	 * //设置账号信息，用户名、密码、vhost factory.setVirtualHost("/");
	 * factory.setUsername("feng"); factory.setPassword("feng"); return factory; }
	 */
	
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
    
	/*
	 * @Bean public SimpleMessageListenerContainer simpleMessageListenerContainer()
	 * { SimpleMessageListenerContainer container = new
	 * SimpleMessageListenerContainer(connectionFactory); // 设置手动确认队列
	 * container.setQueueNames(ORDER_QUEUE, LAZY_ORDER_QUEUE); // 设置监听接收类
	 * container.setMessageListener(orderReceiver); return container; }
	 */
    
	@Bean("orderExchange")
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE, true, false);
    }

    @Bean("orderQueue")
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true);
    }
    
    @Bean("lazyOrderExchange")
    public TopicExchange lazyOrderExchange() {
    	TopicExchange exchange = new TopicExchange(LAZY_ORDER_EXCHANGE, true, false);
    	exchange.setDelayed(true);
    	return exchange;
    }
    
    @Bean("lazyOrderQueue")
    public Queue lazyOrderQueue() {
    	return new Queue(LAZY_ORDER_QUEUE, true);
    }
    
    // 绑定可以不写死，由配置文件读取
    @Bean
    public Binding orderBinding(){
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(ORDER_KEY);
    }
    
    @Bean
    public Binding lazyOrderBinding(){
        return BindingBuilder.bind(lazyOrderQueue()).to(lazyOrderExchange()).with(LAZY_ORDER_KEY);
    }
}
