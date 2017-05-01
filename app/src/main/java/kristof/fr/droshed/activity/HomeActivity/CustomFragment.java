package kristof.fr.droshed.activity.HomeActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    private AtomicInteger idFragment = new AtomicInteger(0);
    private CustomItemAdapter customAdapter;
    private String path;
    private boolean isLoaded;
    private ManagerHashMap link;


    public interface ManagerHashMap {
        public void addToHashMap(CustomFragment customFragment);
        public void manageItem(FileItemExplorer fileItemExplorer);
    }

    public boolean isLoaded() {
        return isLoaded;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        link = (ManagerHashMap) getActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                link.addToHashMap(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        isLoaded = true;
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            ItemExplorer itemExplorer = (ItemExplorer) parent.getItemAtPosition(position);
            if (itemExplorer instanceof FileItemExplorer) {
                FileItemExplorer fileItemExplorer = (FileItemExplorer) itemExplorer;
                link.manageItem(fileItemExplorer);
            }
            if (itemExplorer instanceof FolderItemExplorer) {
                FolderItemExplorer folderItemExplorer = (FolderItemExplorer) itemExplorer;
                CustomFragment firstFragment = new CustomFragment();
                Bundle args = new Bundle();
                //args.putInt("id",idFragment);
                StringBuilder sb = new StringBuilder(path);
                sb.append("/").append(folderItemExplorer.getName());
                args.putString("path",sb.toString());
                args.putParcelableArrayList("list",folderItemExplorer.getItemExplorerList());
                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                firstFragment.setArguments(args);
                // Add the fragment to the 'fragment_container' FrameLayout
                // support package FragmentManager (getSupportFragmentManager).
                replaceFragment(firstFragment,getActivity());
            }

        });
        if (getArguments() != null && idFragment.getAndIncrement()==0) {
            Bundle args = getArguments();
            if (args.containsKey("list")) {
                path = args.getString("path");
                itemExplorerList.addAll(args.getParcelableArrayList("list"));
            }
        }
        customAdapter = new CustomItemAdapter(getActivity(),itemExplorerList);
        gridView.setAdapter(customAdapter);
        return view;
    }

    public void replaceFragment (Fragment fragment, FragmentActivity activity){
        String backStateName = fragment.getClass().getName();
        FragmentManager manager = activity.getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.flContent, fragment,path);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public void updateGridViewList(List<ItemExplorer> s) {
        itemExplorerList.clear();
        itemExplorerList.addAll(s);
        customAdapter.notifyDataSetChanged();
    }

    public String getPath() {
        return path;
    }

}
