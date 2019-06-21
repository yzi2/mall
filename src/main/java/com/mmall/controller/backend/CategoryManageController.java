package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @Auther: hexiaoyan
 * @Date: 2019/6/12 11:09
 * @Description:
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Resource
    private IUserService iUserService;
    @Resource
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("当前用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 添加添加分类的逻辑
            return iCategoryService.addCategory(categoryName, parentId);
        }
        return ServerResponse.createByErrorMessage("当前用户不是管理员！");

    }

    @RequestMapping(value = "set_category_name.do", method = RequestMethod.POST)
    @ResponseBody
        public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("当前用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 添加添加分类的逻辑
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        }
        return ServerResponse.createByErrorMessage("当前用户不是管理员！");
    }

    @RequestMapping(value = "get_category.do")
    @ResponseBody
    public ServerResponse getParallelCategory(HttpSession session, Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("当前用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 添加获取平级子类的逻辑
            return iCategoryService.getParallelCategory(categoryId);
        }
        return ServerResponse.createByErrorMessage("当前用户不是管理员！");
    }

    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse getDeepCategory(HttpSession session, Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("当前用户未登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 查询当前节点的id和递归子节点的id的逻辑
            return iCategoryService.getChildrenAndDeepCategory(categoryId);
        }
        return ServerResponse.createByErrorMessage("当前用户不是管理员！");
    }

}