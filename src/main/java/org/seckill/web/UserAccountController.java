package org.seckill.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/seckill")
public class UserAccountController {
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public void login(String userName, String password) {

	}

}
