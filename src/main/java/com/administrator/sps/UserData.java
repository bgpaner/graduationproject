package com.administrator.sps;

/**
 * Created by Administrator on 2017/10/24.
 */

public class UserData{
    private String UserName;
    private String UserPassword;
    private int UserId;
    public int pwdresetFlag=0;

    public String getUserName() {             //获取用户名
        return UserName;
    }
    public void setUserName(String userName) {  //设置用户名
        this.UserName = userName;
    }
    public String getUserPwd() {                //获取用户密码
        return UserPassword;
    }
    public void setUserPwd(String userPassword) {     //输入用户密码
        this.UserPassword = userPassword;
    }
    public int getUserId() {                   //获取用户ID号
        return UserId;
    }
    public void setUserId(int userId) {       //设置用户ID号
        this.UserId = userId;
    }
    public UserData(String userName, String userPwd, int userId) {    //用户信息
        super();
        this.UserName = userName;
        this.UserPassword = userPwd;
        this.UserId = userId;
    }
    public UserData(String userName, String userPwd) {  //这里只采用用户名和密码
        super();
        this.UserName = userName;
        this.UserPassword = userPwd;
    }
}
