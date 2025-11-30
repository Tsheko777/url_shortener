package com.uxcorp.url_shortener.dto.uri;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;

public class NewUrIDTO {

    @URL(message = "Invalid URL format")
    @NotBlank(message = "URI cannot be blank")
    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
