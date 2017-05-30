package kristof.fr.droshed.activity.CreateModelActivity;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import kristof.fr.droshed.Explorer.FileItemExplorer;
import kristof.fr.droshed.R;
import kristof.fr.droshed.custom.CustomTextView;

/**
 * Created by kristof
 * on 5/28/17.
 */

public class CustomModelPickerAdapter extends ArrayAdapter<FileItemExplorer> {

    public CustomModelPickerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<FileItemExplorer> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FileItemExplorer item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.model_create_file_picker, parent, false);
            convertView.requestLayout();
        }
        CustomTextView textView = (CustomTextView) convertView.findViewById(R.id.customView);
        textView.setText(item.getName());
        return convertView;
    }
}
