package org.seckill.web;

import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.seckill.entity.User;
import org.seckill.exception.CommonException;
import org.seckill.service.GoodsService;
import org.seckill.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.jms.Destination;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/seckill")
public class UserAccountController {
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    @Qualifier(value="queueDestination")
    private Destination destination;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/toRegister", method = RequestMethod.GET)
    public String toRegister() {
        return "register";
    }

    @Transactional
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public String register(User user) {
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
        userAccountService.sendMsgForLogin(destination,user);
        userAccountService.login(user);
        return "redirect:/seckill/list";
    }

    @RequestMapping(value = "/toUploadPhoto/{seckillId}", method = RequestMethod.GET)
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
    @RequestMapping(value = "/upload/{seckillId}", method = RequestMethod.POST)
    public String uploadPhoto(@RequestParam("file") CommonsMultipartFile file, @RequestParam("seckillId") Long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        try {
            goodsService.uploadGoodsPhoto(file, seckill.getGoodsId());
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
            throw new CommonException("上传商品照片出现错误，请检查");
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
        Seckill seckill = seckillDao.queryById(seckillId);
        String goodsUrl = goodsService.getPhotoUrl(seckill.getGoodsId());
        response.setContentType("img/*");
        OutputStream os = response.getOutputStream();
        FileInputStream fi = new FileInputStream(new File(goodsUrl));
        int b;
        while ((b = fi.read()) != -1) {
            os.write(b);
        }
        os.flush();
        os.close();
        fi.close();
    }

    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String register(){
        return "register";
    }

    @RequestMapping(value = "/user/{phoneNum}/phoneCode",method = RequestMethod.POST)
    @ResponseBody
    public void userPhoneCode(@PathVariable("phoneNum")Long phoneNum){

    }
}
