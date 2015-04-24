package suit.halo.suitcontroller;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class IconList extends ArrayAdapter<String>
{
    private final Activity context;
    private final String[] listOfChoices;

    public IconList(Activity context, String[] listOfChoices)
    {
        super(context, R.layout.list_single, listOfChoices);
        this.context = context;
        this.listOfChoices = listOfChoices;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_single, null, true);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.navigation_icon);
        imageView.setImageResource(Constants.getIconResource(listOfChoices[position]));
        return rowView;
    }
}