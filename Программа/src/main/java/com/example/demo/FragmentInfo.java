package com.example.demo;

import java.io.Serializable;

public class FragmentInfo implements Serializable {
    private static final long serialVersionUID = -3496931855932152057L;
    public String name;
    public String ID;
    public String URL;
    public String fileName;
    public FragmentInfo(String _name, String _ID,String _URL){
        name = _name;
        URL = _URL;
        fileName = URL.substring(URL.lastIndexOf('\\') + 1);
        ID = _ID;
    }
}
