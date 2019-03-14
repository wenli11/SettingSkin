package pers.dg.skin_processor_compile;

import java.util.Map;

/**
 * Created by b on 2019/3/13.
 */

class WriteSkinAttrFactory {

    //包名
    private String packageName;

    //类名
    final String className = "SkinAttrFactory";

    private final Map<String, String> map;

    public WriteSkinAttrFactory(String packageName, Map<String, String> map){
        this.packageName = packageName;
        this.map = map;
    }

    public String write(){
        StringBuilder sb = new StringBuilder();
        //拼接包名
        sb.append("package ").append("pers.dg.android_setting_skin").append(";\n\n");
//        sb.append("package ").append("pers.dg.skin_lib.entity").append(";\n\n");
//        sb.append("package ").append(packageName).append(";\n\n");
        //拼接依赖包
        sb.append("import pers.dg.skin_lib.entity.*;\n");
        sb.append('\n');

        sb.append("public class ").append(className);
        sb.append("{\n");

        //拼接属性
        for(String attr : map.keySet()){
            sb.append("public static final String "+attr+" = \""+attr+"\";\n");
        }

        //拼接方法 get
        sb.append("public static SkinAttr get(String attrName, int attrValueRefId, String attrValueRefName, String typeName){\n");
        sb.append("SkinAttr mSkinAttr = null;\n");
        if(map.size() > 0){
            for(String attr : map.keySet()){
                sb.append("if(").append(attr).append(".equals(attrName)){\nmSkinAttr = new ").append(map.get(attr)).append("();\n}else");
            }
            sb.append("{\nreturn null;\n}\n");
        }
        sb.append("mSkinAttr.attrName = attrName;\n");
        sb.append("mSkinAttr.attrValueRefId = attrValueRefId;\n");
        sb.append("mSkinAttr.attrValueRefName = attrValueRefName;\n");
        sb.append("mSkinAttr.attrValueTypeName = typeName;\n");
        sb.append("return mSkinAttr;\n");
        sb.append("}\n");

        //拼接方法 isSupportedAttr
        sb.append("public static boolean isSupportedAttr(String attrName){\n");
        for(String attr : map.keySet()){
            sb.append("if(").append(attr).append(".equals(attrName))");
            sb.append("return true;\n");
        }
        sb.append("return false;\n");
        sb.append("}\n");

        sb.append("}\n");
        return sb.toString();
    }

}
