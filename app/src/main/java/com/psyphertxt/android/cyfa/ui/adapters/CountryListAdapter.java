package com.psyphertxt.android.cyfa.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.model.Country;
import com.psyphertxt.android.cyfa.util.Fonts;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class CountryListAdapter extends BaseAdapter {

    private Context context;
    List<Country> countries;
    LayoutInflater inflater;

    /**
     * The drawable image name has the format "flag_$countryCode". We need to
     * load the drawable dynamically from country code. Code from
     * http://stackoverflow.com/
     * questions/3042961/how-can-i-get-the-resource-id-of
     * -an-image-if-i-know-its-name
     *
     * @param drawableName
     * @return
     */
    private int getResId(String drawableName) {

		/*try {
            Class<Drawable> res = Drawable.class;
			Field field = res.getField(drawableName);
			int drawableId = field.getInt(null);
			return drawableId;
		} catch (Exception e) {
			Log.e("COUNTRYPICKER", "Failure to get drawable id.", e);
		}*/
        try {
            Class res = R.drawable.class;
            Field field = res.getField(drawableName);
            int drawableId = field.getInt(null);
            return drawableId;
        } catch (Exception e) {
            //Log.e("MyTag", "Failure to get drawable id.", e);
        }
        return -1;
    }

    /**
     * Constructor
     *
     * @param context
     * @param countries
     */
    public CountryListAdapter(Context context, List<Country> countries) {
        super();
        this.context = context;
        this.countries = countries;
        inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    /**
     * Return country_item for each country
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cellView = convertView;
        Cell cell;
        Country country = countries.get(position);

        if (convertView == null) {
            cell = new Cell();
            cellView = inflater.inflate(R.layout.country_item, parent, false);
            cell.textView = (TextView) cellView.findViewById(R.id.row_title);
            cellView.setTag(cell);
        } else {
            cell = (Cell) cellView.getTag();
        }

        //TODO update implementation to use xml fonts
        //load application font for consistency in UI
        Fonts.regularFont(cell.textView, context);

        cell.textView.setText(country.getName());

        // Load country flags dynamically from country code
        String drawableName = "flag_"
                + country.getCode().toLowerCase(Locale.ENGLISH);
        try {
            cell.textView.setCompoundDrawablesWithIntrinsicBounds(getResId(drawableName), 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cellView;
    }

    /**
     * Holder for the cell
     */
    static class Cell {
        public TextView textView;
        public ImageView imageView;
    }

}