package org.seckill.web;

import org.seckill.entity.Seckill;
import org.seckill.entity.User;
import org.seckill.exception.SeckillException;
import org.seckill.service.GoodsService;
import org.seckill.service.SeckillService;
import org.seckill.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/seckill")
public class UserAccountController {
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SeckillService seckillService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional
    @RequestMapping(value = "/register/create", method = RequestMethod.POST)
    @ResponseBody
    public String register(User user) {
        userAccountService.register(user);
        return "success";
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String toLogin() {
        return "login";
    }


    @RequestMapping(value = "/login/session", method = RequestMethod.POST)
    public String login(User user) {
        String psd = user.getPassword();
        user.setPassword(DigestUtils.md5DigestAsHex(psd.getBytes()));
//        userAccountService.sendMsgForLogin(destination,user);
        try {
            userAccountService.login(user);
        } catch (SeckillException e) {
            return "login";
        }
        return "redirect:/seckill/list";
    }

    @RequestMapping(value = "/uploadPhoto/{seckillId}", method = RequestMethod.GET)
    public String toUploadPhoto(@PathVariable("seckillId") Long seckillId, Model model) {
        return "redirect:/seckill/upload/" + seckillId;
    }

    @RequestMapping(value = "/upload/{seckillId}", method = RequestMethod.GET)
    public String upload(@PathVariable("seckillId") Long seckillId, Model model) {
        model.addAttribute("seckillId", seckillId);
        return "upload";
    }


    /**
     * 上传商品图片
     *
     * @param file 图片源文件
     * @return String
     */
    @Transactional
    @RequestMapping(value = "/upload/{seckillId}/create", method = RequestMethod.POST)
    public String uploadPhoto(@RequestParam("file") CommonsMultipartFile file, @RequestParam("seckillId") Long seckillId) {
        Seckill seckill = seckillService.selectById(seckillId);
        try {
            goodsService.uploadGoodsPhoto(seckill.getGoodsId(), file.getBytes());
        } catch (IOException e) {
            logger.error("上传文件失败："+e);
        }
        return "redirect:/seckill/list";
    }

    /**
     * 根据秒杀id加载对应图片
     *
     * @param seckillId 秒杀id
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/img/seckill/{seckillId}", method = RequestMethod.GET)
    public void loadImg(@PathVariable("seckillId") Long seckillId, HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
        Seckill seckill = seckillService.selectById(seckillId);
        byte[] goodsPhotoImage = goodsService.getPhotoImage(seckill.getGoodsId());
        response.setContentType("img/*");
        OutputStream os = response.getOutputStream();
        os.write(goodsPhotoImage);
        os.flush();
        os.close();
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register() {
        return "register";
    }

    @RequestMapping(value = "/user/{phoneNum}/phoneCode", method = RequestMethod.POST)
    @ResponseBody
    public void userPhoneCode(@PathVariable("phoneNum") Long phoneNum) {

    }
}
