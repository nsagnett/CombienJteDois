package nsapp.com.combienjtedois.views.adapters;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.AllDatas;
import nsapp.com.combienjtedois.model.Person;
import nsapp.com.combienjtedois.model.ViewCreator;
import nsapp.com.combienjtedois.views.fragments.AbstractMoneyFragment;
import nsapp.com.combienjtedois.views.fragments.MoneyFragment;

public class PersonListAdapter extends BaseAdapter {

    private final Context context;
    private final MoneyFragment fragment;
    private final ArrayList<Person> personList = new ArrayList<Person>();

    private final boolean isDeletingView;
    private final boolean isEditingView;

    public PersonListAdapter(Context context, MoneyFragment fragment, ArrayList<Person> personList, boolean isDeletingView, boolean isEditingView) {
        this.context = context;
        this.fragment = fragment;
        this.personList.addAll(personList);
        this.isDeletingView = isDeletingView;
        this.isEditingView = isEditingView;
    }

    @Override
    public int getCount() {
        return personList.size();
    }

    @Override
    public Object getItem(int position) {
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
            convertView = inflater.inflate(R.layout.linear_container, null);
        }

        final Person person = personList.get(position);
        String name = person.getName();
        Double total = Double.parseDouble(person.getTotalAmount());

        ((TextView) convertView.findViewById(R.id.nameView)).setText(name);

        ImageView profileImage = ((ImageView) convertView.findViewById(R.id.profileView));

        TextView countView = ((TextView) convertView.findViewById(R.id.countView));
        countView.setText(String.format(context.getString(R.string.money_format), total));
        countView.setTextColor(total >= 0 ? context.getResources().getColor(R.color.green) : context.getResources().getColor(R.color.red));
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ListView listView = new ListView(context);
                String[] items = new String[]{context.getString(R.string.camera), context.getString(R.string.gallery), context.getString(R.string.nothing)};
                SimpleListAdapter adapter = new SimpleListAdapter(context, items);
                listView.setAdapter(adapter);
                final AlertDialog dialog = ViewCreator.createListViewDialogBox(context, listView);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        fragment.setPersonExtra(person);
                        dialog.dismiss();
                        if (position == 0) {
                            String fileName = "temp.jpg";
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, fileName);
                            ((AbstractMoneyFragment) fragment).setCapturedImageURI(context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values));

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, ((AbstractMoneyFragment) fragment).getCapturedImageURI());
                            fragment.startActivityForResult(intent, AllDatas.TAKE_PICTURE_FOR_PERSON);
                        } else if (position == 1) {
                            Intent galleryPicture = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            fragment.startActivityForResult(galleryPicture, AllDatas.IMPORT_PERSON_IMAGE_CODE);
                        } else {
                            AllDatas.dbManager.setImageProfileUrlPerson(person.getId(), "");
                            fragment.notifyChanges();
                        }
                    }
                });
                dialog.show();
            }
        });

        String pathImage = person.getImageProfileUrl();

        if (pathImage != null && !pathImage.isEmpty()) {
            Bitmap image = AllDatas.getImageFromPath(pathImage);
            if (image != null) {
                image = Bitmap.createScaledBitmap(image, AllDatas.SIZE_IMAGE, AllDatas.SIZE_IMAGE, true);
                profileImage.setImageBitmap(ViewCreator.getRoundedShape(image));
                profileImage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        ImageView view = new ImageView(context);
                        view.setImageBitmap(BitmapFactory.decodeFile(person.getImageProfileUrl()));
                        builder.setView(view);
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        return true;
                    }
                });
            }
        }

        if (isDeletingView) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.otherView);
            imageView.setImageResource(R.drawable.dark_delete);
            imageView.setVisibility(View.VISIBLE);
        } else if (isEditingView) {
            ImageView imageView = (ImageView) convertView.findViewById(R.id.otherView);
            imageView.setImageResource(R.drawable.dark_edit);
            imageView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
