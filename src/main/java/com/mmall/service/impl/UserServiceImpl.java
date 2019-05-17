package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @Auther: hexiaoyan
 * @Date: 2019/5/13 17:21
 * @Description:
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String digestPassword = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, digestPassword);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> serverResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        serverResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        // MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isBlank(type)) {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        if (Const.EMAIL.equalsIgnoreCase(type)) {
            int resultCount = userMapper.checkEmail(str);
            if (resultCount > 0) {
                return ServerResponse.createByErrorMessage("邮箱已存在");
            }
        }
        if (Const.USERNAME.equalsIgnoreCase(type)) {
            int resultCount = userMapper.checkUsername(str);
            if (resultCount > 0) {
                return ServerResponse.createByErrorMessage("用户名已存在");
            }

        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    @Override
    public ServerResponse<String> forgetGetQuestion(String username) {
        ServerResponse<String> serverResponse = checkValid(username, Const.USERNAME);
        if (serverResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("该用户未设置找回密码问题");
    }

    @Override
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("参数错误，forgetToken需要传递");
        }
        ServerResponse<String> serverResponse = checkValid(username, Const.USERNAME);
        if (serverResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token 无效或者已经过期");
        }
        if (StringUtils.equals(forgetToken, token)) {
            // 新密码更新旧密码
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username, md5Password);
            if (resultCount > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse<String> reset_password(String passwordOld, String passwordNew, User user) {
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");

        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        int resultCount = userMapper.checkEmailByUserid(user.getEmail(), user.getId());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("该email已存在，请更换email再重试");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        int Count = userMapper.updateByPrimaryKeySelective(updateUser);
        if (Count > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByError();
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
}