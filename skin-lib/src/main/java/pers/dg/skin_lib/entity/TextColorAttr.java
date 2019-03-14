package pers.dg.skin_lib.entity;

import android.view.View;
import android.widget.TextView;

import pers.dg.skin_annotation.SkinAttrFactoryAnnotation;
import pers.dg.skin_lib.SkinManager;

@SkinAttrFactoryAnnotation(attrName = "textColor", className = "TextColorAttr")
public class TextColorAttr extends SkinAttr {

	@Override
	public void apply(View view) {
		if(view instanceof TextView){
			TextView tv = (TextView)view;
			if("color".equals(attrValueTypeName)){
				tv.setTextColor(SkinManager.getInstance().convertToColorStateList(attrValueRefId));
			}
		}
	}
}
