package com.example.android.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract.BookEntry;


/**
 * Allows user to see the detail of an existing book.
 */
public class ProductDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Content URI for the existing book
     */
    private Uri mCurrentBookUri;

    /**
     * TextView field for the book's title
     */
    private TextView mTitleTextView;

    /**
     * TextView field for the book's author
     */
    private TextView mAuthorTextView;

    /**
     * TextView field for the book's price
     */
    private TextView mPriceTextView;

    /**
     * TextView field for the book's quantity
     */
    private TextView mQuantityTextView;

    /**
     * TextView field for the supplier's name
     */
    private TextView mSupplierNameTextView;

    /**
     * TextView field for the supplier's number
     */
    private TextView mNumberTextView;

    /**
     * Button field for increase book quantity
     */
    private Button mIncreaseButton;

    /**
     * Button field for decrease book quantity
     */
    private Button mDecreaseButton;

    /**
     * The book quantity
     */
    int quantity;

    /**
     * The supplier's number
     */
    Long number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // Find all relevant views that we will need to read user input from
        mTitleTextView = findViewById(R.id.edit_book_title);
        mAuthorTextView = findViewById(R.id.edit_book_author);
        mPriceTextView = findViewById(R.id.edit_book_price);
        mQuantityTextView = findViewById(R.id.edit_book_quantity);
        mSupplierNameTextView = findViewById(R.id.edit_supplier_name);
        mNumberTextView = findViewById(R.id.edit_supplier_number);
        mIncreaseButton = findViewById(R.id.increase_quantity);
        mDecreaseButton = findViewById(R.id.decrease_quantity);

        // Initialize a loader to read the book data from the database
        // and display the current values in the detail layout
        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);


        mDecreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseBookQuantity(mCurrentBookUri, quantity);
            }
        });

        mIncreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseBookQuantity(mCurrentBookUri, quantity);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_edit:
                // Open editor activity to edit book detail
                Intent intent = new Intent(ProductDetailActivity.this, EditorActivity.class);
                intent.setData(mCurrentBookUri);
                startActivity(intent);
                return true;

            // Respond to a click on the "Order" menu option
            case R.id.action_order:
                // Order using the supplier phone number
                Intent i = new Intent(Intent.ACTION_DIAL);
                String numberString = String.valueOf(number);
                i.setData(Uri.parse("tel:" + numberString));
                if (i.resolveActivity(this.getPackageManager()) != null) {
                    startActivity(i);
                }

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current book
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int numberColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            number = cursor.getLong(numberColumnIndex);

            // Update the views on the screen with the values from the database
            mTitleTextView.setText(title);
            mAuthorTextView.setText(author);
            mPriceTextView.setText(Integer.toString(price));
            mQuantityTextView.setText(Integer.toString(quantity));
            mSupplierNameTextView.setText(supplierName);
            mNumberTextView.setText(Long.toString(number));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mTitleTextView.setText("");
        mAuthorTextView.setText("");
        mPriceTextView.setText("");
        mQuantityTextView.setText("");
        mSupplierNameTextView.setText("");
        mNumberTextView.setText("");
    }

    private void decreaseBookQuantity(Uri bookUri, int quantity) {
        if (quantity == 0) {
            Toast.makeText(getApplicationContext(), R.string.out_of_stock, Toast.LENGTH_SHORT).show();
            return;
        } else {
            quantity = quantity - 1;
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
            int rowsAffected = getContentResolver().update(bookUri, contentValues, null, null);

        }
    }

    private void increaseBookQuantity(Uri bookUri, int quantity) {
        quantity = quantity + 1;
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        int rowsAffected = getContentResolver().update(bookUri, contentValues, null, null);

    }


    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}

