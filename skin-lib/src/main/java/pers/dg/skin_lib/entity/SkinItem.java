package pers.dg.skin_lib.entity;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SkinItem {
	
	public View view;
	
	public List<SkinAttr> attrs;
	
	public SkinItem(){
		attrs = new ArrayList<>();
	}
	
	public void apply(){
		if(attrs.size() == 0){
			return;
		}
		for(SkinAttr at : attrs){
			at.apply(view);
		}
	}
	
	public void clean(){
		for(SkinAttr at : attrs){
			at = null;
		}
	}
}
