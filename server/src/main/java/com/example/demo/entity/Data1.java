package com.example.demo.entity;

import com.mysql.cj.xdevapi.JsonArray;

import java.util.ArrayList;

public class Data1 {

    private int code;
    private ArrayList<Item> result;

    public Data1 setCode(int code) {
        this.code = code;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Data1 setResult(ArrayList<Item> result) {
        this.result = result;
        return this;
    }

    public ArrayList<Item> getResult() {
        return result;
    }
}
