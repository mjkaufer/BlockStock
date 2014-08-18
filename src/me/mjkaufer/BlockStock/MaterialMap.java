package me.mjkaufer.BlockStock;

import org.bukkit.Material;

public class MaterialMap {
	private Material /*before, */after;
	private int required, yield;
	public MaterialMap(/*Material b, */Material a, int r){
		//before = b;
		after = a;
		required = r;
		yield = 1;
	}
	
	public MaterialMap(/*Material b, */Material a, int r, int y){
		//before = b;
		after = a;
		required = r;
		yield = y;
	}
	
//	public Material getBefore(){
//		return before;
//	}
	
	public Material getAfter(){
		return after;
	}
	
	public int getAmount(){
		return required;
	}
	
	public int getYield(){
		return yield;
	}
	
	public String toString(){
		return getAfter() + ":" + getAmount() + ":" + getYield();
	}
}
