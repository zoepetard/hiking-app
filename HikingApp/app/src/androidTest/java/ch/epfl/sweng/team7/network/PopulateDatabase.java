package ch.epfl.sweng.team7.network;

import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by simon on 11/13/15.
 */
public class PopulateDatabase {

    public static void run() {
        List<File> allFiles = findAllFiles();
        for(File gpxFile : allFiles) {
            RawHikeData rawHikeData = parseFile(gpxFile);
            // TODO: post to database
        }
    }

    // Find all .gpx files in the device external storage
    public static List<File> findAllFiles() {
        ArrayList<File> allFiles = new ArrayList<>();

        String externalDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        File f = new File(externalDirectory);
        if (f.exists() && f.isDirectory()) {
            final Pattern p = Pattern.compile(".*\\.gpx");

            File[] gpxFiles = f.listFiles(new FileFilter(){
                @Override
                public boolean accept(File file) {
                    return p.matcher(file.getName()).matches();
                }
            });

            for(File gpxFile : gpxFiles) {
                allFiles.add(gpxFile);
            }
        }

        return allFiles;
    }

    // Push a file to the database
    public static RawHikeData parseFile(File gpxFile) {
        Log.d("PopulateDatabase", "pushing "+gpxFile.toString());

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(gpxFile);
            return RawHikeData.parseFromGPXDocument(doc);
        } catch(Exception e) {
            Log.e("PopulateDatabase", "Fail: "+e.toString());
            return null;
        }
    }
}
