package cmanager.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectHelper {

    @SuppressWarnings("unchecked")
    public static <T> T copy(T o) {
        T result = null;

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
            objectOutputStream.close();
            byteArrayOutputStream.close();
            final byte[] byteData = byteArrayOutputStream.toByteArray();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteData);
            result = (T) new ObjectInputStream(byteArrayInputStream).readObject();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return result;
    }

    static boolean areEqual(Object object1, Object object2) {
        return !(object1 == null || !object1.equals(object2));
    }

    public static <T> T getBest(T object1, T object2) {
        return areEqual(object1, object2) ? object1 : object2;
    }
}
