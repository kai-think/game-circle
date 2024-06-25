package com.kai.sys.entity;

import lombok.Data;

@Data
public class PermissionPath {
    Integer roleId;
    Integer menuId;
    String path1;
    String path2;
}
