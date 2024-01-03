package com.project.core.enums;

public enum RoleName {

    ROLE_ROOT("Root",0),
    ROLE_ADMIN("Administrator",1),
    ROLE_MANAGER("Manager",2),
    ROLE_USER("User",3),
    ROLE_MAINTENER("Maintener",4),
    ROLE_AUDITOR("Auditor",5),
    ROLE_PORTAL("Portal",6);

    private String name;
    private Integer priority;

    private RoleName(String name, Integer priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public Integer getPriority() {
        return priority;
    }
}
