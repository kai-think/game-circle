package com.kai.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.sql.FilterItemSuper;
import com.example.demo.sys.entity.SysRole;
import com.example.demo.sys.entity.SysUser;
import com.example.demo.sys.entity.UserRole;
import com.example.demo.sys.service.impl.UserRoleServiceImpl;
import com.example.demo.utils.IValidator;
import com.example.demo.utils.QueryWrapperUtils;
import com.example.demo.utils.SysJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础Controller
 * @author bill
 *
 */
@Transactional
public class BaseController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired(required = false)
    protected HttpServletResponse response;

    @Autowired
    private UserRoleServiceImpl userRoleService;

    //设置授权码
    public void setAuthorization(@NonNull SysUser sysUser) {
        List<UserRole> userRoleList = userRoleService.list(new QueryWrapper<UserRole>().eq("user_id", sysUser.getId()));
        List<SysRole> sysRoleList = userRoleList.stream().map(ur -> {
            SysRole sr = new SysRole();
            sr.setId(ur.getRoleId());
            return sr;
        }).collect(Collectors.toList());

        sysUser.putEtc("Authorization", SysJwt.createToken(sysUser, sysRoleList));
    }

    //验证授权码
    public SysUser verifyAuthorization() {
        String authorization = request.getHeader("Authorization");
        if (IValidator.empty(authorization))
            return null;

        SysUser sysUser = null;
        try {
            sysUser = SysJwt.getUser(authorization);
        } catch (Exception e) {

        };

        return sysUser;
    }

    public SysUser getUser() {
        return (SysUser) request.getAttribute("user");
    }

    public void setQueryWrapperOrderByList(QueryWrapper<?> wrapper, List<String> orderByList) {
        QueryWrapperUtils.setQueryWrapperOrderByList(wrapper, orderByList);
    }

    public String setQueryWrapperFilterList(QueryWrapper<?> wrapper, List<FilterItemSuper> filterList) {
        return QueryWrapperUtils.setQueryWrapperFilterList(wrapper, filterList);
    }
}
