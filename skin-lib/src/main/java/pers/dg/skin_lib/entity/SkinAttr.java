package pers.dg.skin_lib.entity;

import android.view.View;

@SuppressWarnings("ALL")
public abstract class SkinAttr {

	/**
	 * attr 的名字，如
	 */
	public String attrName;
	
	/**
	 * attr 的对应 value 的 id 值
	 */
	public int attrValueRefId;
	
	/**
	 * attr 对应的 value 值
	 */
	public String attrValueRefName;
	
	/**
	 * attr 对应 value 的 type 值
	 */
	public String attrValueTypeName;

	/**
	 * 设置 view 对应 attr 的值
	 * @param view
	 */
	public abstract void apply(View view);
}
