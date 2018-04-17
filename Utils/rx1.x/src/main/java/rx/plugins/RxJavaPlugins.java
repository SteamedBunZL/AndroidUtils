package rx.plugins;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Steve on 2018/4/12.
 */

public class RxJavaPlugins {

    private final static RxJavaPlugins INSTANCE = new RxJavaPlugins();

    private final AtomicReference<RxJavaObservableExecutionHook> observableExecutionHook = new AtomicReference<>();

    public static RxJavaPlugins getInstance(){
        return INSTANCE;
    }

    public RxJavaObservableExecutionHook getObservableExecutionHook(){
        if (observableExecutionHook.get() == null){
            //Object impl
        }
        return null;
    }

    private static Object getPluginImplementationViaProperty(Class<?> pluginClass){
        String classSimpleName = pluginClass.getSimpleName();

        String implementingClass = System.getProperty("rxjava.plugin." + classSimpleName + ".implementation");
        if (implementingClass != null){
            try {
                Class<?> cls = Class.forName(implementingClass);
                cls = cls.asSubclass(pluginClass);
                return cls.newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }else{
            return null;
        }
        return null;
    }


}
