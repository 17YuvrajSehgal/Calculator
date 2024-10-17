package com.cosc3p97.calculator;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText resultView, newNumberView;
    private TextView operationView;
    private Double operand;
    private String pendingOperation = "=";
    private Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    private View buttonDecimal, buttonAdd, buttonSub, buttonMultiply, buttonDivide, buttonEquals;
    private Switch toggleBasicModeBtn;
    private View.OnClickListener digitListener;
    private View.OnClickListener operationListener;
    private View.OnClickListener scientificOperationsListener;
    private View.OnClickListener toggleModeSwitchListener;

    private static final String STATE_PENDING_OPERATION = "PendingOperation";
    private static final String STATE_OPERAND1 = "Operand1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        this.resultView = findViewById(R.id.result);
        this.newNumberView = findViewById(R.id.newNumber);
        this.operationView = findViewById(R.id.operation);
        //digits buttons
        this.button0 = findViewById(R.id.button0);
        this.button1 = findViewById(R.id.button1);
        this.button2 = findViewById(R.id.button2);
        this.button3 = findViewById(R.id.button3);
        this.button4 = findViewById(R.id.button4);
        this.button5 = findViewById(R.id.button5);
        this.button6 = findViewById(R.id.button6);
        this.button7 = findViewById(R.id.button7);
        this.button8 = findViewById(R.id.button8);
        this.button9 = findViewById(R.id.button9);
        //operators button
        this.buttonDecimal = findViewById(R.id.buttonDecimal);
        this.buttonAdd = findViewById(R.id.buttonAdd);
        this.buttonSub = findViewById(R.id.buttonSub);
        this.buttonMultiply = findViewById(R.id.buttonMultiply);
        this.buttonDivide = findViewById(R.id.buttonDivide);
        this.buttonEquals = findViewById(R.id.buttonEquals);
        //basic mode toggle switch
        this.toggleBasicModeBtn = findViewById(R.id.buttonBS);

        this.digitListener = getDigitListener();
        this.operationListener = getOperationListener();
        this.scientificOperationsListener = getScientificOperationListener();
        this.toggleModeSwitchListener = getToggleSwitchListener();

        setDigitListener(this.digitListener);
        setOperationListener(this.operationListener);

        toggleBasicModeBtn.setOnClickListener(this.toggleModeSwitchListener);


    }




    private void performOperation(Double value, String operation) {
        if (operand == null) {
            operand = value;
        } else {
            if (pendingOperation.equals("=")) {
                pendingOperation = operation;
            }

            switch (pendingOperation) {
                case "=":
                    operand = value;
                    break;
                case "/":
                    if (value == 0) {
                        operand = 0.0;
                    } else {
                        operand /= value;
                    }
                    break;
                case "*":
                    operand *= value;
                    break;
                case "+":
                    operand += value;
                    break;
                case "-":
                    operand -= value;
                    break;
            }
        }
        resultView.setText(operand.toString());
        newNumberView.setText("");
    }

    private View.OnClickListener getToggleSwitchListener() {
        return view -> {
            // Ensure you are correctly referencing the Switch
            Switch basicScientificSwitch = (Switch) view;

            if (basicScientificSwitch.isChecked()) {
                // If the switch is ON, hide the TextView
                operationView.setVisibility(View.GONE);
                // Change newNumberView input type to text
                newNumberView.setInputType(InputType.TYPE_CLASS_TEXT);
                buttonAdd.setOnClickListener(scientificOperationsListener);
                buttonSub.setOnClickListener(scientificOperationsListener);
                buttonMultiply.setOnClickListener(scientificOperationsListener);
                buttonDivide.setOnClickListener(scientificOperationsListener);
                buttonEquals.setOnClickListener(scientificOperationsListener);

            } else {
                // If the switch is OFF, show the TextView
                operationView.setVisibility(View.VISIBLE);
                newNumberView.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
                buttonAdd.setOnClickListener(operationListener);
                buttonSub.setOnClickListener(operationListener);
                buttonMultiply.setOnClickListener(operationListener);
                buttonDivide.setOnClickListener(operationListener);
                buttonEquals.setOnClickListener(operationListener);
            }
        };
    }

    private View.OnClickListener getScientificOperationListener() {
        return view -> {
            Button button = (Button) view;
            newNumberView.append(button.getText().toString());
        };
    }

    private void setOperationListener(View.OnClickListener operationListener) {
        buttonAdd.setOnClickListener(operationListener);
        buttonSub.setOnClickListener(operationListener);
        buttonMultiply.setOnClickListener(operationListener);
        buttonDivide.setOnClickListener(operationListener);
        buttonEquals.setOnClickListener(operationListener);
    }

    private View.OnClickListener getOperationListener() {
        return view -> {
            Button button = (Button) view;
            String operation = button.getText().toString();
            String value = newNumberView.getText().toString();

            try {
                Double doubleValue = Double.valueOf(value);
                performOperation(doubleValue, operation);
            } catch (NumberFormatException e) {
                newNumberView.setText("");
            }

            pendingOperation = operation;
            operationView.setText(pendingOperation);
        };
    }

    private void setDigitListener(View.OnClickListener digitListener) {
        button0.setOnClickListener(digitListener);
        button1.setOnClickListener(digitListener);
        button2.setOnClickListener(digitListener);
        button3.setOnClickListener(digitListener);
        button4.setOnClickListener(digitListener);
        button5.setOnClickListener(digitListener);
        button6.setOnClickListener(digitListener);
        button7.setOnClickListener(digitListener);
        button8.setOnClickListener(digitListener);
        button9.setOnClickListener(digitListener);
        buttonDecimal.setOnClickListener(digitListener);
    }

    private View.OnClickListener getDigitListener() {
        return view -> {
            Button button = (Button) view;
            newNumberView.append(button.getText().toString());
        };
    }


}

/*
@Override
public void onSaveInstanceState(@NonNull Bundle outState) {
    outState.putString(STATE_PENDING_OPERATION, pendingOperation);
    if (operand != null) {
        outState.putDouble(STATE_OPERAND1, operand);
    }
    super.onSaveInstanceState(outState);
}

@Override
protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    pendingOperation = savedInstanceState.getString(STATE_PENDING_OPERATION);
    operand = savedInstanceState.getDouble(STATE_OPERAND1);
    operationView.setText(pendingOperation);
}
*/
