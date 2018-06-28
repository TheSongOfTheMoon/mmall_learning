package com.mmall.controller.backend;

import com.mmall.common.Conts;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/manage/category")
@Slf4j
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;



    @RequestMapping(value ="add_Category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value ="parentId",defaultValue ="0") int parentId){
        User user=(User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            return iCategoryService.addCategory(categoryName,parentId);
        }else{

            return ServerResponse.createByErrorMessage("无操作权限，需要管理员权限");
        }
    }

    @RequestMapping(value ="set_CategoryName.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session,String categoryName,Integer categoryId){
        //校验是不是管理员
        User user=(User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //是管理员
            return iCategoryService.updateCatrgoryName(categoryName,categoryId);
        }else{
            return ServerResponse.createByErrorMessage("无操作权限，需要管理员权限");
        }

    }


    //获取平级子节点
    @RequestMapping(value ="get_ChildrenParallelCategory.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session, @RequestParam(value="categoryId",defaultValue ="0")Integer categoryId){
        //校验是不是管理员
        User user=(User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登录");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){

            //查询当前子节点和递归子节点的id
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else{
            return ServerResponse.createByErrorMessage("无操作权限，需要管理员权限");
        }
    }


    //获取子节点
    @RequestMapping(value ="get_DeepChildrenParallelCategory.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Integer>> getDeepChildrenParallelCategory(HttpSession session, @RequestParam(value="categoryId",defaultValue ="0")Integer categoryId){
        //校验是不是管理员
        User user=(User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登录");
        }
        //获取
        if (iUserService.checkAdminRole(user).isSuccess())
            return iCategoryService.selectCategoryAndChilerenById(categoryId);
        else{
            return ServerResponse.createByErrorMessage("无操作权限，需要管理员权限");
        }
    }



}
