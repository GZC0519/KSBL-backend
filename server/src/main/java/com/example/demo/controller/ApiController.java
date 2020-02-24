package com.example.demo.controller;

import com.example.demo.entity.*;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.mysql.cj.xdevapi.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api")
public class ApiController {
    @Value("${upload.file.path}")
    private  String uploadPathStr;
    //自动加载application.properties里的配置
    @Autowired
    private JdbcTemplate jdbcTemplate ;
    //配置URI
    @RequestMapping(value = "/insertRegistUser")
    public Data ins(RegisterUser u){ return insertRegist(u); }

    @RequestMapping(value = "/insertCase")
    public  Data insC(String caseID,String wfrName,String wfrID,boolean issick,boolean isOfficial,String jqsName,String jqsTel,String handler){return insCase(caseID,wfrName,wfrID,issick,isOfficial,jqsName,jqsTel,handler);}

    @RequestMapping(value = "/updateCaseTable")
    public Data updateCaseTable(String str1,String str2,String str3){return updateCaseT(str1,str2,str3);}

    @RequestMapping(value = "/login")//未完成，需对数据库查询得到的结果进行空判断。
    public  Data log(String ID,String password){
        return login(ID,password);
    }

    @PostMapping(value = "/uploadImage")//处理图片上传请求
    public Data up(MultipartFile file,String filename){ return upload(file,filename);}

    @PostMapping(value = "/downloadImage")//处理下载图片请求
    public ResponseEntity<FileSystemResource> download(@RequestParam String filename)
    {
        if(filename == null || filename.isEmpty())
            return null;
        File file = Paths.get(uploadPathStr).resolve(filename).toFile();
        if(file.exists() && file.canRead())
        {
            System.out.println("download file , filename is "+filename);
            return ResponseEntity.ok()
                    .contentType(file.getName().contains(".jpg") ? MediaType.IMAGE_JPEG : MediaType.IMAGE_PNG)
                    .body(new FileSystemResource(file));
        }
        else
            return null;
    }

    @RequestMapping(value = "/getCaseID")
    public Data getCaseID(){return  returnCaseID();}

    @RequestMapping(value = "/getAllCase")
    public Data1 getAllCase(String handler){return getAllCa(handler);}

    @RequestMapping(value = "matchPD")
    public  Data matchPD(String ID,String name){return match(ID,name);}


    private  Data insertRegist(RegisterUser u){
        String ID = u.getID();
        String name = u.getName();
        String password = u.getPassword();

        String sql = "insert into registeruser values ("+"\""+ID+"\""+","+"\""+password+"\""+","+"\""+name+"\""+");";
        try {
            jdbcTemplate.execute(sql);
            return new Data().setCode(1).setResult("insert successful");
        }catch (Exception e){
            e.printStackTrace();
            return new Data().setCode(-1).setResult("insert fail");
        }
    }

    private  Data login(String ID,String password){
        String sql = "select * from registeruser where ID = "+"\""+ID+"\""+";";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
            if(result.next()){
                if (password.equals(result.getString(2))){
                    return new Data().setCode(1).setResult(result.getString(3));
                }
                else {
                    return new Data().setCode(-3).setResult("wrong password ！");
                }
            }
            else {
                return new Data().setCode(-2).setResult("账户不存在 ！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return new Data().setCode(-1).setResult("login fail !");
        }
    }

    private  Data upload(MultipartFile file,String filename){
        if(file == null || file.isEmpty() || filename == null || filename.isEmpty())
            return new Data().setCode(7).setResult("上传失败");
        try(InputStream inputStream = file.getInputStream())
        {
            Path uploadPath = Paths.get(uploadPathStr);
            if(!uploadPath.toFile().exists())
                uploadPath.toFile().mkdirs();
            Files.copy(inputStream, Paths.get(uploadPathStr).resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("upload file , filename is "+filename);
            return new Data().setCode(7).setResult("上传成功");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new Data().setCode(7).setResult("上传失败");
        }
    }

    private  Data returnCaseID(){
        String sql = "select CaseID from caseTable order by CaseID desc limit 0,1";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        result.next();
        String caseID = result.getString(1);
        int x = Integer.parseInt(caseID.substring(4));
        x=x+1;
        String temp;
        if(0<x&&x<9){
            temp="000"+x;
        }
        else if(10<=x&&x<99){
            temp="00"+x;
        }else if(100<=x&&x<999){
            temp="0"+x;
        }else{
            temp=""+x;
        }
        caseID=caseID.substring(0,4)+temp;
        return new Data().setCode(1).setResult(caseID);
    }

    private  Data insCase(String caseID,String wfrName,String wfrID,boolean issick,boolean isOfficial,String jqsName,String jqsTel,String handler){
        String sql = "insert into caseTable values ("+"\""+caseID+"\""+","+"\""+wfrName+"\""+","+"\""+wfrID+"\""+","+issick+","+isOfficial+","+"\""+jqsName+"\""+","+"\""+jqsTel+"\""+","+null+","+"\""+handler+"\""+","+"\""+caseID+"_1.jpg"+"\""+","+"\""+caseID+"_2.jpg"+"\""+")";
        System.out.println(sql);
        try {
            jdbcTemplate.execute(sql);
            return  new Data().setCode(1).setResult("insert into caseTable successful!");
        }catch (Exception e){
            e.printStackTrace();
            return  new Data().setCode(-1).setResult(e.getMessage());
        }
    }

    private  Data updateCaseT(String str1,String str2,String str3){
        String sql = "update caseTable set "+str1+" = "+"\""+str2+"\""+" where CaseID = "+"\""+str3+"\"";
        System.out.println(sql);
        try{
            jdbcTemplate.execute(sql);
            return new Data().setCode(1).setResult("update successful");
        }catch (Exception e){
            e.printStackTrace();
            return new Data().setCode(-1).setResult(e.getMessage());
        }

    }

    private Data1 getAllCa(String  handler){
            String sql = "select * from caseTable where handler = "+"\""+handler+"\"";
            int i = 0;
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
            int count = 0;
            while(result.next()){
                count++;
            }
            while(result.previous()){

            }
            //System.out.println(count);
            ArrayList<Item> al=new ArrayList<Item>();
            while(result.next()){
                Item item = new Item();
                item.value1=result.getString(1);
                item.value2=result.getString(2);
                item.value3=result.getString(3);
                item.value4=result.getString(8);
                item.value5=result.getString(9);
                item.value6=result.getString(10);
                item.value7=result.getString(11);
                al.add(item);
            }
            return  new Data1().setCode(1).setResult(al);
    }

    private  Data match(String  ID,String name){
        String sql = "select * from policeuser where ID = "+"\""+ID+"\"";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
            if (result.next())
                return  new Data().setCode(1).setResult("MATCH SUCCESSFUL");
            else
                return  new Data().setCode(-1).setResult("police did't exist");
        }catch (Exception e){
            e.printStackTrace();
            return  new Data().setCode(-2).setResult("MATCH UNSUCESSFUL");
        }
    }
}
