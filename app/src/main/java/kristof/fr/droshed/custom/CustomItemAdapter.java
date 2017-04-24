package kristof.fr.droshed.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kristof.fr.droshed.Explorer.FileItemExplorer;
import kristof.fr.droshed.Explorer.FolderItemExplorer;
import kristof.fr.droshed.Explorer.ItemExplorer;
import kristof.fr.droshed.R;

/**
 * Created by kristof
 * on 4/17/17.
 */

public class CustomItemAdapter extends ArrayAdapter<ItemExplorer> {

    public CustomItemAdapter(@NonNull Context context, List<ItemExplorer> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ItemExplorer item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(item.getLayoutID(), parent, false);
            convertView.requestLayout();
        }

        TextView textView = (TextView) convertView.findViewById(R.id.textView2);
        textView.setText(item.getName());
        return convertView;
    }


}
