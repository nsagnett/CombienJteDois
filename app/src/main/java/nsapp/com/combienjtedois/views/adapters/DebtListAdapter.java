package nsapp.com.combienjtedois.views.adapters;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Debt;
import nsapp.com.combienjtedois.model.Utils;
import nsapp.com.combienjtedois.model.ViewCreator;
import nsapp.com.combienjtedois.views.activities.FullScreenImageActivity;
import nsapp.com.combienjtedois.views.fragments.money.AbstractMoneyFragment;
import nsapp.com.combienjtedois.views.fragments.money.DetailPersonFragment;

public class DebtListAdapter extends BaseAdapter implements View.OnClickListener {

    private final Context context;
    private DetailPersonFragment fragment;
    private final ArrayList<Debt> debtArrayList = new ArrayList<Debt>();
    private final boolean isDeletingView;
    private final boolean isEditingView;
    private Debt debt;

    public DebtListAdapter(Context context, DetailPersonFragment fragment, ArrayList<Debt> debtArrayList, boolean isDeletingView, boolean isEditingView) {
        this.fragment = fragment;
        this.context = context;
        this.debtArrayList.addAll(debtArrayList);
        this.isDeletingView = isDeletingView;
        this.isEditingView = isEditingView;
    }

    @Override
    public int getCount() {
        return debtArrayList.size();
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

        final LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.linear_container, null);
        }

        debt = debtArrayList.get(position);
        String reason = debt.getReason();
        Double amount = Double.parseDouble(debt.getAmount());

        TextView reasonView = (TextView) convertView.findViewById(R.id.nameView);
        ((TextView) convertView.findViewById(R.id.dateView)).setText(String.format(context.getString(R.string.date_format), debt.getDate()));
        reasonView.setText(reason);

        TextView amountView = ((TextView) convertView.findViewById(R.id.countView));
        ImageView profileImage = ((ImageView) convertView.findViewById(R.id.profileView));

        profileImage.setOnClickListener(this);

        String pathImage = debt.getProfileImageUrl();
        Bitmap image = Utils.getImageFromPath(pathImage);

        if (image != null) {
            profileImage.setImageBitmap(ViewCreator.getRoundedShape(image));
        }

        amountView.setText(String.format(context.getString(R.string.money_format), amount));
        amountView.setTextColor(amount >= 0 ? context.getResources().getColor(R.color.green) : context.getResources().getColor(R.color.red));

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

    @Override
    public void onClick(View v) {
        ListView listView = new ListView(context);
        String[] items = new String[]{context.getString(R.string.camera), context.getString(R.string.gallery), context.getString(R.string.fullscreen), context.getString(R.string.nothing)};
        SimpleListAdapter adapter = new SimpleListAdapter(context, items);
        listView.setAdapter(adapter);
        final AlertDialog dialog = ViewCreator.createListViewDialogBox(context, listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragment.setDebtExtra(debt);
                dialog.dismiss();
                switch (position) {
                    case 0:
                        String fileName = "temp.jpg";
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, fileName);
                        ((AbstractMoneyFragment) fragment).setCapturedImageURI(context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values));

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, ((AbstractMoneyFragment) fragment).getCapturedImageURI());
                        fragment.startActivityForResult(intent, Utils.TAKE_PICTURE_FOR_DEBT);
                        break;
                    case 1:
                        Intent galleryPicture = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        fragment.startActivityForResult(galleryPicture, Utils.IMPORT_DEBT_IMAGE_CODE);
                        break;
                    case 2:
                        String path = debt.getProfileImageUrl();
                        if (path != null && !path.isEmpty()) {
                            Intent fullScreenIntent = new Intent(context, FullScreenImageActivity.class);
                            fullScreenIntent.putExtra(Utils.PATH_KEY, path);
                            context.startActivity(fullScreenIntent);
                        } else {
                            Toast.makeText(context, context.getString(R.string.missing_picture), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 3:
                        Utils.dbManager.setImageProfileUrlDebt(debt.getId(), "");
                        fragment.notifyChanges();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }
}
