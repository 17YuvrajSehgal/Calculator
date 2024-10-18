package com.cosc3p97.calculator;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText resultView, newNumberView;
    private TextView operationView;
    private Double operand, operandInMemory;
    private String pendingOperation = "=";
    private Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    private Button buttonDecimal, buttonAdd, buttonSub, buttonMultiply, buttonDivide, buttonEquals, buttonSaved, buttonRetrieve, buttonLeftParen, buttonRightParen, buttonAllClear, buttonCorrect;
    private Switch toggleBasicModeBtn;
    private View.OnClickListener digitListener, memoryListener, operationListener, scientificOperationsListener, toggleModeSwitchListener, memoryRetrieveListener, allClearListener, correctListener;
    private EquationCalculator equationCalculator;

    private static final String STATE_PENDING_OPERATION = "PendingOperation";
    private static final String STATE_OPERAND1 = "Operand1";
    private static final String STATE_TOGGLE_BTN = "ToggleBasicModeBtn";
    private static final String STATE_MEMORY_OPERAND = "OperandInMemory";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        operandInMemory = (double) 0;
        initializeViews();
        initializeListeners();

    }

    /**
     * Initializes the view components of the calculator app by binding
     * the UI elements defined in the layout file (activity_main.xml)
     * to their corresponding Java objects. This method sets up
     * references for display elements, digit buttons, operator buttons,
     * memory buttons, and toggle switches.
     */
    public void initializeViews() {
        // Initialize display components
        this.resultView = findViewById(R.id.result);
        this.newNumberView = findViewById(R.id.newNumber);
        this.operationView = findViewById(R.id.operation);

        // Initialize digit buttons
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

        // Initialize operator buttons
        this.buttonDecimal = findViewById(R.id.buttonDecimal);
        this.buttonAdd = findViewById(R.id.buttonAdd);
        this.buttonSub = findViewById(R.id.buttonSub);
        this.buttonMultiply = findViewById(R.id.buttonMultiply);
        this.buttonDivide = findViewById(R.id.buttonDivide);
        this.buttonEquals = findViewById(R.id.buttonEquals);

        // Initialize parentheses buttons and set their visibility
        this.buttonLeftParen = findViewById(R.id.buttonLeftParenthesis);
        this.buttonRightParen = findViewById(R.id.buttonRightParenthesis);
        this.buttonLeftParen.setVisibility(View.GONE);
        this.buttonRightParen.setVisibility(View.GONE);

        // Initialize all-clear and correction buttons
        this.buttonAllClear = findViewById(R.id.buttonAllClear);
        this.buttonCorrect = findViewById(R.id.buttonCorrect);

        // Initialize basic mode toggle switch
        this.toggleBasicModeBtn = findViewById(R.id.buttonBS);

        // Initialize memory function buttons
        this.buttonSaved = findViewById(R.id.buttonStore);
        this.buttonRetrieve = findViewById(R.id.buttonRetrieve);
    }


    /**
     * Initializes the listeners for all the buttons and other interactive elements
     * in the calculator app. This method assigns appropriate OnClickListener instances
     * to each button, enabling them to respond to user actions.
     */
    public void initializeListeners() {
        this.digitListener = getDigitListener();
        this.operationListener = getOperationListener();
        this.scientificOperationsListener = getScientificOperationListener();
        this.toggleModeSwitchListener = getToggleSwitchListener();
        this.memoryListener = getMemoryStoreListener();
        this.memoryRetrieveListener = getMemoryRetrieveListener();
        this.allClearListener = getAllClearListener();
        this.correctListener = getCorrectListener();

        // Set digit listener for number buttons
        setDigitListener(this.digitListener);

        // Set operation listener for basic and scientific operations
        setOperationListener(this.operationListener);

        // Assign OnClickListener for clear and correct buttons
        buttonAllClear.setOnClickListener(this.allClearListener);
        buttonCorrect.setOnClickListener(this.correctListener);

        // Assign listeners for memory functions
        buttonSaved.setOnClickListener(this.memoryListener);
        buttonRetrieve.setOnClickListener(this.memoryRetrieveListener);

        // Assign listeners for scientific operation buttons
        buttonLeftParen.setOnClickListener(this.scientificOperationsListener);
        buttonRightParen.setOnClickListener(this.scientificOperationsListener);

        // Assign listener for mode toggle button
        toggleBasicModeBtn.setOnClickListener(this.toggleModeSwitchListener);
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(STATE_PENDING_OPERATION, pendingOperation);
        outState.putBoolean(STATE_TOGGLE_BTN, toggleBasicModeBtn.isChecked());
        outState.putDouble(STATE_MEMORY_OPERAND, operandInMemory);
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
        operandInMemory = savedInstanceState.getDouble(STATE_MEMORY_OPERAND);

        // Retrieve and apply the saved toggle button state
        boolean isToggleOn = savedInstanceState.getBoolean(STATE_TOGGLE_BTN, false);
        toggleBasicModeBtn.setChecked(isToggleOn);
        if (isToggleOn) {
            setViewForScientific();
        } else {
            setViewForBasic();
        }
    }


    /**
     * Performs the specified arithmetic operation on the current operand and the provided value.
     *
     * @param value     The new value to be used in the operation.
     * @param operation The operation to perform, such as "+", "-", "*", "/", or "=".
     *                  If "=" is provided, the operand will be replaced by the new value.
     *                  If the operation or value is invalid, the method will show a Toast message.
     */
    private void performOperation(Double value, String operation) {
        // Check if the operation or value is null, and show a Toast if invalid.
        if (operation == null || value == null) {
            Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
            return; // Exit early if inputs are invalid.
        }

        // If no operand exists, initialize it with the provided value.
        if (operand == null) {
            operand = value;
        } else {
            // If the previous operation was "=", use the new operation for further calculations.
            if (pendingOperation.equals("=")) {
                pendingOperation = operation;
            }

            // Perform the operation based on the pending operation.
            switch (pendingOperation) {
                case "=":
                    operand = value; // Simply set the operand to the new value.
                    break;

                case "/":
                    // Handle division by zero with an error message.
                    if (value == 0) {
                        resultView.setText("Error: Divide by 0");
                        return; // Exit early to avoid further processing.
                    }
                    operand /= value; // Perform division.
                    break;

                case "*":
                    operand *= value; // Perform multiplication.
                    break;

                case "+":
                    operand += value; // Perform addition.
                    break;

                case "-":
                    operand -= value; // Perform subtraction.
                    break;

                default:
                    // Handle any unexpected operations gracefully.
                    Toast.makeText(MainActivity.this, "Invalid operation", Toast.LENGTH_SHORT).show();
                    return; // Exit early for invalid operations.
            }
        }

        // Update the result view with the new operand value.
        resultView.setText(operand.toString());
        // Clear the new number input field to prepare for the next input.
        newNumberView.setText("");
    }



    private View.OnClickListener getAllClearListener() {
        return view -> {
            operand = 0.0;
            pendingOperation = "=";
            operandInMemory = 0.0;
            newNumberView.setText("");
            resultView.setText("");
        };
    }

    private View.OnClickListener getCorrectListener() {
        return view -> {
            String currentText = newNumberView.getText().toString();

            // Check if the text is not empty before deleting the last character
            if (!currentText.isEmpty()) {
                // Remove the last character
                String updatedText = currentText.substring(0, currentText.length() - 1);
                newNumberView.setText(updatedText);
            }
        };
    }


    private View.OnClickListener getToggleSwitchListener() {
        return view -> {
            // Ensure you are correctly referencing the Switch
            Switch basicScientificSwitch = (Switch) view;

            if (basicScientificSwitch.isChecked()) {    //if the current mode is scientific
                setViewForScientific();
                //reset the input, output and operations box
                resetViewsAndOperands();

            } else {    //if current mode is set to basic again:
                setViewForBasic();
                //reset the input, output and operations box
                resetViewsAndOperands();
            }
        };
    }

    public void setViewForScientific() {
        // If the switch is ON, hide the TextView
        operationView.setVisibility(View.GONE);
        buttonLeftParen.setVisibility(View.VISIBLE);
        buttonRightParen.setVisibility(View.VISIBLE);
        // Change newNumberView input type to text
        newNumberView.setInputType(InputType.TYPE_CLASS_TEXT);

        //set the operations listener back to scientific mode
        setOperationListener(scientificOperationsListener);
    }

    private void setViewForBasic() {
        // If the switch is OFF, bring back the TextView  for operations
        operationView.setVisibility(View.VISIBLE);
        buttonLeftParen.setVisibility(View.GONE);
        buttonRightParen.setVisibility(View.GONE);
        // Change newNumberView input type back to signed number
        newNumberView.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);

        //set the operations listener back to normal mode
        setOperationListener(operationListener);
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
                    equationCalculator = new EquationCalculator(currentText);
                    double result = Double.parseDouble(equationCalculator.equals());
                    resultView.setText(String.valueOf(result));
                } catch (Exception e) {
                    resultView.setText("Error");
                }
                newNumberView.setText("");
                return;
            }

            // Check if the button is a parenthesis
            if (buttonText.equals("(") || buttonText.equals(")")) {
                newNumberView.append(buttonText);
                return;
            }

            // Handle replacing the last operation if necessary
            if (isLastCharacterOperation(currentText)) {
                currentText = currentText.substring(0, currentText.length() - 1);
                newNumberView.setText(currentText);
            }

            // Append the new operation or digit
            newNumberView.append(buttonText);
        };
    }

    // Helper method to determine if the last character is an operator
    private boolean isLastCharacterOperation(String text) {
        if (text.isEmpty()) return false;
        char lastChar = text.charAt(text.length() - 1);
        return "+-*/".indexOf(lastChar) != -1;  // Valid operators
    }


    private View.OnClickListener getMemoryStoreListener() {
        return view -> {
            if (operand != null) {
                operandInMemory = operand;
                Toast.makeText(MainActivity.this, "Stored in memory: " + operandInMemory, Toast.LENGTH_SHORT).show();
            }
        };
    }


    private View.OnClickListener getMemoryRetrieveListener() {
        return view -> {
            if (operand != null) {
                operand = operandInMemory;
                newNumberView.setText(operand.toString());
                Toast.makeText(MainActivity.this, "Retrieved from memory: " + operandInMemory, Toast.LENGTH_SHORT).show();
            }
        };
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
