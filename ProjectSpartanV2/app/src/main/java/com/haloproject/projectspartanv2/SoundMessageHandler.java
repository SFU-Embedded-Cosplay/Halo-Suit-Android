package com.haloproject.projectspartanv2;

import android.media.SoundPool;

import org.json.JSONException;
import org.json.JSONObject;

public class SoundMessageHandler
{
    public static void handleSoundMessage(JSONObject jsonObject, final SoundPool soundPool, final int volume)
    {
        if(jsonObject.has("play sound"))
        {
            try
            {
                final String soundToPlay = jsonObject.getString("play sound");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(soundToPlay.equals("lights"))
                        {
                            soundPool.play(1,volume,volume,1,0,1);
                        }
                        else if(soundToPlay.equals("shield_off"))
                        {
                            soundPool.play(2,volume,volume,1,0,1);
                        }
                        else if(soundToPlay.equals("shield_on"))
                        {
                            soundPool.play(3,volume,volume,1,0,1);
                        }
                    }
                }).start();
            } catch (JSONException e)
            {}

        }
    }
}
