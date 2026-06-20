package com.fashion.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank
    private String fullName;

    @NotBlank
    @Pattern(regexp = "^[0-9]{9,11}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotNull
    private Integer provinceId;

    @NotBlank
    private String province;

    @NotNull
    private Integer districtId;

    @NotBlank
    private String district;

    @NotBlank
    private String wardCode;

    @NotBlank
    private String ward;

    @NotBlank
    private String detail;

    private Boolean isDefault = false;
}
