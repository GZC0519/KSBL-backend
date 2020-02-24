package com.example.demo.entity;
/**

  * @author gzc

  * @Description:验证人民警察数据库成功后在本系统注册的用户实体类

  * @date 2020/1/29

  */
public class RegisterUser {

    //ID
    private String ID;

    //name
    private  String name;

    //password
    private  String password;

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }



}
