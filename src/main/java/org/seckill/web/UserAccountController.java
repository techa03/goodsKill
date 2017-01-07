package org.seckill.web;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.seckill.dao.SeckillDao;
import org.seckill.entity.Seckill;
import org.seckill.entity.User;
import org.seckill.exception.HengException;
import org.seckill.service.GoodsService;
import org.seckill.service.UserAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;

@Controller
@RequestMapping("/seckill")
public class UserAccountController {
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private GoodsService goodsService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/toRegister", method = RequestMethod.GET)
    public String toRegister() {
        return "register";
    }

    @Transactional
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public
    @ResponseBody
    String register(User user) {
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

    @RequestMapping(value = "/toUploadPhoto/{seckillId}", method = RequestMethod.GET)
    public String toUploadPhoto(@PathVariable("seckillId")Long seckillId, Model model) {
        return "redirect:/seckill/upload/"+seckillId;
    }

    @RequestMapping(value = "/upload/{seckillId}", method = RequestMethod.GET)
    public String upload(@PathVariable("seckillId")Long seckillId, Model model) {
        model.addAttribute("seckillId",seckillId);
        return "upload";
    }


    /**
     * 上传商品图片
     * @param file 图片源文件
     * @return String
     */
    @Transactional
    @RequestMapping(value = "/upload/{seckillId}", method = RequestMethod.POST)
    public String uploadPhoto(@RequestParam("file") CommonsMultipartFile file, @RequestParam("seckillId") Long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        goodsService.uploadGoodsPhoto(file, seckill.getGoodsId());
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
        String goodsUrl=goodsService.getPhotoUrl(seckill.getGoodsId());
        response.setContentType("img/*");
        OutputStream os = response.getOutputStream();
        FileInputStream fi = new FileInputStream(new File(goodsUrl));
        int b = 0;
        while ((b = fi.read()) != -1) {
            os.write(b);
        }
        os.flush();
        os.close();
        fi.close();
    }
}
