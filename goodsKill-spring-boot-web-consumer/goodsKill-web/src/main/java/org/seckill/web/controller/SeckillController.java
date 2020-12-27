package org.seckill.web.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.github.pagehelper.PageInfo;
import com.goodskill.es.api.GoodsEsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.seckill.api.dto.Exposer;
import org.seckill.api.dto.SeckillExecution;
import org.seckill.api.dto.SeckillResponseDto;
import org.seckill.api.dto.SeckillResult;
import org.seckill.api.enums.SeckillStatEnum;
import org.seckill.api.exception.RepeatKillException;
import org.seckill.api.exception.SeckillCloseException;
import org.seckill.api.service.*;
import org.seckill.entity.*;
import org.seckill.web.dto.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author techa03
 * @date 2016/7/23
 */
@Api(tags = "秒杀管理")
@Controller
@RequestMapping("/seckill")
@Validated
public class SeckillController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Reference
    private SeckillService seckillService;
    @Reference
    private GoodsService goodsService;
    @Reference
    private RolePermissionService rolePermissionService;
    @Reference
    private UserRoleService userRoleService;
    @Reference
    private UserAccountService userAccountService;
    @Reference
    private PermissionService permissionService;
    @Resource
    private GoodsEsService goodsEsService;

    @ApiOperation(value = "秒杀列表", notes = "分页显示秒杀列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "offset", value = "当前页数", required = true, dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "每页显示的记录数", required = true, dataType = "int")})
    @GetMapping(value = "/list")
    @SentinelResource("seckillList")
    public String list(
            Model model,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(name = "limit", required = false, defaultValue = "4") int limit,
            @RequestParam(name = "goodsName", required = false) String goodsName) {
        PageInfo<Seckill> pageInfo = seckillService.getSeckillList(offset, limit, goodsName);
        long totalNum = pageInfo.getTotal();
        long pageNum = (totalNum % limit == 0) ? totalNum / limit : totalNum / limit + 1;
        model.addAttribute("list", pageInfo.getList());
        model.addAttribute("totalNum", totalNum);
        model.addAttribute("pageNum", pageNum);
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
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now = new Date();
        return new SeckillResult(true, now.getTime());
    }

    @PostMapping(value = "/create")
    public String addSeckill(@RequestBody @Valid Seckill seckill) {
        seckillService.save(seckill);
        return "redirect:list";
    }

    @GetMapping(value = "/new")
    public String toAddSeckillPage() {
        return "seckill/addSeckill";
    }

    @GetMapping(value = "/{seckillId}/delete")
    public String delete(@PathVariable("seckillId") Long seckillId) {
        seckillService.removeById(seckillId);
        return "redirect:/seckill/list";
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
        return "redirect:/seckill/list";
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
        SeckillResponseDto responseDto = seckillService.getQrcode(filename);
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

    /**
     * 根据秒杀id加载对应图片
     *
     * @param seckillId 秒杀id
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/img/seckill/{seckillId}", method = RequestMethod.GET)
    public void loadImg(@PathVariable("seckillId") Long seckillId,
                        HttpServletResponse response) throws IOException {
        Seckill seckill = seckillService.getById(seckillId);
        byte[] goodsPhotoImage = goodsService.getById(seckill.getGoodsId()).getPhotoImage();
        response.setContentType("img/*");
        OutputStream os = response.getOutputStream();
        os.write(goodsPhotoImage);
        os.flush();
        os.close();
    }

    @GetMapping(value = "/uploadPhoto/{seckillId}")
    public String toUploadPhoto(@PathVariable("seckillId") Long seckillId) {
        return "redirect:/seckill/upload/" + seckillId;
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
    @Transactional
    @RequestMapping(value = "/upload/{seckillId}/create", method = RequestMethod.POST)
    public String uploadPhoto(@RequestParam("file") CommonsMultipartFile file, @RequestParam("seckillId") Long seckillId) {
        Seckill seckill = seckillService.getById(seckillId);
        goodsService.uploadGoodsPhoto(seckill.getGoodsId(), file.getBytes());
        return "redirect:/seckill/list";
    }


    @RequestMapping(value = "/user/{phoneNum}/phoneCode", method = RequestMethod.POST, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public void userPhoneCode(@PathVariable("phoneNum") Long phoneNum) {

    }

    @RequestMapping(value = "/permission/list", method = RequestMethod.GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDto getPermissionList() {
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
        ResponseDto<Permission> responseDto = new ResponseDto<>();
        List<Permission> permissions = set.stream().sorted(Comparator.comparing(Permission::getPermissionId).reversed()).collect(Collectors.toList());
        logger.info(user.toString());
        responseDto.setData(permissions.toArray(new Permission[permissions.size()]));
        return responseDto;
    }

    @RequestMapping(value = "/permission/diretorylist", method = RequestMethod.GET, produces = {
            "application/json;charset=UTF-8"})
    @ResponseBody
    public ResponseDto getDirectoryPermissionList() {
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
        ResponseDto<Permission> responseDto = new ResponseDto<>();
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
    public ResponseDto searchGoods(@PathVariable("goodsName") String goodsName) {
        List goodsList = goodsEsService.searchWithNameByPage(goodsName);
        ResponseDto responseDto = ResponseDto.ok();
        responseDto.setData(goodsList.toArray());
        return responseDto;
    }

}
