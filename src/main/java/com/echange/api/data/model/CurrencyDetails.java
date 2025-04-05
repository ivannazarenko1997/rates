package com.echange.api.data.model;


public   class CurrencyDetails {
    private String description;

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CurrencyDetails(String description, String code) {
        this.description = description;
        this.code = code;
    }

    private String code;
}