package tlog;


import java.util.Map;

/**
 *
 * Created by hui.zhu on 2016/5/12.
 */
public abstract class TaskProvider implements TLogProvider{

    public TaskProvider() {

    }

    @Override
    public void onError(int err) {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public Map<String, String> getParams() {
        return null;
    }


    @Override
    public Map<String, byte[]> getPostEntities() {
        return null;
    }


    @Override
    public boolean supportPost() {
        return true;
    }

    @Override
    public int strategyParse(){
        return TLogStrategy.DEFAULT;
    }

}
