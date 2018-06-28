package com.mmall.utils;
import org.apache.commons.net.ftp.FTPClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FTPUtil {

    private static final Logger logger= LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp=PropertiesUtil.getProperties("ftp.server.ip");
    private static String ftpUser=PropertiesUtil.getProperties("ftp.user");
    private static String ftpPassword=PropertiesUtil.getProperties("ftp.pass");


    public FTPUtil(String IP, int Port, String User, String Password){
        this.IP=IP;
        this.Port=Port;
        this.User=User;
        this.Password=Password;
    }


    //开放批量上传文件uploadFile
    public static boolean uploadFile(List<File> fileList) throws IOException {
        logger.info("开始连接FTP服务器---");
        FTPUtil ftpUtil =new FTPUtil(ftpIp,21,ftpUser,ftpPassword);
        logger.info("开始连接FTP服务器");
        boolean result= ftpUtil.uploadFile2("img",fileList);
        logger.info("连接FTP服务器结束，上传结果:{}",result);
        return result;
    }





    //连接FTP逻辑
    private  boolean uploadFile2(String remotePath, List<File> fileList) throws IOException {
        boolean uploaded=true;//上传文件结果
        FileInputStream fis=null;
        //连接
        if (connectServer(this.IP,this.Port,this.User,this.Password)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath);//让FTP判断是否需要切换路径
                ftpClient.setBufferSize(1024*100);
                //编码+二进制格式
                ftpClient.setControlEncoding("utf-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();

                //文本
                for (File fileItem : fileList) {
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(), fis);
                }


            } catch (IOException e) {
                uploaded=false;
                logger.error("切换文件路径异常：" + e.getMessage());
                e.printStackTrace();
            } finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    private  boolean connectServer(String IP,int Port,String User,String Password){

        boolean isSuccess=false;
        ftpClient=new FTPClient();
        try {
            ftpClient.connect(IP);
            isSuccess=ftpClient.login(User,Password);
        } catch (IOException e) {
            logger.error("上传ftp服务器异常:"+e.getMessage());
            e.printStackTrace();
        }
        return isSuccess;
    }



    private String IP;
    private int Port;
    private String User;
    private String Password;
    private FTPClient ftpClient;

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return Port;
    }

    public void setPort(int port) {
        Port = port;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
