package com.example.consumerserver.common.config.kafka.consumer;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import com.example.consumerserver.common.config.kafka.event.PaymentCompletedEvent;

@Configuration
public class KafkaConsumerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootStrapServers;

	@Value("${spring.kafka.consumer.max-poll-records}")
	private int maxPollRecords;

	public Map<String, Object> baseConsumerProps(String groupId){
		Map<String, Object> props = new HashMap<>();

		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);

		return props;

	}

	private ConsumerFactory<String, PaymentCompletedEvent> buildConsumerFactory(String groupId){
		JacksonJsonDeserializer<PaymentCompletedEvent> deserializer = new JacksonJsonDeserializer<>(PaymentCompletedEvent.class, false);

		deserializer.addTrustedPackages("*");

		return new DefaultKafkaConsumerFactory<>(
			baseConsumerProps(groupId),
			new StringDeserializer(),
			deserializer
		);
	}

	@Bean
	public ConsumerFactory<String, PaymentCompletedEvent> paymentNotificatoinConsumerFactory(){
		return buildConsumerFactory("payment-completed-notification");
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> paymentCompletedNotificationEventKafkaListenerContainerFactory(){
		ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

		factory.setConsumerFactory(paymentNotificatoinConsumerFactory());
		factory.setConcurrency(5);

		factory.setBatchListener(true);

		return factory;
	}

	@Bean
	public ConsumerFactory<String, PaymentCompletedEvent> paymentRankingConsumerFactory(){
		return buildConsumerFactory("payment-completed-ranking");
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> paymentCompletedRankingEventKafkaListenerContainerFactory(){
		ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

		factory.setConsumerFactory(paymentRankingConsumerFactory());
		factory.setConcurrency(1);

		factory.setBatchListener(true);

		return factory;
	}

	@Bean
	public ConsumerFactory<String, PaymentCompletedEvent> paymentDeliveryConsumerFactory(){
		return buildConsumerFactory("payment-completed-delivery");
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> paymentCompletedDeliveryEventKafkaListenerContainerFactory(){
		ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

		factory.setConsumerFactory(paymentDeliveryConsumerFactory());
		factory.setConcurrency(1);

		factory.setBatchListener(true);

		return factory;
	}

	@Bean
	public ConsumerFactory<String, PaymentCompletedEvent> paymentPointConsumerFactory(){
		return buildConsumerFactory("payment-completed-point");
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> paymentCompletedPointEventKafkaListenerContainerFactory(){
		ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

		factory.setConsumerFactory(paymentPointConsumerFactory());
		factory.setConcurrency(1);

		factory.setBatchListener(true);

		return factory;
	}


}
