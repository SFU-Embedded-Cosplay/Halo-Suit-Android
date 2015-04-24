package suit.halo.suitcontroller;

import com.google.glass.logging.FormattingLogger;
import com.google.glass.logging.FormattingLoggers;
import com.google.glass.voice.VoiceListener;

public abstract class StubVoiceListener extends VoiceListener.SimpleVoiceListener
{

    @Override
    public FormattingLogger getLogger()
    {
        return FormattingLoggers.getContextLogger();
    }
}

