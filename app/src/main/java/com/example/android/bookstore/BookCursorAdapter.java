package com.example.android.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstore.data.BookContract.BookEntry;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    public static final String LOG_TAG = BookCursorAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView productNameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        Button buyButton = view.findViewById(R.id.buy);
        TextView quantityTextView = view.findViewById(R.id.quantity);

        // Find the columns of book attributes that we're interested in
        int bookTitleColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);

        // Read the book attributes from the Cursor for the current book
        String BookTitle = cursor.getString(bookTitleColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        final int bookId = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));

        // Update the TextViews with the attributes for the current book
        productNameTextView.setText(BookTitle);
        priceTextView.setText(String.valueOf(price));
        quantityTextView.setText(String.valueOf(quantity));

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri BookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);
                decreaseBookQuantity(context, BookUri, quantity);
            }
        });

    }

    private void decreaseBookQuantity(Context context, Uri bookUri, int quantity) {

        if (quantity == 0) {
            Toast.makeText(context.getApplicationContext(), R.string.out_of_stock, Toast.LENGTH_SHORT).show();
            return;
        } else {
            quantity = quantity - 1;
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
            int rowsAffected = context.getContentResolver().update(bookUri, contentValues, null, null);

            // Show a log depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Log.e(LOG_TAG, context.getString(R.string.editor_update_book_failed));
            } else {
                // Otherwise, the update was successful and we can display a log.
                Log.i(LOG_TAG, context.getString(R.string.editor_update_book_successful));
            }
        }
    }
}
