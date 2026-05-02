package com.goodskill.auth.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "C端用户信息更新DTO")
@Data
public class CustomerUserInfoUpdateDTO {

    @Schema(description = "邮箱地址")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String emailAddr;
}
