package com.example.myclient.settings;

public class DoubleSetting extends Setting<Double> {
    private final double min,max,step;
    public DoubleSetting(String name,double value,double min,double max,double step){ super(name,value); this.min=min; this.max=max; this.step=step; }
    public double min(){return min;} public double max(){return max;} public double step(){return step;}
    public void increment(){ set(Math.min(max,get()+step)); }
    public void decrement(){ set(Math.max(min,get()-step)); }
}
