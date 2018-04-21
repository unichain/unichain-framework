package tech.unichain.framework.utils.file.callback;

/**
 * Created by lait.zhang@gmail.com on 2015-12-09 0009.
 */
public interface ReadCallBack extends CanExitCallBack {

    void readLine(int lineNumber, String line);

    default void error(Throwable e) {
        e.printStackTrace();
    }

    default void done(int total) {
    }

}
