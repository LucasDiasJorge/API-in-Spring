package com.project.core.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EEntityType {

    ITEM("ITEM"),
    PRODUCT("PRODUCT"),
    LOCATION("LOCATION"),
    NONE("NONE");

    private String value;

    private EEntityType(String value){
        this.value = value;
    }

    @JsonCreator
    public static EEntityType validator(String value){
        for (EEntityType status : values()) {
            if(status.value.equals(value)){
                return status;
            }
        }

        throw new EnumConstantNotPresentException(EEntityType.class, value + " não é um valor válido para EEntityType");
    }

}
