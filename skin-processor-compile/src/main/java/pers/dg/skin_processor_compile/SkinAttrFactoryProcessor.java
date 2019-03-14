package pers.dg.skin_processor_compile;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import pers.dg.skin_annotation.SkinAttrFactoryAnnotation;

import static javax.lang.model.SourceVersion.latestSupported;

/**
 * Created by b on 2019/3/13.
 */

@AutoService(Processor.class)
class SkinAttrFactoryProcessor extends AbstractProcessor {

    private Types mTypeUtils;
    private Messager mMessager;
    private Filer mFiler;
    private Elements mElementUtils;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(SkinAttrFactoryAnnotation.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mTypeUtils = processingEnvironment.getTypeUtils();
        mMessager = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //  通过RoundEnvironment获取到所有被@SkinAttrFactory注解的对象
        Map<String, String> map = new HashMap<>();
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(SkinAttrFactoryAnnotation.class)) {
            //循环读取注解对象的值，并保存到 map 中，用于在工厂类中设置对应的类和属性
            if (annotatedElement.getKind() != ElementKind.CLASS) {
//                throw new ProcessingException(annotatedElement, "Only classes can be annotated with @%s", SkinAttrFactory.class.getSimpleName());
                continue;
            }
            TypeElement typeElement = (TypeElement) annotatedElement;
            SkinAttrFactoryAnnotation annotatedElement1 = typeElement.getAnnotation(SkinAttrFactoryAnnotation.class);
            String className = annotatedElement1.className();
            String attrName = annotatedElement1.attrName();
//            packageName = aClass.getPackage().getName();
            map.put(attrName,className);
        }
        if(map.size()>0){
            try {
                //写指定的 java 文件
                WriteSkinAttrFactory writeSkinAttrFactory = new WriteSkinAttrFactory("", map);
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                        writeSkinAttrFactory.className);
                Writer writer = jfo.openWriter();
                writer.write(writeSkinAttrFactory.write());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
