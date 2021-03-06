package com.autogarcon.android;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.autogarcon.android.API.Allergen;
import com.autogarcon.android.API.MenuItem;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

/**
 * Activity to display all items within a category.
 * @author Riley Tschumper
 */
public class MenuItemListActivity extends AppCompatActivity {
    List<MenuItem> category;
    private RecyclerView recyclerView;
    private MenuItemListAdapter mAdapter;
    private SearchView searchView;
    private String title;
    private FloatingTextButton cartButton;
    private android.view.Menu filterMenu;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Adds the text passed from the previous activity to the title bar
        title = (String)getIntent().getSerializableExtra("title");
        setTitle(title);

        // sets category to the clicked category from the previous activity
        this.category = (List<MenuItem>) getIntent().getSerializableExtra("category");

        setContentView(R.layout.activity_menu_item_list);

        // Finds order total $ and displays the cart button if there are items in it
        cartButton = (FloatingTextButton) findViewById(R.id.c_button);
        double total = 0;
        if(ActiveSession.getInstance().getAllOrders().size() > 0){
            for (int index = 0; index < ActiveSession.getInstance().getAllOrders().size(); index++) {
                total = total + ActiveSession.getInstance().getAllOrders().get(index).getPrice();
            }
        }
        cartButton.setTitle("$" + String.format("%.2f", total));
        if(ActiveSession.getInstance().getAllOrders().size() == 0){
            cartButton.setVisibility(View.GONE);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new MenuItemListAdapter(category);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MenuItemListActivity.this, MenuItemFullActivity.class);
                intent.putExtra("item", category.get(position));
                intent.putExtra("title", title);
                ImageView image = ((MenuItemListAdapter.MyViewHolder)(recyclerView.getChildViewHolder(recyclerView.getChildAt(position)))).getMenuImage();
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MenuItemListActivity.this, (View)image, "itemImage");
                startActivityForResult(intent, 2, options.toBundle());
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartButton.setVisibility(View.GONE);
                ActiveSession.getInstance().setButtonFlag(true);
                setResult(4);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Apply the CustomTheme
        ActiveSession.getInstance().getCustomTheme().applyTo(this);

        // Displays the cart button if there are items in the cart
        if(ActiveSession.getInstance().getAllOrders().size() > 0){
            cartButton.setVisibility(View.VISIBLE);
            double total = 0;
            for (int index = 0; index < ActiveSession.getInstance().getAllOrders().size(); index++) {
                total = total + ActiveSession.getInstance().getAllOrders().get(index).getPrice();
            }
            cartButton.setTitle("$" + String.format("%.2f", total));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu){
        getMenuInflater().inflate(R.menu.menu_filtering,menu);

        filterMenu = menu;
        //Updates the filter menu based on the saved user preferences
        updateFilterPreferences();

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Input Text", "Search Text: " + newText );
                // filter recycler view when text is changed
                mAdapter.getFilter().filter("search" + newText);
                return false;
            }
        });

        return true;
    }
    /**
     * Activity to display all items within a category.
     * @param item a MenuItem to be checked if a filter should be applied
     * @author Riley Tschumper
     */
    public boolean onOptionsItemSelected(android.view.MenuItem item){
        switch(item.getItemId()){
            case R.id.meat_filter:
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    mAdapter.getFilter().filter("filternomeat");
                }else{
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    mAdapter.getFilter().filter("filtermeat");
                }
                return true;
            case R.id.nuts_filter:
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    mAdapter.getFilter().filter("filternonuts");
                }else{
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    mAdapter.getFilter().filter("filternuts");
                }
                return true;
            case R.id.dairy_filter:
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    mAdapter.getFilter().filter("filternodairy");
                }else{
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    mAdapter.getFilter().filter("filterdairy");
                }
                return true;
            case R.id.gluten_filter:
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    mAdapter.getFilter().filter("filternogluten");
                }else{
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    mAdapter.getFilter().filter("filtergluten");
                }
                return true;
            case R.id.soy_filter:
                if(item.isChecked()){
                    // If item already checked then unchecked it
                    item.setChecked(false);
                    mAdapter.getFilter().filter("filternosoy");
                }else{
                    // If item is unchecked then checked it
                    item.setChecked(true);
                    mAdapter.getFilter().filter("filtersoy");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Updates the filters to reflect the users saved preferences
     * @author Riley Tschumper
     */
    public void updateFilterPreferences(){
        ArrayList<Allergen> allergenPreferences = ActiveSession.getInstance().getAllergenPreferences();
        if(allergenPreferences != null) {
            if(allergenPreferences.contains(Allergen.MEAT)) {
                android.view.MenuItem meatItem = filterMenu.findItem(R.id.meat_filter);
                if (meatItem.isChecked()) {
                    // If item already checked then unchecked it
                    meatItem.setChecked(false);
                    mAdapter.getFilter().filter("filternomeat");
                }
            }
            if(allergenPreferences.contains(Allergen.NUTS)) {
                android.view.MenuItem nutsItem = filterMenu.findItem(R.id.nuts_filter);
                if (nutsItem.isChecked()) {
                    // If item already checked then unchecked it
                    nutsItem.setChecked(false);
                    mAdapter.getFilter().filter("filternonuts");
                }
            }
            if(allergenPreferences.contains(Allergen.GLUTEN)) {
                android.view.MenuItem glutenItem = filterMenu.findItem(R.id.gluten_filter);
                if (glutenItem.isChecked()) {
                    // If item already checked then unchecked it
                    glutenItem.setChecked(false);
                    mAdapter.getFilter().filter("filternogluten");
                }
            }
            if(allergenPreferences.contains(Allergen.DAIRY)) {
                android.view.MenuItem dairyItem = filterMenu.findItem(R.id.dairy_filter);
                if (dairyItem.isChecked()) {
                    // If item already checked then unchecked it
                    dairyItem.setChecked(false);
                    mAdapter.getFilter().filter("filternodairy");
                }
            }
            if(allergenPreferences.contains(Allergen.SOY)) {
                android.view.MenuItem soyItem = filterMenu.findItem(R.id.soy_filter);
                if (soyItem.isChecked()) {
                    // If item already checked then unchecked it
                    soyItem.setChecked(false);
                    mAdapter.getFilter().filter("filternosoy");
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
