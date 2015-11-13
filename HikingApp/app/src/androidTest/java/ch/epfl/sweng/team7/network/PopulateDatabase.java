package ch.epfl.sweng.team7.network;

import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by simon on 11/13/15.
 */
public class PopulateDatabase {

    // Find all .gpx files in the device external storage
    public static void findAllFiles() {
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
                Log.d("PopulateDatabase", gpxFile.toString());
            }
        }
    }
    public static void pushFile(String filePath) {

        try {
            File fXmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            RawHikeData rawHikeData = RawHikeData.parseFromGpxXml(doc);
            // TODO: post to database
        } catch(Exception e) {
            Log.e("PopulateDatabase", "Fail: "+e.toString());
        }
    }
}
