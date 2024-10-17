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

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private EditText resultView, newNumberView;
    private TextView operationView;
    private Double operand;
    private String pendingOperation = "=";
    private Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    private View buttonDecimal, buttonAdd, buttonSub, buttonMultiply, buttonDivide, buttonEquals, buttonSaved, buttonRetrieve;
    private Switch toggleBasicModeBtn;
    private View.OnClickListener digitListener, memoryListener, operationListener, scientificOperationsListener, toggleModeSwitchListener, memoryRetrieveListener;
    private Double operandInMemory;

    private static final String STATE_PENDING_OPERATION = "PendingOperation";
    private static final String STATE_OPERAND1 = "Operand1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        operandInMemory = (double) 0;

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
        //memory buttons
        this.buttonSaved = findViewById(R.id.buttonStore);
        this.buttonRetrieve = findViewById(R.id.buttonRetrieve);

        this.digitListener = getDigitListener();
        this.operationListener = getOperationListener();
        this.scientificOperationsListener = getScientificOperationListener();
        this.toggleModeSwitchListener = getToggleSwitchListener();
        this.memoryListener = getMemoryStoreListener();
        this.memoryRetrieveListener = getMemoryRetrieveListener();

        setDigitListener(this.digitListener);
        setOperationListener(this.operationListener);
        buttonSaved.setOnClickListener(this.memoryListener);
        buttonRetrieve.setOnClickListener(this.memoryRetrieveListener);

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
                    operand = (value == 0) ? Double.POSITIVE_INFINITY : operand / value;
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

            if (basicScientificSwitch.isChecked()) {    //if the current mode is scientific
                // If the switch is ON, hide the TextView
                operationView.setVisibility(View.GONE);
                // Change newNumberView input type to text
                newNumberView.setInputType(InputType.TYPE_CLASS_TEXT);
                //reset the input, output and operations box
                resetViewsAndOperands();
                //set the operations listener back to scientific mode
                setOperationListener(scientificOperationsListener);

            } else {    //if current mode is set to basic again:
                // If the switch is OFF, bring back the TextView  for operations
                operationView.setVisibility(View.VISIBLE);
                // Change newNumberView input type back to signed number
                newNumberView.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
                //reset the input, output and operations box
                resetViewsAndOperands();
                //set the operations listener back to normal mode
                setOperationListener(operationListener);
            }
        };
    }

    private void resetViewsAndOperands() {
        operationView.setText("=");
        resultView.setText("");
        newNumberView.setText("");
        this.operand = (double) 0;
    }

    private View.OnClickListener getScientificOperationListener() {
        return view -> {
            Button button = (Button) view;
            String buttonText = button.getText().toString();
            String currentText = newNumberView.getText().toString();

            if (buttonText.equals("=")) {
                try {
                    double result = calculateExpression(currentText);
                    resultView.setText(String.valueOf(result));
                } catch (Exception e) {
                    resultView.setText("Error");
                }
                newNumberView.setText("");
            }
            // Check if the last character is already an operation
            if (isLastCharacterOperation(currentText)) {
                // Replace the last operation with the new one
                currentText = currentText.substring(0, currentText.length() - 1);
                newNumberView.setText(currentText);
            }

            // Append the new operation
            newNumberView.append(buttonText);
        };
    }

    // Helper method to determine if the last character is an operator
    private boolean isLastCharacterOperation(String text) {
        if (text.isEmpty()) return false;

        char lastChar = text.charAt(text.length() - 1);
        return "+-*/".indexOf(lastChar) != -1;
    }


    // New method to evaluate the final expression with PEMDAS
    private double calculateExpression(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);

            // If it's a digit or decimal point, extract the complete number
            if (Character.isDigit(c) || c == '.') {
                StringBuilder number = new StringBuilder();
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    number.append(expression.charAt(i++));
                }
                numbers.push(Double.parseDouble(number.toString()));
                continue;  // Avoid incrementing `i` again
            }

            // If it's an operator, process precedence
            if ("+-*/".indexOf(c) != -1) {
                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }
            i++;
        }

        // Complete remaining operations in the stacks
        while (!operators.isEmpty()) {
            numbers.push(applyOperator(operators.pop(), numbers.pop(), numbers.pop()));
        }

        // Final result
        return numbers.pop();
    }

    // Helper method to determine operator precedence
    private boolean hasPrecedence(char op1, char op2) {
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true;
    }

    // Helper method to apply an operator to two operands
    private double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                return (b == 0) ? Double.POSITIVE_INFINITY : a / b;
        }
        return 0;
    }

    private View.OnClickListener getMemoryStoreListener() {
        View.OnClickListener memoryStoreListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("old memory: " + operandInMemory);
                if (operand != null)
                    operandInMemory = operand;
                System.out.println("new memory: " + operandInMemory);
            }
        };
        return memoryStoreListener;
    }

    private View.OnClickListener getMemoryRetrieveListener() {
        View.OnClickListener memoryRetrieveListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (operand != null) {
                    System.out.println("retrieved memory: " + operandInMemory);
                    operand = operandInMemory;
                    newNumberView.setText(operand.toString());
                }
            }
        };
        return memoryRetrieveListener;
    }

    private void setMemoryListener(View.OnClickListener memoryListener) {
        buttonSaved.setOnClickListener(memoryListener);
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
