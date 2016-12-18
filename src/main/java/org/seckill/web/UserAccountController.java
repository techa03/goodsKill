package org.seckill.web;

import org.seckill.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/seckill")
public class UserAccountController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/toRegister", method = RequestMethod.GET)
	public String toRegister() {
		return "register";
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(User user) {
		logger.info(user.toString());
		return "index";
	}

}
