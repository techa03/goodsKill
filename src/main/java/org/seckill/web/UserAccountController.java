package org.seckill.web;

import javax.transaction.Transactional;

import org.seckill.entity.User;
import org.seckill.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/seckill")
public class UserAccountController {
	@Autowired
	private UserAccountService userAccountService;
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/toRegister", method = RequestMethod.GET)
	public String toRegister() {
		return "register";
	}

	@Transactional
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public @ResponseBody String register(User user) {
		userAccountService.register(user);
		return "success";
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index() {
		return "index";
	}

	@RequestMapping(value = "/toLogin", method = RequestMethod.GET)
	public String toLogin() {
		return "login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(User user) {
		String psd = user.getPassword();
		user.setPassword(DigestUtils.md5DigestAsHex(psd.getBytes()));
		userAccountService.login(user);
		return "redirect:/seckill/list";
	}

	@RequestMapping(value = "/toUploadPhoto", method = RequestMethod.GET)
	public String toUploadPhoto() {
		return "upload";
	}

	@RequestMapping(value = "/uploadPhoto", method = RequestMethod.POST)
	public String uploadPhoto(User user) {
		String psd = user.getPassword();
		System.out.println("haha");
		return "redirect:/seckill/list";
	}
}
