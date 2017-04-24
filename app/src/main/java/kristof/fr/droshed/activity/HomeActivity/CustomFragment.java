package kristof.fr.droshed.activity.HomeActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

import kristof.fr.droshed.Explorer.ItemExplorer;
import kristof.fr.droshed.R;
import kristof.fr.droshed.custom.CustomItemAdapter;

/**
 * Created by kristof
 * on 4/23/17.
 */

public class CustomFragment extends android.support.v4.app.Fragment {

    private GridView gridView;
    private List<ItemExplorer> itemExplorerList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridView = (GridView) view.findViewById(R.id.gridView);
        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey("list"))
            {
                itemExplorerList = args.getParcelableArrayList("list");
                gridView.setAdapter(new CustomItemAdapter(getActivity(),itemExplorerList));
            }
        }


    }
}
