package nsapp.com.combienjtedois.views.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.views.activities.LaunchActivity;

public class DonationsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String PAYPAL_USER = "nsagnett@gmail.com";
    private static final String PAYPAL_CURRENCY_CODE = "EUR";

    private String mPaypalUser = "";
    private String mPaypalCurrencyCode = "";
    private String mPaypalItemName = "";

    public static DonationsFragment newInstance(int sectionNumber) {

        DonationsFragment donationsFragment = new DonationsFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);

        donationsFragment.setArguments(args);
        return donationsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.donation_fragment, container, false);

        mPaypalUser = PAYPAL_USER;
        mPaypalCurrencyCode = PAYPAL_CURRENCY_CODE;
        mPaypalItemName = getString(R.string.donation_paypal);

        LinearLayout donationPaypal = (LinearLayout) view.findViewById(R.id.paypal_donate_view);
        donationPaypal.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                donatePayPalOnClick();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LaunchActivity launchActivity = (LaunchActivity) getActivity();
        launchActivity.updateActionBarTitle(getString(R.string.title_section4));
        launchActivity.supportInvalidateOptionsMenu();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((LaunchActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    void donatePayPalOnClick() {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https").authority("www.paypal.com").path("cgi-bin/webscr");
        uriBuilder.appendQueryParameter("cmd", "_donations");

        uriBuilder.appendQueryParameter("business", mPaypalUser);
        uriBuilder.appendQueryParameter("lc", "US");
        uriBuilder.appendQueryParameter("item_name", mPaypalItemName);
        uriBuilder.appendQueryParameter("no_note", "1");
        uriBuilder.appendQueryParameter("no_shipping", "1");
        uriBuilder.appendQueryParameter("currency_code", mPaypalCurrencyCode);
        Uri payPalUri = uriBuilder.build();

        // Start your favorite browser
        try {
            Intent viewIntent = new Intent(Intent.ACTION_VIEW, payPalUri);
            startActivity(viewIntent);
        } catch (ActivityNotFoundException e) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setIcon(android.R.drawable.ic_dialog_alert);
            dialog.setTitle(R.string.donation_alert_dialog_title);
            dialog.setMessage(getString(R.string.donation_alert_dialog_no_browser));
            dialog.setCancelable(true);
            dialog.setNeutralButton(R.string.donations_button_close,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
            );
            dialog.show();
        }
    }
}
