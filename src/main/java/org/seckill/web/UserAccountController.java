package org.seckill.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.seckill.entity.User;
import org.seckill.exception.HengException;
import org.seckill.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

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

	@Transactional
	@RequestMapping(value = "/uploadPhoto", method = RequestMethod.POST)
	public String uploadPhoto(@RequestParam("file") CommonsMultipartFile file, HttpServletRequest request) {
		System.out.println("haha");
		System.out.println(file.getOriginalFilename());
		try {
			FileOutputStream fos = new FileOutputStream("G:/java学习/" + file.getOriginalFilename());
			InputStream is = file.getInputStream();
			int b = 0;
			while ((b = is.read()) != -1) {
				fos.write(b);
			}
			fos.flush();
			fos.close();
			is.close();
		} catch (IOException e) {
			throw new HengException("上传文件异常");
		}
		return "redirect:/seckill/list";
	}

	@RequestMapping(value = "/img/seckill/{seckillId}", method = RequestMethod.GET)
	public void loadImg(@PathVariable("seckillId") Long seckillId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setContentType("img/*");
		OutputStream os = response.getOutputStream();
		FileInputStream fi = new FileInputStream(new File("G:/java学习/4096383O52964Q8P9DHJ1ZD4AA72LMFC.jpg"));
		int b = 0;
		while ((b = fi.read()) != -1) {
			os.write(b);
		}
		os.flush();
		os.close();
		fi.close();
	}
}
