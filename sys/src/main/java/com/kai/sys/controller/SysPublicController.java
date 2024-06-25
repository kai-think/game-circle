package com.kai.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kai.common.BaseController;
import com.kai.common.config.encrypt.EncryptedRequestBody;
import com.kai.common.utils.IValidator;
import com.kai.common.utils.cipher.MD5WithSalt;
import com.kai.common.utils.httpresult.FailResult;
import com.kai.common.utils.httpresult.HttpResult;
import com.kai.common.utils.httpresult.SuccessResult;
import com.kai.sys.entity.SysUser;
import com.kai.sys.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@RestController
@RequestMapping("/sys/public")
public class SysPublicController extends BaseController {
    @Autowired
    SysUserMapper sysUserMapper;

    @PostMapping("/login")
    @EncryptedRequestBody
    public HttpResult<SysUser> login(String username, String password, HttpServletResponse response) {
        SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));
        if (sysUser == null)
            return new FailResult<>("用户不存在");

        if (sysUser.getUseful() == 0)
            return new FailResult<>("用户不可用");

        if (!MD5WithSalt.verify(password, sysUser.getSalt(), sysUser.getPassword()))
            return new FailResult<>("用户名或密码错误");

        setAuthorization(sysUser);
        return new SuccessResult<>(sysUser);
    }

    @PostMapping("/loginWithToken")
    public HttpResult<SysUser> login(HttpServletRequest request) {
        SysUser sysUser = verifyAuthorization();
        if (sysUser == null)
            return new FailResult<>("错误用户");

        return new SuccessResult<>(sysUser);
    }

    @PostMapping("/register")
    public HttpResult<SysUser> register(@RequestBody @NonNull SysUser sysUser, HttpServletResponse response) throws NoSuchAlgorithmException {
        if (IValidator.empty(sysUser.getUsername(), sysUser.getPassword()))
            return new FailResult<>("账号密码为空");

        SysUser u = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", sysUser.getUsername()));
        if (u != null)
            return new FailResult<>("用户已存在");

        String salt = UUID.randomUUID().toString();
        String password = MD5WithSalt.digest(sysUser.getPassword(), salt);

        sysUser.setPassword(password);
        sysUser.setSalt(salt);
        sysUserMapper.insert(sysUser);

        setAuthorization(sysUser);
        return new SuccessResult<>(sysUser);
    }
}
