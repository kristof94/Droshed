package kristof.fr.droshed.activity.HomeActivity;

import android.app.Fragment;
import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class CustomFragment extends Fragment {

    public ArrayList<ItemExplorer> getItemExplorerList() {
        return itemExplorerList;
    }

    private ArrayList<ItemExplorer> itemExplorerList;
    private CustomItemAdapter customAdapter;
    private String path;
    private FolderManager link;
    private ItemExplorer itemExplorer;

    public interface FolderManager {
        void refresh(CustomFragment customFragment);

        void manageItem(String path, FileItemExplorer fileItemExplorer);

        void addFragmentToStack(Bundle args);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        link = (FolderManager) getActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                link.refresh(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreateFuckingFragment");
        setRetainInstance(true);
        setHasOptionsMenu(true);
        if (itemExplorerList == null) {
            itemExplorerList = new ArrayList<>();
        }
        Bundle bundle = getArguments();
        if (bundle != null) {
            path = bundle.getString("path");
            itemExplorer = bundle.getParcelable("itemExplorer");
            if (bundle.containsKey("itemExplorerList")) {
                itemExplorerList.addAll(bundle.getParcelableArrayList("itemExplorerList"));
            }
            return;
        }
        if (savedInstanceState != null) {
            path = savedInstanceState.getString("path");
            itemExplorer = savedInstanceState.getParcelable("itemExplorer");
            itemExplorerList.addAll(savedInstanceState.getParcelableArrayList("itemExplorerList"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("path", path);
        outState.putParcelableArrayList("itemExplorerList", itemExplorerList);
        outState.putParcelable("itemExplorer", itemExplorer);
        super.onSaveInstanceState(outState);
    }

    static public CustomFragment createNewFragment(String root) {
        CustomFragment firstFragment = new CustomFragment();
        Bundle args = new Bundle();
        args.putString("path", root);
        firstFragment.setArguments(args);
        return firstFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemExplorer itemExplorer = (ItemExplorer) parent.getItemAtPosition(position);
                if (itemExplorer instanceof FileItemExplorer) {
                    FileItemExplorer fileItemExplorer = (FileItemExplorer) itemExplorer;
                    link.manageItem(path + "/" + fileItemExplorer.getName(), fileItemExplorer);
                }
                if (itemExplorer instanceof FolderItemExplorer) {
                    FolderItemExplorer folderItemExplorer = (FolderItemExplorer) itemExplorer;
                    CustomFragment firstFragment = new CustomFragment();
                    Bundle args = new Bundle();
                    StringBuilder sb = new StringBuilder(path);
                    sb.append("/").append(folderItemExplorer.getName());
                    args.putString("path", sb.toString());
                    args.putParcelableArrayList("itemExplorerList", folderItemExplorer.getItemExplorerList());
                    firstFragment.setArguments(args);
                    link.addFragmentToStack(args);
                    //updateGridViewList(folderItemExplorer.getItemExplorerList());
                }
            }
        });
        customAdapter = new CustomItemAdapter(getActivity(), itemExplorerList);
        gridView.setAdapter(customAdapter);
        return view;
    }

    public void updateGridViewList(List<ItemExplorer> s) {
        itemExplorerList.clear();
        itemExplorerList.addAll(s);
        customAdapter.notifyDataSetChanged();
    }
}
