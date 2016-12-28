package org.seckill.service;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class ContextTest {
	public static void main(String[] args) {
		WebApplicationContext context = new XmlWebApplicationContext();
	}
}
