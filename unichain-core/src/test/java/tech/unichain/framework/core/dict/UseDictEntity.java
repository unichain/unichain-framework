package tech.unichain.framework.core.dict;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lait.zhang
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UseDictEntity {
    @Dict(
            alias = "statusText"
            , items = {
            @Item(text = "正常", value = "1"),
            @Item(text = "失效", value = "0")
    })
    private Byte status;

    private String statusText;
}
