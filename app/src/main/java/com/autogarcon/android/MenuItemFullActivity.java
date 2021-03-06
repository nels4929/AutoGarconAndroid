package com.autogarcon.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.autogarcon.android.API.MenuItem;
import com.autogarcon.android.API.OrderItem;


/**
 * Activity for the full view of a single menu item.
 * @author Tim Callies
 */
public class MenuItemFullActivity extends AppCompatActivity {

    private ImageView largeImage;
    private MenuItem menuItem;
    private TextView name;
    private TextView description;
    private TextView price;
    private EditText chefNote;
    private Button button;
    private String title;
    private TextView charCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Adds the text passed from the previous activity to the title bar
        title = (String)getIntent().getSerializableExtra("title");
        setTitle(title);

        // sets menuItem to the clicked item from the previous activity
        this.menuItem = (MenuItem) getIntent().getSerializableExtra("item");

        setContentView(R.layout.activity_menu_item_full);

        charCount = (TextView) findViewById(R.id.charCount);
        button = (Button) findViewById(R.id.button);
        name = (TextView) findViewById(R.id.itemFullName);
        description = (TextView) findViewById(R.id.itemFullDescription);
        price = (TextView) findViewById(R.id.itemFullPrice);
        largeImage = (ImageView) findViewById(R.id.itemFullImage);
        chefNote = (EditText) findViewById(R.id.itemFullChefNotes);
        ThumbnailManager.getInstance().getImage(menuItem.getImageURL(),largeImage);

        name.setText(menuItem.getName());
        description.setText(menuItem.getDescription());
        price.setText(String.format("$%.2f",menuItem.getPrice()));

        chefNote.addTextChangedListener(mTextEditorWatcher);

        // creates an OrderItem and adds it to order once button is clicked
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderItem orderItem = new OrderItem(menuItem, chefNote.getText().toString());
                ActiveSession.getInstance().addOrder(orderItem);
                setResult(2);
                finish();
            }
        });
    }


    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            charCount.setText("                                                                     " + (250 - s.toString().length()) + "/250");
        }

        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Apply the CustomTheme
        ActiveSession.getInstance().getCustomTheme().applyTo(this);
    }
}

