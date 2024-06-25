package com.kai.common;

import java.util.List;


public interface EnableListToTree<T> {
    Object id();
    Object parentId();
    List<T> children();
    void setChildren(List<T> list);
}