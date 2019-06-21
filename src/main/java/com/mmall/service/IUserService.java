package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    /**
     * 未登录状态下忘记密码找到问题答案
     *
     * @param username
     * @return
     */
    ServerResponse<String> forgetGetQuestion(String username);

    /**
     * 校验用户名，问题，答案是否一致,并往token里面传值
     *
     * @param username
     * @param question
     * @param answer
     * @return
     */

    ServerResponse<String> forgetCheckAnswer(String username, String question, String answer);

    /**
     * 未登录状态 忘记密码后校验token是否一致，修改密码
     *
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    /**
     * 登录状态下重置密码
     *
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    ServerResponse<String> reset_password(String passwordOld, String passwordNew, User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);

    ServerResponse checkAdminRole(User user);
}
