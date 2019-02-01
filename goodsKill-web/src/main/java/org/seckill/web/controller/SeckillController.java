package org.seckill.web.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.seckill.api.dto.Exposer;
import org.seckill.api.dto.SeckillExecution;
import org.seckill.api.dto.SeckillInfo;
import org.seckill.api.dto.SeckillResult;
import org.seckill.api.enums.SeckillStatEnum;
import org.seckill.api.exception.RepeatKillException;
import org.seckill.api.exception.SeckillCloseException;
import org.seckill.api.service.*;
import org.seckill.entity.*;
import org.seckill.util.common.util.PropertiesUtil;
import org.seckill.web.dto.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * Created by heng on 2016/7/23.
 */
@Api(tags = "秒杀管理")
@Controller
@RequestMapping("/seckill")
public class SeckillController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RolePermissionService rolePermissionService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private PermissionService permissionService;


    @ApiOperation(value = "秒杀列表", notes = "分页显示秒杀列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "model", value = "model对象", required = true, dataType = "Model"),
            @ApiImplicitParam(name = "offset", value = "当前页数", required = true, dataType = "int"),
            @ApiImplicitParam(name = "limit", value = "每页显示的记录数", required = true, dataType = "int")})
    @GetMapping(value = "/list")
    public String list(Model model, @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                       @RequestParam(name = "limit", required = false, defaultValue = "4") int limit) {
        PageInfo<Seckill> pageInfo = seckillService.getSeckillList(offset, limit);
        long totalNum = pageInfo.getTotal();
        long pageNum = (totalNum % limit == 0) ? totalNum / limit : totalNum / limit + 1;
        model.addAttribute("list", pageInfo.getList());
        model.addAttribute("totalNum", totalNum);
        model.addAttribute("pageNum", pageNum);
        return "list";
    }

    @GetMapping(value = "/{seckillId}/detail")
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        SeckillInfo seckillInfo = null;
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
    public SeckillResult<Long> time() {
        Date now = new Date();
        return new SeckillResult(true, now.getTime());
    }

    @PostMapping(value = "/create")
    public String addSeckill(Seckill seckill) {
        seckillService.addSeckill(seckill);
        return "redirect:list";
    }

    @GetMapping(value = "/new")
    public String toAddSeckillPage() {
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
        model.addAttribute("seckillInfo", seckillService.getById(seckillId));
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
        try (FileInputStream fi = new FileInputStream(new File(PropertiesUtil.getProperties("QRCODE_IMAGE_DIR") + "\\" + QRfilePath + ".png"));
             OutputStream os = response.getOutputStream()) {
            int b;
            while ((b = fi.read()) != -1) {
                os.write(b);
            }
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
            logger.error("上传文件失败：" + e);
        }
        return "redirect:/seckill/list";
    }


    @RequestMapping(value = "/user/{phoneNum}/phoneCode", method = RequestMethod.POST)
    @ResponseBody
    public void userPhoneCode(@PathVariable("phoneNum") Long phoneNum) {

    }

    @RequestMapping(value = "/permission/list", method = RequestMethod.GET)
    @ResponseBody
    public ResponseDto getPermissionList() {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getSession().getAttribute("user");
        user = userAccountService.findByUserAccount(user.getAccount());
        UserRoleExample example = new UserRoleExample();
        example.createCriteria().andUserIdEqualTo(user.getId());
        List<UserRole> userRoleList = userRoleService.selectByExample(example);
        RolePermissionExample rolePermissionExample = new RolePermissionExample();
        Set<Permission> set = new HashSet<>();
        for (UserRole userRole : userRoleList) {
            rolePermissionExample.clear();
            rolePermissionExample.createCriteria().andRoleIdEqualTo(userRole.getRoleId());
            List<RolePermission> rolePermissionList = rolePermissionService.selectByExample(rolePermissionExample);
            for (RolePermission rolePermission : rolePermissionList) {
                set.add(permissionService.selectByPrimaryKey(rolePermission.getPermissionId()));
            }
        }
        ResponseDto<Permission> responseDto = new ResponseDto<>();
        List<Permission> permissions = new ArrayList<>(set);
        Collections.sort(permissions);
        logger.info(user.toString());
        responseDto.setData(permissions.toArray(new Permission[permissions.size()]));
        return responseDto;
    }

    @RequestMapping(value = "/permission/diretorylist", method = RequestMethod.GET)
    @ResponseBody
    public ResponseDto getDirectoryPermissionList() {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getSession().getAttribute("user");
        user = userAccountService.findByUserAccount(user.getAccount());
        UserRoleExample example = new UserRoleExample();
        example.createCriteria().andUserIdEqualTo(user.getId());
        List<UserRole> userRoleList = userRoleService.selectByExample(example);
        RolePermissionExample rolePermissionExample = new RolePermissionExample();
        Set<Permission> set = new HashSet<>();
        for (UserRole userRole : userRoleList) {
            rolePermissionExample.clear();
            rolePermissionExample.createCriteria().andRoleIdEqualTo(userRole.getRoleId());
            List<RolePermission> rolePermissionList = rolePermissionService.selectByExample(rolePermissionExample);
            for (RolePermission rolePermission : rolePermissionList) {
                Permission permission = permissionService.selectByPrimaryKey(rolePermission.getPermissionId());
                if ("Y".equals(permission.getIsDir())) {
                    set.add(permission);
                }
            }
        }
        ResponseDto<Permission> responseDto = new ResponseDto<>();
        List<Permission> permissions = new ArrayList<>(set);
        Collections.sort(permissions);
        logger.info(user.toString());
        responseDto.setData(permissions.toArray(new Permission[permissions.size()]));
        return responseDto;
    }

    @GetMapping(value = "/chatroom")
    public String chatroom() {
        return "seckill/chatroom";
    }

}
