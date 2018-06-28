package com.mmall.controller.backend;

import com.mmall.common.Conts;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileServer;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.management.modelmbean.RequiredModelMBean;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
@Slf4j
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileServer iFileServer;

    @RequestMapping(value = "SaveProduct.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse SaveProduct(HttpSession session, Product product){
        //已经登录
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登录");
        }
        //已有权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            //追加产品的业务逻辑
            return iProductService.SaveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("保存产品失败");
        }

    }

    @RequestMapping(value = "SetSaleStatus.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse SetSaleStatus(HttpSession session, Product product){
        //已经登录
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登录");
        }
        //已有权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            //追加产品的业务逻辑
            return iProductService.SetSaleStatus(product.getId(),product.getStatus());
        }else{
            return ServerResponse.createByErrorMessage("保存产品失败");
        }
    }


    @RequestMapping(value = "getDetails.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getmanageProductDetails(HttpSession session, Integer productId){
        //已经登录
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登录");
        }
        //已有权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            //追加产品的业务逻辑
            return iProductService.GetManageProductDetails(productId);
        }else{
            return ServerResponse.createByErrorMessage("保存产品失败");
        }
    }


    //后台商品分页
    @RequestMapping(value = "getList.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue ="1")int pageNum, @RequestParam(value ="pageSize",defaultValue ="10") int pageSize){
        //已经登录
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登录");
        }
        //已有权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            //追加产品的业务逻辑
            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("保存产品失败");
        }
    }


    //根据商品名字搜索或者用户Id搜索
    //后台商品分页
    @RequestMapping(value = "getProductSearch.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getProductSearch(HttpSession session,String ProductName,Integer productId, @RequestParam(value = "pageNum",defaultValue ="1")int pageNum, @RequestParam(value ="pageSize",defaultValue ="10") int pageSize){
        //已经登录
        User user= (User) session.getAttribute(Conts.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登录");
        }
        //已有权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            //追加产品的业务逻辑
            return iProductService.getProductSearchList(ProductName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("搜索产品失败");
        }
    }



    @RequestMapping(value = "upload.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse upload(HttpSession session ,@RequestParam(value ="upload_file" ,required = false)MultipartFile file, HttpServletRequest request){
        //已经登录
        //User user= (User)session.getAttribute(Conts.CURRENT_USER);
        //if (user==null){
         //   return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"管理员未登录，请登录");
        //}
        //已有权限
        //if (iUserService.checkAdminRole(user).isSuccess()){
            String path=request.getSession().getServletContext().getRealPath("upload");//上传的路径名,一般是泛指在ewebapps中
            //创建一个文件处理的服务
            String targetFileName=iFileServer.upload(file,path);
            String url= PropertiesUtil.getProperties("ftp.server.http.prefix")+targetFileName;
            Map filemap=new HashMap();
            filemap.put("uri",targetFileName);
            filemap.put("url",url);
            return ServerResponse.createBySuccess(filemap);
        //}else{
            //return ServerResponse.createByErrorMessage("上传校验失败");
        //}
    }


    @RequestMapping(value = "richtext_upload.do",method = RequestMethod.POST)
    @ResponseBody
    public  Map richupload(HttpSession session , @RequestParam(value ="upload_file" ,required = false)MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map resultMap = new HashMap();

        //已经登录
        User user = (User) session.getAttribute(Conts.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请登录管理员");
            return resultMap;
        }
        //已有权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");//上传的路径名,一般是泛指在ewebapps中
            //创建一个文件处理的服务
            String targetFileName = iFileServer.upload(file, path);

            if (org.apache.commons.lang3.StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
            }
            String url = PropertiesUtil.getProperties("ftp.server.http.prefix") + targetFileName;
            Map filemap = new HashMap();
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            filemap.put("file_path", url);
            response.addHeader("Accesss-Control-Allow-Headers", "X-File-Name");
            return resultMap;
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "无操作权限");
            return resultMap;
        }

    }
}

