package com.example.myclient.settings;

public class IntSetting extends Setting<Integer> {
    private final int min,max;
    public IntSetting(String name,int value,int min,int max){ super(name,value); this.min=min; this.max=max; }
    public int min(){return min;} public int max(){return max;}
    public void increment(){ set(Math.min(max,get()+1)); }
    public void decrement(){ set(Math.max(min,get()-1)); }
}
