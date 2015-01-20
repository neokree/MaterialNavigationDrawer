package it.neokree.example.functionalities.master_child;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.neokree.example.R;

/**
 * Created by neokree on 20/01/15.
 */
public class ChildFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("MaterialNavigationDrawer Master-Child","Child created");

        return inflater.inflate(R.layout.masterchild_child,container,false);
    }

}
