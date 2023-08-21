package it.polimi.tiw.utils;

import com.google.gson.reflect.TypeToken;

import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Utils {
    public static Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String readBody(HttpServletRequest req) throws IOException {
        StringBuilder jsonBuff = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuff.append(line);
        }
        return jsonBuff.toString();
    }

    public static <T> List<T> parseJsonList(String json, Class<T> type) {
        Type listType = TypeToken.getParameterized(List.class, type).getType();
        return new Gson().fromJson(json, listType);
    }
}
