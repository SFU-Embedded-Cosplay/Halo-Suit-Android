package suit.halo.suitcontroller;

import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Constants
{
    public static String getPhoneDeviceIdentifier()
    {
        return phoneDeviceIdentifier;
    }

    private static void setPhoneDeviceIdentifier(String phoneDeviceIdentifier)
    {
        Constants.phoneDeviceIdentifier = phoneDeviceIdentifier;
    }

    private static String phoneDeviceIdentifier;
    public static  String audioDeviceUuid;
    private static final String configFileName = "suitConfig";

    public static String OK_GLASS;

    public static String DEVICE_IDENTIFIER;

    public static final int DEVICE_CHANNEL = 2;

    public static enum VOICE_MENU_MODE
    {
        KEYWORD, FIRST_LEVEL, SECOND_LEVEL
    }


    public static final String[] getFirstWords()
    {
        List<String> ret = new LinkedList<>();

        Map<String, String[]> commandPhrases = getCommandPhrases();
        for (String s : commandPhrases.keySet())
        {
            ret.add(s);
        }
        String[] retArr = new String[ret.size()];
        return ret.toArray(retArr);
    }

    public static final String[] getSecondWords(String firstWord)
    {
        List<String> ret = new LinkedList<>();

        Map<String, String[]> commandPhrases = getCommandPhrases();
        for (String s : commandPhrases.get(firstWord))
        {
            ret.add(s);
        }
        String[] retArr = new String[ret.size()];
        return ret.toArray(retArr);
    }


    public static final Map<String, String[]> getCommandPhrases()
    {
        Map<String, String[]> commandPhrases = new HashMap<>();
        String[] cooling = new String[2];
        cooling[0] = "on";
        cooling[1] = "off";
        String[] lights = new String[3];
        lights[0] = "on";
        lights[1] = "off";
        lights[2] = "auto";
        String[] headLights = new String[2];
        headLights[0] = "on";
        headLights[1] = "off";
        commandPhrases.put("cooling_on", cooling);
        commandPhrases.put("lights", lights);
        commandPhrases.put("head lights", headLights);

        return commandPhrases;
    }

    public static final Integer getIconResource(String info)
    {
        switch (info)
        {
            case "cooling_on":
                return R.drawable.cooling_on;
            case "lights":
                return R.drawable.lights;
            case "head lights":
                return R.drawable.head_light;
            case "on":
                return R.drawable.icon_low;
            case "off":
                return R.drawable.icon_off;
            case "auto":
                return R.drawable.icon_high;
            default:
                return R.drawable.ic_glass_logo;
        }
    }

    public static final void initializeConstants()
    {
        File sdcard = Environment.getExternalStorageDirectory();
        File settingsFile = new File(sdcard,"Pictures"+File.separator+configFileName);
        try
        {
            BufferedReader fileContentsReader = new BufferedReader(new FileReader(settingsFile));
            StringBuilder text = new StringBuilder();

            String line;
            while ((line = fileContentsReader.readLine()) != null) {
                text.append(line);
            }
            fileContentsReader.close();

            JSONObject jsonObject = new JSONObject(text.toString());
            setPhoneDeviceIdentifier(jsonObject.getString("audioDeviceIdentifier"));
            audioDeviceUuid = jsonObject.getString("uuid");
            OK_GLASS = jsonObject.getString("okGlass");
            DEVICE_IDENTIFIER = jsonObject.getString("beagleBoneIdentifier");
            int x = 1;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
    }
}