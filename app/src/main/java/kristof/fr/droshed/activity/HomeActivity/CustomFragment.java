package kristof.fr.droshed.activity.HomeActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
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

public class CustomFragment extends  android.support.v4.app.Fragment {

    private List<ItemExplorer> itemExplorerList = new ArrayList<>();
    private int idFragment;
    private CustomItemAdapter customAdapter;
    private boolean isLoaded;

    public boolean isLoaded() {
        return isLoaded;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        isLoaded = true;
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

    public void updateGridViewList(List<ItemExplorer> s) {
        itemExplorerList.clear();
        itemExplorerList.addAll(s);
        customAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            ItemExplorer itemExplorer = (ItemExplorer) parent.getItemAtPosition(position);
            if (itemExplorer instanceof FileItemExplorer) {
                Snackbar.make(getView(), itemExplorer.getName(), Snackbar.LENGTH_SHORT);
            }
            if (itemExplorer instanceof FolderItemExplorer) {
                FolderItemExplorer folderItemExplorer = (FolderItemExplorer) itemExplorer;
                CustomFragment firstFragment = new CustomFragment();
                Bundle args = new Bundle();
                args.putInt("id",idFragment);
                args.putParcelableArrayList("list",folderItemExplorer.getItemExplorerList());
                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                firstFragment.setArguments(args);
                // Add the fragment to the 'fragment_container' FrameLayout
                // support package FragmentManager (getSupportFragmentManager).
                replaceFragment(firstFragment,getActivity());
            }
        });
        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.containsKey("list")) {
                idFragment = args.getInt("id");
                itemExplorerList.addAll(args.getParcelableArrayList("list"));
                customAdapter = new CustomItemAdapter(getActivity(),itemExplorerList);
                gridView.setAdapter(customAdapter);
            }
        }


    }
}
