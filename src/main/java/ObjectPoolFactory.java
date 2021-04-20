import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ObjectPoolFactory {
    private Map<String, Object> objPool = new HashMap<>();
    public void initPool() throws Exception{
        File file;
        InputStream fis = ObjectPoolFactory.class.getClassLoader().getResourceAsStream("obj.txt");
        Properties props = new Properties();
        props.load(fis);
        for (String name : props.stringPropertyNames()) {
            if (!name.contains("%")) {
                objPool.put(name, createObject(props.getProperty(name)));
            }
        }
    }

    public void initMethod() throws Exception{
        File file;
        InputStream fis = ObjectPoolFactory.class.getClassLoader().getResourceAsStream("obj.txt");
        Properties props = new Properties();
        props.load(fis);
        for (String name : props.stringPropertyNames()) {
            if (name.contains("%")) {
                String[] strs = name.split("%");
                String objKey = strs[0];
                String methodName = strs[1];

                Object target = objPool.get(objKey);
                Method mtd = target.getClass().getMethod(methodName, String.class);
                mtd.invoke(target, props.getProperty(name));
            }
        }
    }

    public Object createObject(String clazzName) throws Exception{
        Class<?> clazz = Class.forName(clazzName);
        return clazz.newInstance();

    }

    public Object getObject (String name) {
        return objPool.get(name);
    }

    public static void main(String[] args) throws Exception{
        ObjectPoolFactory factory = new ObjectPoolFactory();
        factory.initPool();
        factory.initMethod();
        System.out.println(factory.getObject("a"));
        System.out.println(factory.getObject("b"));
        System.out.println(factory.getObject("c"));
    }
}
