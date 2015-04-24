package suit.halo.suitcontroller;

import android.content.Context;

import com.google.glass.voice.VoiceCommand;
import com.google.glass.voice.VoiceConfig;
import com.google.glass.voice.VoiceInputHelper;

import java.util.LinkedList;
import java.util.Map;

public class VoiceDetection extends StubVoiceListener
{

    private static final String THIS = VoiceDetection.class.getSimpleName();

    private final VoiceConfig mVoiceConfig;
    private String[] keyWordPhrases;
    private VoiceInputHelper mVoiceInputHelper;
    private VoiceDetectionListener mListener;
    private boolean mRunning = true;
    Constants.VOICE_MENU_MODE mode;
    String firstWord;

    public VoiceDetection(Context context, String keyWord, VoiceDetectionListener listener, String[] junkWords)
    {
        mVoiceInputHelper = new VoiceInputHelper(context, this);

        keyWordPhrases = assemblePhrases(new String[]{keyWord}, new String[0], junkWords);

        mVoiceConfig = new VoiceConfig();
        mVoiceConfig.setShouldSaveAudio(false);

        mListener = listener;
    }

    private String[] assemblePhrases(String[] hotword, String[] commands, String[] junkWords)
    {
        LinkedList<String> combinedPhraseList = new LinkedList<>();
        for (String s : hotword)
        {
            combinedPhraseList.add(s);
        }
        for (String s : commands)
        {
            combinedPhraseList.add(s);
        }
        for (String s : junkWords)
        {
            combinedPhraseList.add(s);
        }
        return combinedPhraseList.toArray(new String[combinedPhraseList.size()]);
    }

    public void changePhrases(Constants.VOICE_MENU_MODE mode, String... category)
    {
        if(category.length > 0)
        {
            firstWord = category[0];
        }

        switch (mode)
        {
            case KEYWORD:
            {
                mVoiceConfig.setCustomPhrases(keyWordPhrases);
                mVoiceInputHelper.setVoiceConfig(mVoiceConfig);
                this.mode = Constants.VOICE_MENU_MODE.KEYWORD;
            }
            break;
            case FIRST_LEVEL:
            {
                {
                    Map<String, String[]> commandPhrases = Constants.getCommandPhrases();
                    String[] firstPhrases = new String[commandPhrases.keySet().size()];
                    Constants.getCommandPhrases().keySet().toArray(firstPhrases);
                    mVoiceConfig.setCustomPhrases(firstPhrases);
                    mVoiceInputHelper.setVoiceConfig(mVoiceConfig);
                    this.mode = Constants.VOICE_MENU_MODE.FIRST_LEVEL;
                }
                break;
            }
            case SECOND_LEVEL:
            {
                {
                    Map<String, String[]> commandPhrases = Constants.getCommandPhrases();
                    String[] secondPhrases = commandPhrases.get(firstWord);
                    mVoiceConfig.setCustomPhrases(secondPhrases);
                    mVoiceInputHelper.setVoiceConfig(mVoiceConfig);
                    this.mode = Constants.VOICE_MENU_MODE.SECOND_LEVEL;
                }
                break;
            }
        }
    }

    @Override
    public VoiceConfig onVoiceCommand(VoiceCommand vc)
    {
        String literal = vc.getLiteral();

        switch (mode)
        {
            case KEYWORD:
            {
                if(literal.equals(Constants.OK_GLASS))
                {
                    mListener.onHotwordDetected();
                    return null;
                }
            }
            break;
            case FIRST_LEVEL:
            {
                Map<String, String[]> commandPhrases = Constants.getCommandPhrases();
                String[] firstPhrases = new String[commandPhrases.keySet().size()];
                Constants.getCommandPhrases().keySet().toArray(firstPhrases);

                for (int i = 0; i < firstPhrases.length; ++i)
                {
                    String item = firstPhrases[i];
                    if(item.equalsIgnoreCase(literal))
                    {
                        mListener.onFirstWordDetected(i, literal);
                        return null;
                    }
                }
            }
            break;
            case SECOND_LEVEL:
            {
                {
                    Map<String, String[]> commandPhrases = Constants.getCommandPhrases();
                    String[] secondPhrases = commandPhrases.get(firstWord);

                    for (int i = 0; i < secondPhrases.length; ++i)
                    {
                        String item = secondPhrases[i];
                        if(item.equalsIgnoreCase(literal))
                        {
                            mListener.onSecondWordDetected(i, literal);
                            return null;
                        }
                    }
                }
                break;
            }
        }
        return null;
    }

    public void start()
    {
        mRunning = true;
        mVoiceInputHelper.setVoiceConfig(mVoiceConfig);
    }

    public void stop()
    {
        mRunning = false;
        mVoiceInputHelper.setVoiceConfig(null);
    }

    @Override
    public boolean isRunning()
    {
        return mRunning;
    }

    public interface VoiceDetectionListener
    {
        public void onHotwordDetected();

        public void onFirstWordDetected(int index, String phrase);

        public void onSecondWordDetected(int index, String phrase);
    }
}

