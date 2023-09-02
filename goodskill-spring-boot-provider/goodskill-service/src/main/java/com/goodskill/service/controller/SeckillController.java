package com.goodskill.service.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodskill.api.dto.ExposerDTO;
import com.goodskill.api.service.GoodsService;
import com.goodskill.api.service.SeckillService;
import com.goodskill.api.vo.SeckillVO;
import com.goodskill.common.info.Result;
import com.goodskill.es.api.GoodsEsService;
import com.goodskill.service.dto.ResponseDTO;
import com.goodskill.service.inner.PermissionService;
import com.goodskill.service.inner.RolePermissionService;
import com.goodskill.service.inner.UserAccountService;
import com.goodskill.service.inner.UserRoleService;
import com.goodskill.service.util.UploadFileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author techa03
 * @date 2016/7/23
 */
@Tag(name = "秒杀管理")
@Controller
@RequestMapping("/seckill")
@Validated
@Slf4j
public class SeckillController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private SeckillService seckillService;
    @Resource
    private GoodsService goodsService;
    @Resource
    private RolePermissionService rolePermissionService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private UserAccountService userAccountService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private GoodsEsService goodsEsService;
    @Resource
    private UploadFileUtil uploadFileUtil;

    @Operation(summary = "秒杀列表", description = "分页显示秒杀列表")
    @Parameters({
            @Parameter(name = "offset", description = "当前页数", required = true),
            @Parameter(name = "limit", description = "每页显示的记录数", required = true)})
    @GetMapping(value = "/list")
    @SentinelResource("seckillList")
    public String list(
            Model model,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "limit", required = false, defaultValue = "4") int limit,
            @RequestParam(name = "goodsName", required = false) String goodsName) {
        Page<SeckillVO> pageInfo = seckillService.getSeckillList(offset, limit, goodsName);
        long totalNum = pageInfo.getTotal();
        model.addAttribute("list", pageInfo.getRecords());
        model.addAttribute("totalNum", totalNum);
        model.addAttribute("pageNum", pageInfo.getPages());
        model.addAttribute("limit", limit);
        return "list";
    }

    @GetMapping(value = "/{seckillId}/detail")
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        SeckillVO seckillInfo = null;
        seckillInfo = seckillService.findById(seckillId);
        if (seckillInfo == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckillInfo", seckillInfo);
        return "detail";

    }

    @PostMapping(value = "/{seckillId}/exposer", produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public Result<ExposerDTO> exposer(@PathVariable("seckillId") Long seckillId) {
        Result<ExposerDTO> result;
        try {
            ExposerDTO exposerDTO = seckillService.exportSeckillUrl(seckillId);
            result = Result.ok(exposerDTO);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = Result.fail(e.getMessage());
        }
        return result;
    }


    @PostMapping(value = "/create")
    public String addSeckill(SeckillVO seckill) {
        seckillService.save(seckill);
        return null;
    }

    @GetMapping(value = "/new")
    public String toAddSeckillPage() {
        return "seckill/addSeckill";
    }

    @GetMapping(value = "/{seckillId}/delete")
    public String delete(@PathVariable("seckillId") Long seckillId) {
        seckillService.removeBySeckillId(seckillId);
        return null;
    }

    @Transactional
    @GetMapping(value = "/{seckillId}/edit")
    public String edit(Model model, @PathVariable("seckillId") Long seckillId) {
        model.addAttribute("seckillInfo", seckillService.getInfoById(seckillId));
        return "seckill/edit";
    }

    @Transactional
    @PostMapping(value = "/{seckillId}/update")
    public String update(SeckillVO seckill) {
        seckillService.saveOrUpdateSeckill(seckill);
        return null;
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


    /**
     * 上传商品图片
     *
     * @param file 图片源文件
     * @return String
     */
    @SneakyThrows
    @Transactional
    @RequestMapping(value = "/upload/{seckillId}/create", method = RequestMethod.POST)
    public String uploadPhoto(@RequestParam("file") MultipartFile file, @RequestParam("seckillId") Long seckillId) {
        SeckillVO seckill = seckillService.findById(seckillId);
        String url = uploadFileUtil.uploadFile(file);
        goodsService.uploadGoodsPhoto(seckill.getGoodsId(), url);
        return url;
    }

    /**
     * 根据商品名称检索商品
     *
     * @param goodsName 商品名称，模糊匹配
     * @return 包含商品名称和高亮显示的商品名称html内容
     */
    @GetMapping(value = "/goods/search/{goodsName}", produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDTO searchGoods(@PathVariable("goodsName") String goodsName) {
        List goodsList = goodsEsService.searchWithNameByPage(goodsName);
        ResponseDTO responseDto = ResponseDTO.ok();
        responseDto.setData(goodsList.toArray());
        return responseDto;
    }

}
