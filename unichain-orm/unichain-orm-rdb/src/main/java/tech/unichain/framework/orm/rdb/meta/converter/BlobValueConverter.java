package tech.unichain.framework.orm.rdb.meta.converter;

import org.hswebframework.ezorm.core.ValueConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class BlobValueConverter implements ValueConverter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Object getData(Object value) {
        if (value instanceof String) {
            value = ((String) value).getBytes();
        }
        if (value instanceof byte[]) {
            try {
                return new SerialBlob(((byte[]) value));
            } catch (SQLException e) {
                return value;
            }
        }
        return value;
    }

    @Override
    public Object getValue(Object data) {
        if (data == null) return null;
        if (data instanceof Blob) {
            Blob blobValue = ((Blob) data);
            try (InputStream inputStream = blobValue.getBinaryStream()) {
                //尝试转为对象
                try {
                    ObjectInputStream inputStream1 = new ObjectInputStream(inputStream);
                    return inputStream1.readObject();
                } catch (IOException e) {
                    //可能不是对象
                } catch (ClassNotFoundException e) {
                    logger.warn("blob is class,but class not found!", e);
                }
                //转为bytes
                return blobValue.getBytes(0, (int) blobValue.length());
            } catch (Exception e) {
                logger.warn("blob data error", e);
            }
        }
        return data;
    }
}
