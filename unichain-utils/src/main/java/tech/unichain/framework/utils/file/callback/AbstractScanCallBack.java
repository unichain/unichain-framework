package tech.unichain.framework.utils.file.callback;

/**
 * Created by lait.zhang@gmail.com on 2015-12-09 0009.
 */
public abstract class AbstractScanCallBack implements ScanCallBack {

    private boolean exit = false;

    @Override
    public void exit() {
        exit=true;
    }

    @Override
    public boolean isExit() {
        return exit;
    }

}
