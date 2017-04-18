package kristof.fr.droshed;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kristof
 * on 4/17/17.
 */

public class CustomItemAdapter extends ArrayAdapter<CustomItem> {

    public CustomItemAdapter(@NonNull Context context, ArrayList<CustomItem> list) {
        super(context,0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_item_layout, parent,false);
            convertView.requestLayout();
        }

        CustomItem item = getItem(position);
        TextView textView = (TextView) convertView.findViewById(R.id.textView2);
        textView.setText(item.getText());
        return convertView;
    }


}
