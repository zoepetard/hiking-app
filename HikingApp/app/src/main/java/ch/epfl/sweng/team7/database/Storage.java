package ch.epfl.sweng.team7.database;

<<<<<<< HEAD
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class to save/read a Generic object from the internal storage of the phone
=======
/**
 * Class to save/read a file from the internal storage of the phone
>>>>>>> Rebase to master
 * Created by pablo on 18/11/15.
 */
public class Storage {

<<<<<<< HEAD
    private final static String LOG_FLAG = "DB_Storage";
    /**
     * Method to save a generic object in the internal memory
     * @param genericObject
     * @param ctx the context of the app
     * @return
     * @throws IOException
     */
    public static void saveToMemory(Object genericObject, Context ctx) throws IOException {
        String fileName = genericObject.getClass().toString();
        FileOutputStream fos = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(genericObject);
        oos.close();
    }

    /**
     * Method to read a generic object from the internal memory
     * @param context
     * @param fileName
     * @return the object with the given filename
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object readFromMemory(Context context, String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        return object;

    }


=======
    //TODO
>>>>>>> Rebase to master
}
