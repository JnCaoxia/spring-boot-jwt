package murraco.utils;

import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * JavaBean的一些适用方法，包括BeanCopy,目前该功能依赖spring-core/spring-beans
 */
public final class JavaBean {
    private static final ConversionService DEFAULT_CONVERSION = new DefaultConversionService();

    private JavaBean(){
        throw new AssertionError("不支持实例化");
    }

    public static<T> T copyOf(Object source, Supplier<T> beanCreator){
        return copyOf(source, beanCreator, false);
    }

    public static <T> T copyOf(Object source, Supplier<T> beanCreator, boolean enableTypeAutoConvert){
        return copyOf(source, beanCreator, enableTypeAutoConvert ? Specification.TYPE_AUTO_CONVERT: Specification.DEFAULT);
    }

    public static <T> List<T> copyOf(Collection<?> sourceList, Supplier<T> beanCreator){
        return copyOf(sourceList, beanCreator, false);
    }

    public static <T> List<T> copyOf(Collection<?> sourceList, Supplier<T> beanCreator, boolean enableTypeAutoConvert){
        return copyOf(sourceList, beanCreator, enableTypeAutoConvert ? Specification.TYPE_AUTO_CONVERT: Specification.DEFAULT);
    }

    public static <T> T copyOfThenSet(Object source, Supplier<T> beanCreator, Consumer<T> consumer){
        T r = copyOf(source, beanCreator);
        Optional.ofNullable(r).ifPresent(consumer);
        return r;
    }

    public static <T> T copyOf(Object source, Supplier<T> beanCreator, Specification beanCopyRule){
        if(beanCopyRule == null){
            throw new UnsupportedOperationException("请指定beanCopyRule");
        }
        if(source == null || beanCreator == null){
            return null;
        }else{
            try{
                T target = beanCreator.get();
                PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(target.getClass());
                Set<String> excludeFields = null;
                if(beanCopyRule.getExcludeFields() != null && beanCopyRule.getExcludeFields().length > 0){
                    excludeFields = new HashSet<>(Arrays.asList(beanCopyRule.getExcludeFields()));
                }
                Set<String> includeFields = null;
                if(beanCopyRule.getIncludeFields() != null && beanCopyRule.getIncludeFields().length > 0){
                    includeFields = new HashSet<>(Arrays.asList(beanCopyRule.getIncludeFields()));
                }
                for(PropertyDescriptor targetPd : targetPds){
                    Method writeMethod = targetPd.getWriteMethod();
                    if(writeMethod != null){
                        if(excludeFields != null && excludeFields.contains(targetPd.getName())){
                            continue;// 先过滤黑名单
                        }
                        if(includeFields != null && !includeFields.contains(targetPd.getName())){
                            continue;// 再过滤白名单
                        }
                        PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                        if(sourcePd != null){
                            Method readMethod = sourcePd.getReadMethod();
                            if(readMethod != null){
                                // getter方法存在，我们判断下字段类型是否兼容
                                boolean fieldTypeCompatible = ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType());
                                // 类型不兼容但是也没有开启类型自动转换，直接忽略这个字段
                                if(!fieldTypeCompatible && !beanCopyRule.isTypeAutoConvert()){
                                    continue;
                                }
                                try{
                                    if(!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())){
                                        readMethod.setAccessible(true);
                                    }
                                    Object value = readMethod.invoke(source);
                                    if(null == value){
                                        continue;
                                    }
                                    // 尝试类型转换
                                    if(!fieldTypeCompatible && beanCopyRule.isTypeAutoConvert()){
                                        // 检查能否转换，不能转换忽略掉，要打印日志吗
                                        if(!DEFAULT_CONVERSION.canConvert(readMethod.getReturnType(), writeMethod.getParameterTypes()[0])){
                                            continue;
                                        }
                                        // 如果转换异常的话，直接报错吧
                                        value = DEFAULT_CONVERSION.convert(value, writeMethod.getParameterTypes()[0]);
                                    }

                                    if(!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())){
                                        writeMethod.setAccessible(true);
                                    }
                                    writeMethod.invoke(target, value);
                                }catch (Throwable ex){
                                    throw new FatalBeanException("Could not copy property '" + targetPd.getName() +"' from source to target",ex);
                                }

                            }
                        }
                    }
                }
                return target;
            }catch (BeansException e){
                throw new UnsupportedOperationException("===>> class copy error: from 【" + source.getClass() +"】 to targetClass 】", e);
            }
        }
    }

    public static <T> List<T> copyOf(Collection<?> sourceList, Supplier<T> beanCreator, Specification beanCopyRule){
        if(beanCopyRule == null){
            throw new UnsupportedOperationException("请指定beanCopyRule");
        }
        if(sourceList == null || beanCreator == null || sourceList.isEmpty()){
            return null;
        }else{
            try{
                List<T> targetList = new ArrayList<>(sourceList.size());
                for(Object object : sourceList){
                    T source = copyOf(object, beanCreator, beanCopyRule);
                    if(source != null){
                        targetList.add(source);
                    }
                }
                return targetList;
            }catch (BeansException e){
                throw new UnsupportedOperationException("bean fields copy error", e);
            }
        }
    }

    /**
     * 关于BeanCopy的一些描述规则
     */
    public static class Specification{
        /**
         * 默认行为，不开启类型转换，也不过滤字段
         */
        private static final Specification DEFAULT = Specification.define();

        /**
         * 支持类型转换，但不支持过滤字段
         */
        private static final Specification TYPE_AUTO_CONVERT = Specification.define().enableTypeAutoConvert();

        /**
         * 定义一组Bean Copy规则
         * @return
         */
        public static Specification define(){
            return new Specification();
        }

        /**
         * 字段类型是否自动转换，当两个Bean字段名一样，但是字段类型不一样的，是否自动转换
         *
         * 目前主要应用在后端是long/double类型，但是前端js不支持这个精度，因此需要Number转换为String传递
         */
        private boolean typeAutoConvert = false;


        /**
         * 排除哪些字段（黑名单）
         */
        private String[] excludeFields;

        /**
         * 只包含那些字段（白名单）
         */
        private String[] includeFields;

        /**
         * 字段类型是否自动转换，当两个Bean字段名一样，但是字段类型不一样时， 是否自动转换
         */
        public Specification enableTypeAutoConvert(){
            this.typeAutoConvert = true;
            return this;
        }

        public Specification excludeThese(String... fieldNames){
            this.excludeFields = fieldNames;
            return this;
        }

        public Specification includeThese(String... fieldNames){
            this.includeFields = fieldNames;
            return this;
        }

        boolean isTypeAutoConvert(){
            return this.typeAutoConvert;
        }
        String[] getExcludeFields(){
            return this.excludeFields;
        }

        String[] getIncludeFields() {
            return this.includeFields;
        }


    }

}




