package kristof.fr.droshed.activity.HomeActivity;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;

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
        return folderItemExplorer.getItemExplorerList();
    }

    private CustomItemAdapter customAdapter;
    private FolderManager link;
    private FolderItemExplorer folderItemExplorer;

    public interface FolderManager {

        CustomFragment getFragment(FolderItemExplorer fileItemExplorer);

        void manageItem(FileItemExplorer fileItemExplorer);

        void addFragmentToHashMap(FolderItemExplorer folderItemExplorer, CustomFragment customFragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        link = (FolderManager) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreateFuckingFragment");
        setRetainInstance(true);
        setHasOptionsMenu(true);
        System.out.println("onCReate");
        Bundle bundle = getArguments();
        if (bundle != null) {
            folderItemExplorer = bundle.getParcelable("folderItemExplorer");
            if (folderItemExplorer.getItemExplorerList() == null) {
                folderItemExplorer.setItemExplorerList(new ArrayList<>());
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("itemExplorerList", folderItemExplorer.getItemExplorerList());
        outState.putParcelable("itemExplorer", folderItemExplorer);
        super.onSaveInstanceState(outState);
    }

    static public CustomFragment createNewFragment(FolderItemExplorer folderItemExplorer) {
        CustomFragment firstFragment = new CustomFragment();
        Bundle args = new Bundle();
        args.putParcelable("folderItemExplorer", folderItemExplorer);
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
                    link.manageItem(fileItemExplorer);
                }
                if (itemExplorer instanceof FolderItemExplorer) {
                    FolderItemExplorer folderItemExplorer = (FolderItemExplorer) itemExplorer;
                    CustomFragment customFragment = link.getFragment(folderItemExplorer);
                    if (customFragment == null) {
                        customFragment = createNewFragment(folderItemExplorer);
                    }
                    link.addFragmentToHashMap(folderItemExplorer, customFragment);
                }
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ItemExplorer itemExplorer = (ItemExplorer) parent.getItemAtPosition(position);
                Snackbar bar = Snackbar.make(view, getString(R.string.delete)+itemExplorer.toString(), Snackbar.LENGTH_INDEFINITE).setActionTextColor(Color.RED)
                        .setAction(getString(R.string.delete), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.out.println(folderItemExplorer.getItemExplorerList().remove(itemExplorer));
                                customAdapter.notifyDataSetChanged();
                                File file = new File(itemExplorer.getPath());
                                file.delete();
                            }
                        });
                bar.show();
                return true;
            }
        });

        customAdapter = new CustomItemAdapter(getActivity(), folderItemExplorer.getItemExplorerList());
        gridView.setAdapter(customAdapter);
        return view;
    }

    public void updateGridViewList(ArrayList<ItemExplorer> s) {
        folderItemExplorer.setItemExplorerList(s);
        customAdapter.notifyDataSetChanged();
    }
}
