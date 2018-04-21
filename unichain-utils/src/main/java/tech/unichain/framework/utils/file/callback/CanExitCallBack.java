package tech.unichain.framework.utils.file.callback;

/**
 * Created by lait.zhang@gmail.com on 2015-12-09 0009.
 */
public interface CanExitCallBack {

    default void exit() {
    }

    default boolean isExit() {
        return false;
    }
}
