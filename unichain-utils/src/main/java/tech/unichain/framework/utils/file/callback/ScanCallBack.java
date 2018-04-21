package tech.unichain.framework.utils.file.callback;

import java.io.File;

/**
 * Created by lait.zhang@gmail.com on 2015-12-09 0009.
 */
public interface ScanCallBack extends CanExitCallBack {

    void accept(int deep, File file);

    default void error(int deep, File file, Throwable e) {
        e.printStackTrace();
    }

}
