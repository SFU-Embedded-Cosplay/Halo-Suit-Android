package suit.halo.suitcontroller;

import android.view.View;

public class ViewHelperFactory
{

    private static final String LOG_TAG = "ViewHelper";

    public static abstract class ViewHelper
    {


        protected View view;

        protected ViewHelper(View view)
        {
            this.view = view;
        }

        public abstract void postOnAnimation(Runnable action);

        public abstract void setScrollX(int value);

        public abstract boolean isHardwareAccelerated();
    }

    public static class ViewHelperDefault extends ViewHelper
    {

        public ViewHelperDefault(View view)
        {
            super(view);
        }

        @Override
        public void postOnAnimation(Runnable action)
        {
            view.postOnAnimation(action);
        }

        @Override
        public void setScrollX(int value)
        {
            view.setScrollX(value);
        }

        @Override
        public boolean isHardwareAccelerated()
        {
            return view.isHardwareAccelerated();
        }
    }

    public static final ViewHelper create(View view)
    {
        final int version = android.os.Build.VERSION.SDK_INT;

        return new ViewHelperDefault(view);

    }

}

