package kristof.fr.droshed.activity.HomeActivity;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import kristof.fr.droshed.Explorer.FileItemExplorer;
import kristof.fr.droshed.Explorer.FolderItemExplorer;
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

    public static void replaceFragment (Fragment fragment, FragmentActivity activity){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = activity.getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.flContent, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemExplorer itemExplorer = (ItemExplorer) parent.getItemAtPosition(position);
                if (itemExplorer instanceof FileItemExplorer) {
                    Snackbar.make(getView(), itemExplorer.getName(), Snackbar.LENGTH_SHORT);
                }
                if (itemExplorer instanceof FolderItemExplorer) {
                    FolderItemExplorer folderItemExplorer = (FolderItemExplorer) itemExplorer;
                    CustomFragment firstFragment = new CustomFragment();
                    Bundle args = new Bundle();
                    args.putParcelableArrayList("list",folderItemExplorer.getItemExplorerList());
                    // In case this activity was started with special instructions from an
                    // Intent, pass the Intent's extras to the fragment as arguments
                    firstFragment.setArguments(args);
                    // Add the fragment to the 'fragment_container' FrameLayout
                    // support package FragmentManager (getSupportFragmentManager).
                    replaceFragment(firstFragment,getActivity());
                }
            }
        });
        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey("list")) {
                itemExplorerList = args.getParcelableArrayList("list");
                gridView.setAdapter(new CustomItemAdapter(getActivity(), itemExplorerList));
            }
        }


    }
}
