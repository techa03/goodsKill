package com.goodskill.web.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodskill.api.dto.ExposerDTO;
import com.goodskill.api.dto.SeckillExecutionDTO;
import com.goodskill.api.dto.SeckillResponseDTO;
import com.goodskill.api.service.*;
import com.goodskill.api.vo.SeckillVO;
import com.goodskill.common.enums.SeckillStatEnum;
import com.goodskill.common.exception.RepeatKillException;
import com.goodskill.common.exception.SeckillCloseException;
import com.goodskill.common.info.Result;
import com.goodskill.entity.*;
import com.goodskill.es.api.GoodsEsService;
import com.goodskill.web.dto.ResponseDTO;
import com.goodskill.web.util.HttpUrlUtil;
import com.goodskill.web.util.UploadFileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

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
    @DubboReference
    private SeckillService seckillService;
    @DubboReference
    private GoodsService goodsService;
    @DubboReference
    private RolePermissionService rolePermissionService;
    @DubboReference
    private UserRoleService userRoleService;
    @DubboReference
    private UserAccountService userAccountService;
    @DubboReference
    private PermissionService permissionService;
    @Resource
    private GoodsEsService goodsEsService;
    @Autowired
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
        Seckill seckillInfo = null;
        seckillInfo = seckillService.getById(seckillId);
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

    @PostMapping(value = "/{seckillId}/{md5}/execution", produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public Result<SeckillExecutionDTO> execute(@PathVariable("seckillId") Long seckillId,
                                               @PathVariable("md5") String md5, @CookieValue(value = "killPhone", required = false) String phone) {
        Result<SeckillExecutionDTO> result;
        if (phone == null) {
            result = Result.fail("未注册");
            return result;
        }
        try {
            SeckillExecutionDTO execution = seckillService.executeSeckill(seckillId, phone, md5);
            result = Result.ok(execution);
        } catch (RepeatKillException e) {
            logger.info(e.getMessage(), e);
            SeckillExecutionDTO execution = new SeckillExecutionDTO(seckillId, SeckillStatEnum.REPEAT_KILL.getStateInfo());
            result = Result.fail(execution, null);
        } catch (SeckillCloseException e) {
            logger.info(e.getMessage(), e);
            SeckillExecutionDTO execution = new SeckillExecutionDTO(seckillId, SeckillStatEnum.END.getStateInfo());
            result = Result.fail(execution, null);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            SeckillExecutionDTO execution = new SeckillExecutionDTO(seckillId, SeckillStatEnum.INNER_ERROR.getStateInfo());
            result = Result.fail(execution, null);
        }
        return result;
    }

    @GetMapping(value = "/time/now")
    @ResponseBody
    public Result<Long> time() {
        Date now = new Date();
        return Result.ok(now.getTime());
    }

    @PostMapping(value = "/create")
    public String addSeckill(Seckill seckill) {
        seckillService.save(seckill);
        return HttpUrlUtil.replaceRedirectUrl("redirect:/seckill/list");
    }

    @GetMapping(value = "/new")
    public String toAddSeckillPage() {
        return "seckill/addSeckill";
    }

    @GetMapping(value = "/{seckillId}/delete")
    public String delete(@PathVariable("seckillId") Long seckillId) {
        seckillService.removeById(seckillId);
        return HttpUrlUtil.replaceRedirectUrl("redirect:/seckill/list");
    }

    @Transactional
    @GetMapping(value = "/{seckillId}/edit")
    public String edit(Model model, @PathVariable("seckillId") Long seckillId) {
        model.addAttribute("seckillInfo", seckillService.getInfoById(seckillId));
        return "seckill/edit";
    }

    @Transactional
    @PostMapping(value = "/{seckillId}/update")
    public String update(Seckill seckill) {
        seckillService.saveOrUpdate(seckill);
        return HttpUrlUtil.replaceRedirectUrl("redirect:/seckill/list");
    }

    /**
     * 显示二维码
     *
     * @param filename
     * @param response
     * @throws IOException
     */
    @GetMapping(value = "/Qrcode/{QRfilePath}")
    @Deprecated
    public void showQRcode(@PathVariable("QRfilePath") String filename, HttpServletResponse response) throws IOException {
        response.setContentType("img/*");
        SeckillResponseDTO responseDto = seckillService.getQrcode(filename);
        try (OutputStream os = response.getOutputStream()) {
            os.write(responseDto.getData());
        } catch (FileNotFoundException e) {
            logger.error("the error is :", e);
        }
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

    @GetMapping(value = "/uploadPhoto/{seckillId}")
    public String toUploadPhoto(@PathVariable("seckillId") Long seckillId) {
        return HttpUrlUtil.replaceRedirectUrl("redirect:/seckill/upload/" + seckillId);
    }

    @GetMapping(value = "/upload/{seckillId}")
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
    @SneakyThrows
    @Transactional
    @RequestMapping(value = "/upload/{seckillId}/create", method = RequestMethod.POST)
    public String uploadPhoto(@RequestParam("file") MultipartFile file, @RequestParam("seckillId") Long seckillId) {
        Seckill seckill = seckillService.getById(seckillId);
        String url = uploadFileUtil.uploadFile(file);
        goodsService.uploadGoodsPhoto(seckill.getGoodsId(), url);
        return HttpUrlUtil.replaceRedirectUrl("redirect:/seckill/list");
    }

    @RequestMapping(value = "/permission/list", method = RequestMethod.GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDTO getPermissionList() {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getSession().getAttribute("user");
        user = userAccountService.findByUserAccount(user.getAccount());
        List<UserRole> userRoleList = userRoleService.list(user.getId());
        RolePermission rolePermissionExample = new RolePermission();
        Set<Permission> set = new HashSet<>();
        for (UserRole userRole : userRoleList) {
            rolePermissionExample.setRoleId(userRole.getRoleId());
            List<RolePermission> rolePermissionList = rolePermissionService.list(userRole.getRoleId());
            for (RolePermission rolePermission : rolePermissionList) {
                set.add(permissionService.getById(rolePermission.getPermissionId()));
            }
        }
        ResponseDTO<Permission> responseDto = new ResponseDTO<>();
        List<Permission> permissions = set.stream().sorted(Comparator.comparing(Permission::getPermissionId).reversed()).collect(Collectors.toList());
        logger.info(user.toString());
        responseDto.setData(permissions.toArray(new Permission[permissions.size()]));
        return responseDto;
    }

    @RequestMapping(value = "/permission/diretorylist", method = RequestMethod.GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDTO getDirectoryPermissionList() {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getSession().getAttribute("user");
        user = userAccountService.findByUserAccount(user.getAccount());
        UserRole example = new UserRole();
        example.setUserId(user.getId());
        List<UserRole> userRoleList = userRoleService.list(user.getId());
        RolePermission rolePermissionExample = new RolePermission();
        Set<Permission> set = new HashSet<>();
        for (UserRole userRole : userRoleList) {
            rolePermissionExample.setRoleId(userRole.getRoleId());
            List<RolePermission> rolePermissionList = rolePermissionService.list(userRole.getRoleId());
            for (RolePermission rolePermission : rolePermissionList) {
                Permission permission = permissionService.getById(rolePermission.getPermissionId());
                if ("Y".equals(permission.getIsDir())) {
                    set.add(permission);
                }
            }
        }
        ResponseDTO<Permission> responseDto = new ResponseDTO<>();
        List<Permission> permissions = new ArrayList<>(set);
        logger.info(user.toString());
        responseDto.setData(permissions.toArray(new Permission[permissions.size()]));
        return responseDto;
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
