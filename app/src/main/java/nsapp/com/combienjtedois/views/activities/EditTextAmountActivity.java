package nsapp.com.combienjtedois.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import nsapp.com.combienjtedois.R;
import nsapp.com.combienjtedois.model.Debt;

public class EditTextAmountActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String RESULT_KEY = "result";
    private static final String OPERATION = "operation";
    private static final String TYPE = "type";
    private static final String DEBT_EXTRA = "debt";

    private EditText editText;
    private int operation;
    private Double amount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relative_edittext);

        editText = (EditText) findViewById(R.id.editView);

        int type = getIntent().getIntExtra(TYPE, 0);
        operation = getIntent().getIntExtra(OPERATION, -1);
        Debt debt = (Debt) getIntent().getSerializableExtra(DEBT_EXTRA);

        amount = Math.abs(Double.parseDouble(debt.getAmount()));

        if (type == 0) {
            ((TextView) findViewById(R.id.typeView)).setText(getString(R.string.credence) + " : " + String.format(getString(R.string.money_format), amount));
        } else {
            ((TextView) findViewById(R.id.typeView)).setText(getString(R.string.debt) + " : " + String.format(getString(R.string.money_format), amount));
        }

        if (operation == 0) {
            editText.setHint(editText.getHint() + " " + getString(R.string.add_hint));
        } else {
            editText.setHint(editText.getHint() + " " + getString(R.string.subtract_hint));
        }
        TextView textView = (TextView) findViewById(R.id.validView);

        textView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String amountOperation = editText.getText().toString();
        Double mathOp;
        String result;

        if (operation == 1) {
            mathOp = (amount - Double.parseDouble(amountOperation));
            if (mathOp <= 0) {
                Toast.makeText(this, getString(R.string.negative_amount), Toast.LENGTH_SHORT).show();
            } else {
                result = Double.toString(mathOp);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(RESULT_KEY, result);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        } else {
            mathOp = (amount + Double.parseDouble(amountOperation));
            result = Double.toString(mathOp);
            Intent returnIntent = new Intent();
            returnIntent.putExtra(RESULT_KEY, result);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }
}
