package sample.GSON;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class GsonReader {
    public static <T> T fJson(String jsonString, Class<T> class1) {
        Gson json = new GsonBuilder().create();
        return json.fromJson(jsonString, class1);
    }

    public static <T> String tJson(T t) {
        Gson json = new GsonBuilder().create();
        return json.toJson(t);
    }

    public static boolean toFile(Path path, String string) {
        System.out.println("Trying to write path: " + path);
        try (FileWriter fileWriter = new FileWriter(path.toFile())) {
            fileWriter.write(string);
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String fromFile(Path path) throws IOException {
        FileReader fileReader = new FileReader(path.toFile());
        StringBuilder stringBuilder = new StringBuilder(100);
        int i;
        while ((i = fileReader.read()) != -1) {
            stringBuilder.append((char) i);
        }
        fileReader.close();
        return stringBuilder.toString();

    }

}
