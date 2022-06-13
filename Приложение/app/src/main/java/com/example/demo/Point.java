package com.example.demo;

import org.jgrapht.alg.util.Pair;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Point implements Serializable {
    private static final long serialVersionUID = -3496931855932152057L;
    public Map<Integer, Pair<Double, Double>> positions = new HashMap<>();
    private double x;
    private double y;
    private String name = new String();

    int type;
    private int id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addXY(Integer id, Pair<Double, Double> pos) {positions.put(id, pos);}

    public void removeXY(Integer id) {positions.remove(id);}

    public Pair<Double, Double> getPos(Integer id){
        return positions.get(id);
    }

    public void setID(int c) {
        id = c;
    }

    public String getID() {
        return String.valueOf(id);
    }

    @Override
    public String toString(){
        return getID();
    }
}
