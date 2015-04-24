package suit.halo.suitcontroller;

import android.media.SoundPool;

public class SoundMessageHandler
{
    public static void handleSoundMessage(final String soundToPlay, final SoundPool soundPool, final int volume)
    {

                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(soundToPlay.equals("low_bat"))
                        {
                            soundPool.play(1,volume,volume,1,0,1);
                        }
                    }
                }).start();
    }
}
