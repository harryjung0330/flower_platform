package com.example.flowerplatform.security.oauth2.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
@Slf4j
public class ObjectSerializationUtil
{
    @SneakyThrows
    public byte[] serializeObject(Object obj)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            return  bos.toByteArray();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                log.error(ex.toString());
                throw ex;
            }
        }
    }

    @SneakyThrows
    public Object deserializeObject(byte[] byteObject)
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(byteObject);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();

            return o;

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
                log.error(ex.toString());
                throw ex;
            }
        }
    }


}
