package com.tcl.zhanglong.utils.IPC;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Steve on 16/12/17.
 */

public class SerializableOneObject {

    public void serializeObject() throws Exception {
        UserA user = new UserA(0, "jake", true);
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("cache.txt"));
        out.writeObject(user);
        out.close();
    }

    public void unSerializeObject() throws Exception {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("cache.txt"));
        UserA newUser = (UserA) in.readObject();
        in.close();
    }
}
