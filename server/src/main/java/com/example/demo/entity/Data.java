package com.example.demo.entity;

public class Data {

    private int code;
    private String result;

    public Data setCode(int code) {
        this.code = code;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Data setResult(String result) {
        this.result = result;
        return this;
    }

    public String getResult() {
        return result;
    }
}
