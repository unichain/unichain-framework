package tech.unichain.framework.core.dict;

import lombok.*;

/**
 * @author lait.zhang
 * @since 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UseDictEntity2 extends UseDictEntity {
    @Dict(id = "test-code",
            items = {
                    @Item(text = "编码1", value = "1"),
                    @Item(text = "编码2", value = "2")
            })
    private String code;

    private UserCode userCode=UserCode.SIMPLE;
}
