package ch.epfl.sweng.team7.network;

import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by simon on 11/13/15.
 */
public class PopulateDatabase {

    public static void run(DatabaseClient dbClient) {
        List<File> allFiles = findAllFiles();
        for(File gpxFile : allFiles) {
            try {
                RawHikeData rawHikeData = parseFile(gpxFile);
                dbClient.postHike(rawHikeData);
            } catch(HikeParseException|DatabaseClientException e) {
                //pass
            }
        }
    }

    // Find all .gpx files in the device external storage
    public static List<File> findAllFiles() {
        ArrayList<File> allFiles = new ArrayList<>();

        File externalDirectoryFile = Environment.getExternalStorageDirectory();
        if(externalDirectoryFile == null) return allFiles;
        String externalDirectory = externalDirectoryFile.getAbsolutePath();
        if(externalDirectory == null) return allFiles;

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
    public static RawHikeData parseFile(File gpxFile) throws HikeParseException {
        Log.d("PopulateDatabase", "pushing "+gpxFile.toString());

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(gpxFile);
            return RawHikeData.parseFromGPXDocument(doc);
        } catch(ParserConfigurationException|SAXException|IOException e) {
            throw new HikeParseException(e);
        }
    }
}
