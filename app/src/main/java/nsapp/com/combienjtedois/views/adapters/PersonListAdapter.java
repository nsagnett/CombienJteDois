package nsapp.com.combienjtedois.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.utils.Utils;

public class PersonListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Person> personList = new ArrayList<>();

    private final boolean isEditingView;

    public PersonListAdapter(Context context, ArrayList<Person> personList, boolean isEditingView) {
        this.context = context;
        this.personList.addAll(personList);
        this.isEditingView = isEditingView;
    }

    @Override
    public int getCount() {
        return personList.size();
    }

    @Override
    public Person getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.money_holder, null);
        }

        Person person = personList.get(position);
        String name = person.getName();
        Double total = Double.parseDouble(person.getTotalAmount());

        ((TextView) convertView.findViewById(R.id.nameView)).setText(name);
        TextView dateView = ((TextView) convertView.findViewById(R.id.subTitleView));
        dateView.setText(String.format(context.getString(R.string.modification_date_format), person.getModificationDate()));
        dateView.setText(dateView.getText() + String.format(context.getString(R.string.lifetime_format), Utils.convertLifeTime(context, person.getModificationDate())));


        TextView countView = ((TextView) convertView.findViewById(R.id.rightView));
        countView.setText(String.format(context.getString(R.string.money_format), total));
        countView.setTextColor(total >= 0 ? context.getResources().getColor(R.color.green) : context.getResources().getColor(R.color.red));

        if (isEditingView) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.otherView);
            imageView.setImageResource(R.drawable.dark_edit);
            imageView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
