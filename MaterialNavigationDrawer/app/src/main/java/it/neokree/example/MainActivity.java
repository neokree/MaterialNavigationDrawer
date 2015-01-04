package it.neokree.example;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import it.neokree.example.NavigationDrawerWithAccountActivity;

/**
 * Created by neokree on 30/12/14.
 */
public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> list = new ArrayList<String>();

        list.add("Example with Accounts");
        list.add("Example with multi pane support");
        list.add("Example with ripple Backport Support enabled");
        list.add("Example with no drawer header");
        list.add("Example with bitmap drawer header");
        list.add("Example with custom drawer Header");
        //list.add("Example Test");

        this.setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list));
        this.getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch(position) {
            case 0:
                intent = new Intent(this,NavigationDrawerWithAccountActivity.class);
                break;
            case 1:
                intent = new Intent(this,NavigationDrawerActivityMultipane.class);
                break;
            case 2:
                intent = new Intent(this,NavigationDrawerActivityMultipane.class);
                break;
            case 3:
                intent = new Intent(this,NavigationDrawerNoHeaderActivity.class);
                break;
            case 4:
                intent = new Intent(this,NavigationDrawerImageHeaderActivity.class);
                break;
            case 5:
                intent = new Intent(this,NavigationDrawerCustomHeaderActivity.class);
                break;
            case 6: // used for testing issues
                intent = new Intent(this,NavigationDrawerTest.class);
                break;
            default:
                intent = null;
        }
        startActivity(intent);
    }
}
