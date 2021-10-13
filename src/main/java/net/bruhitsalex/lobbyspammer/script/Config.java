package net.bruhitsalex.lobbyspammer.script;

import com.google.gson.*;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Config {

    private static final HashMap<String, Object> data = new HashMap<>();

    public static void loadConfig(){
        File config = getFile();
        String text = "{}";
        try{
            byte[] byteArray = Files.readAllBytes(Paths.get(config.getPath()));
            String input = new String(byteArray);
            if(!input.isEmpty()){
                text = input;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        JsonObject json = new JsonParser().parse(text).getAsJsonObject();

        putValue("text1", json, "UP TO 150/M PER DAY NETHERWART/SUGARCANE/FISHING SCRIPT ->> taunahi.net/ezskyblockscripts");
        putValue("text2", json, "CHEAP, FAST, GUI, CUSTOMIZABLE SETTINGS, 24/7 SUPPORT, ALL AVAILABLE");
        saveConfig();
    }

    public static String getFirstMessage() {
        return (String) data.get("text1");
    }

    public static String getSecondMessage() {
        return (String) data.get("text2");
    }

    private static void putValue(String key, JsonObject json, Object defaultValue){
        JsonElement value = json.get(key);
        Object finalValue = value == null ? defaultValue : parseNative(value.getAsJsonPrimitive());
        data.put(key, finalValue);
    }

    private static Object parseNative(JsonPrimitive value){
        if(value.isNumber()){
            Number number = value.getAsNumber();
            if(number.doubleValue() == number.intValue()){
                return value.getAsInt();
            } else {
                return value.getAsDouble();
            }
        } else if(value.isBoolean()){
            return value.getAsBoolean();
        } else if(value.isString()){
            return value.getAsString();
        } else {
            return value;
        }
    }

    private static Object getFromValues(String name, Object[] values){
        for(Object value : values){
            if(name.equals(value.toString())){
                return value;
            }
        }
        return null;
    }

    public static void saveConfig(){
        File config = getFile();
        String json = new Gson().toJson(data);
        try {
            Files.write(Paths.get(config.getPath()), json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getFile(){
        File file = new File(Loader.instance().getConfigDir(), "lobbySpammer.cfg");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static <T> T get(String key){
        return (T) data.get(key);
    }

    public static void store(String key, Object value){
        data.put(key, value);
        saveConfig();
    }

}
