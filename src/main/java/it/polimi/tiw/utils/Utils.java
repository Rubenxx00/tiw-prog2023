package it.polimi.tiw.utils;

import com.google.gson.reflect.TypeToken;

import com.google.gson.Gson;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static HashMap<Integer, String> gradeMap = new HashMap<>() {{
        put(0, "<vuoto>");
        put(1, "Assente");
        put(2, "Rimandato");
        put(3, "Riprovato");
        put(18, "18");
        put(19, "19");
        put(20, "20");
        put(21, "21");
        put(22, "22");
        put(23, "23");
        put(24, "24");
        put(25, "25");
        put(26, "26");
        put(27, "27");
        put(28, "28");
        put(29, "29");
        put(30, "30");
        put(31, "30 e Lode");
    }};

    public static Integer getGradeValueFor(String gradeString) {
        for (Map.Entry<Integer, String> entry : gradeMap.entrySet()) {
            if (entry.getValue().equals(gradeString)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
