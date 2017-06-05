package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillInfo;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

/**
 * Created by heng on 2016/7/23.
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;

    @GetMapping(value = "/list")
    public String list(Model model) {
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list", list);
        return "list";
    }

    @GetMapping(value = "/{seckillId}/detail")
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        SeckillInfo seckillInfo = null;
        try {
            seckillInfo = seckillService.getById(seckillId);
        } catch (InvocationTargetException e) {
            logger.error("the error is :", e);
        } catch (IllegalAccessException e) {
            logger.error("the error is :", e);
        }
        if (seckillInfo == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckillInfo", seckillInfo);
        return "detail";

    }

    @PostMapping(value = "/{seckillId}/exposer", produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult(false, e.getMessage());
        }
        return result;
    }

    @PostMapping(value = "/{seckillId}/{md5}/execution", produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5, @CookieValue(value = "killPhone", required = false) String phone) {
        SeckillResult<SeckillExecution> seckillResult;
        if (phone == null) {
            seckillResult = new SeckillResult(false, "未注册");
            return seckillResult;
        }
        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
            seckillResult = new SeckillResult<SeckillExecution>(true, execution);
        } catch (RepeatKillException e) {
            logger.info(e.getMessage(), e);
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            seckillResult = new SeckillResult(false, execution);
        } catch (SeckillCloseException e) {
            logger.info(e.getMessage(), e);
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
            seckillResult = new SeckillResult(false, execution);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            seckillResult = new SeckillResult(false, execution);
        }
        return seckillResult;
    }

    @GetMapping(value = "/time/now")
    public SeckillResult<Long> time() {
        Date now = new Date();
        return new SeckillResult(true, now.getTime());
    }

    @GetMapping(value = "/toAddGoods")
    public String toAddGoods() {
        return "redirect:/goods/addGoods";
    }

    @PostMapping(value = "/addSeckill")
    public String addSeckill(Seckill seckill) {
        seckillService.addSeckill(seckill);
        return "redirect:list";
    }

    @GetMapping(value = "/toAddSeckill")
    public String toAddSeckill() {
        return "seckill/addSeckill";
    }

    @GetMapping(value = "/{seckillId}/delete")
    public String delete(@PathVariable("seckillId") Long seckillId) {
        seckillService.deleteSeckill(seckillId);
        return "redirect:/seckill/list";
    }

    @Transactional
    @GetMapping(value = "/{seckillId}/edit")
    public String edit(Model model, @PathVariable("seckillId") Long seckillId) {
        try {
            model.addAttribute("seckillInfo", seckillService.getById(seckillId));
        } catch (InvocationTargetException e) {
            logger.error("the error is :", e);
        } catch (IllegalAccessException e) {
            logger.error("the error is :", e);
        }
        return "seckill/edit";
    }

    @Transactional
    @PostMapping(value = "/{seckillId}/update")
    public String update(Seckill seckill) {
        seckillService.updateSeckill(seckill);
        return "redirect:/seckill/list";
    }

    @GetMapping(value = "/Qrcode/{QRfilePath}")
    public void showQRcode(@PathVariable("QRfilePath") String QRfilePath, HttpServletResponse response) throws IOException {
        response.setContentType("img/*");
        OutputStream os = response.getOutputStream();
        FileInputStream fi = new FileInputStream(new File("C:/Users/heng/Pictures/" + QRfilePath + ".png"));
        int b;
        while ((b = fi.read()) != -1) {
            os.write(b);
        }
        os.flush();
        os.close();
        fi.close();
    }

    /**
     * 进入支付宝二维码支付页面
     *
     * @param QRfilePath
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/pay/Qrcode/{QRfilePath}", method = RequestMethod.GET)
    public String payTransaction(@PathVariable("QRfilePath") String QRfilePath, Model model) throws IOException {
        model.addAttribute("QRfilePath", QRfilePath);
        return "seckill/payByQrcode";
    }

}
