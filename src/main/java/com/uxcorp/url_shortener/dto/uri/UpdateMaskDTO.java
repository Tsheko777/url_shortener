package com.uxcorp.url_shortener.dto.uri;

import jakarta.validation.constraints.NotBlank;

public class UpdateMaskDTO {
    @NotBlank(message = "Mask cannot be blank")
    private String mask;

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }
}
