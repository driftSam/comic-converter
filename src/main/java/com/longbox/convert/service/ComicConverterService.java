package com.longbox.convert.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ComicConverterService {

	@Autowired
	RabbitTemplate rabbitTemplate;

	public void convert(String rarComic) {

	}
}
