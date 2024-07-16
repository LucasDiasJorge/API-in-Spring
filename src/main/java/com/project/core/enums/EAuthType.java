package com.project.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EAuthType {

    BASIC("BASIC"),
    BEARER("BEARER"),
    API_KEY("API_KEY"),
    NONE("NONE");

    private String value;

    private EAuthType(String value){
        this.value = value;
    }

    @JsonCreator
    public static EAuthType validator(String value){
        for (EAuthType status : values()) {
            if(status.value.equals(value)){
                return status;
            }
        }

        throw new EnumConstantNotPresentException(EAuthType.class, value + " não é um valor válido para EAuthType");
    }

}
