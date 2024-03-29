package dev.adamhodgkinson;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Utils {
    static Gson g = new Gson();
    /**Utility method for converting a request.body json string to an object using Gson library
     * @param in JSON String input
     * @param type Class to create object of
     * @return Object of type type, or null if error encountered*/
    public static <T> T convertBodyToObject(String in, Class<T> type){
        try {
            return g.fromJson(in, type);
        } catch (JsonSyntaxException e) {
            System.out.println("Error reading json");
            System.out.println(in);
            System.out.println(e.getMessage());
            return null;
        }
    }
}
