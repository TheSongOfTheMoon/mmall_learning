package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileServer;
import com.mmall.utils.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileServer")
@Slf4j
public class IFileServerImpl implements IFileServer {

    //private Logger logger= LoggerFactory.getLogger(IFileServerImpl.class);

    public String upload(MultipartFile file, String path){
        String fileName=file.getOriginalFilename();//原始文件名
        String fileExtensionName=fileName.substring(fileName.lastIndexOf(".")+1);//必须取最后一个点
        String uploadFileName= UUID.randomUUID().toString()+"."+fileExtensionName;//用了UUID防止重复
        log.info("开始上传文件,上传的文件名:{},上传的路径:{},新的文件名:{}",fileName,path,uploadFileName);

        //检查文件名是否存在
        File fileDir=new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);//因为设在webapps下面的文件夹，Tomcat会对webapps文件进行权限管理,此处先开通权限
            fileDir.mkdirs();//如果对应的路劲找不到则创建
        }
        log.info("开始");
        //
        File targetFile=new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);//上传文件到Tomcat中的webapps
            log.info("开始-上传");
            //将webapps中的文件传到FTP服务器中-->对接
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            log.info("开始-上传2");
            //上传完成后删除upload的文件
            targetFile.delete();

        } catch (IOException e) {
            e.printStackTrace();
            log.error("上传文件异常："+e.getMessage());
            return null;
        }
        return targetFile.getName();

    }


    /*
    *成员变量
    * 成员方法
    * 局部变量
    * 局部方法
    * 静态变量
    * 静态方法
    * 构造方法
    * 内部类
    * public：全区域
    * defalut:同包
    * proted:同包类和子类
    * private：同类中可用
    *
    * */
}
