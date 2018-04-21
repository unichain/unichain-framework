package tech.unichain.framework.orm.core.param;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lait.zhang@gmail.com on 16-4-21.
 */
public class UpdateMapParam extends UpdateParam<Map<String, Object>> {
    public UpdateMapParam() {
        this(new HashMap<>());
    }

    public UpdateMapParam(Map<String, Object> data) {
        setData(data);
    }

    public UpdateMapParam set(String key, Object value) {
        this.getData().put(key, value);
        return this;
    }
}
