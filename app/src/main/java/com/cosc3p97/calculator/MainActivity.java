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

/**
 * MainActivity is the entry point of the calculator application.
 * It handles user interactions, including button clicks for digits and
 * operations. The activity is responsible for displaying the current
 * input and results on the screen. It utilizes the EquationCalculator
 * class to evaluate mathematical expressions and update the user interface
 * accordingly.
 */
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
    private static final String STATE_PENDING_OPERAND = "PendingOperand";
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


    /**
     * Saves the current state of the activity to the provided Bundle.
     *
     * @param outState The Bundle in which to save the activity state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(STATE_PENDING_OPERATION, pendingOperation);
        outState.putString(STATE_PENDING_OPERAND, String.valueOf(newNumberView.getText()));
        outState.putBoolean(STATE_TOGGLE_BTN, toggleBasicModeBtn.isChecked());
        outState.putDouble(STATE_MEMORY_OPERAND, operandInMemory);
        if (operand != null) {
            outState.putDouble(STATE_OPERAND1, operand);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Restores the state of the activity from the provided Bundle.
     *
     * @param savedInstanceState The Bundle containing the saved state of the activity.
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pendingOperation = savedInstanceState.getString(STATE_PENDING_OPERATION);
        operand = savedInstanceState.getDouble(STATE_OPERAND1);
        operationView.setText(pendingOperation);
        operandInMemory = savedInstanceState.getDouble(STATE_MEMORY_OPERAND);
        String pendingOperand = savedInstanceState.getString(STATE_PENDING_OPERAND);
        // Retrieve and apply the saved toggle button state
        boolean isToggleOn = savedInstanceState.getBoolean(STATE_TOGGLE_BTN, false);
        toggleBasicModeBtn.setChecked(isToggleOn);
        if (isToggleOn) {
            setViewForScientific();
        } else {
            setViewForBasic();
        }
        newNumberView.setText(pendingOperand);
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


    /**
     * Creates an OnClickListener that resets all calculator values to their initial state.
     * This listener is intended to be used with an "All Clear" (AC) button.
     *
     * @return A View.OnClickListener that clears the operand, pending operation, memory, and UI elements.
     */
    private View.OnClickListener getAllClearListener() {
        return view -> {
            // Reset operand and memory values to their initial states.
            operand = 0.0;
            operandInMemory = 0.0;

            // Set the pending operation to "=" to indicate no ongoing operation.
            pendingOperation = "=";

            // Clear the input fields on the UI.
            newNumberView.setText(""); // Clear the input number field.
            resultView.setText("");    // Clear the result display.
            operationView.setText("=");//reset the operation view to =

            // Optional: Display a Toast confirming the reset (for better UX).
            Toast.makeText(view.getContext(), "All cleared", Toast.LENGTH_SHORT).show();
        };
    }


    /**
     * Creates an OnClickListener that removes the last character from the current input number.
     * This listener is intended to be used with a "Correct" or "Backspace" button.
     *
     * @return A View.OnClickListener that updates the input number by removing the last character.
     */
    private View.OnClickListener getCorrectListener() {
        return view -> {
            // Get the current text displayed in the new number input field.
            String currentText = newNumberView.getText().toString();

            // Check if the text is not empty before attempting to delete the last character.
            if (!currentText.isEmpty()) {
                // Remove the last character from the current text.
                String updatedText = currentText.substring(0, currentText.length() - 1);
                newNumberView.setText(updatedText); // Update the input field with the new text.
            }
            // Optional: Display a Toast confirming the character removal (for better UX).
            else {
                Toast.makeText(view.getContext(), "Nothing to correct", Toast.LENGTH_SHORT).show();
            }
        };
    }


    /**
     * Creates an OnClickListener that toggles between basic and scientific calculator modes.
     * This listener is intended to be used with a toggle switch.
     *
     * @return A View.OnClickListener that updates the calculator view based on the selected mode.
     */
    private View.OnClickListener getToggleSwitchListener() {
        return view -> {
            // Cast the view to a Switch to access its properties and methods.
            Switch basicScientificSwitch = (Switch) view;

            // Check if the switch is in the "scientific" mode (checked).
            if (basicScientificSwitch.isChecked()) {
                setViewForScientific(); // Switch to scientific mode view.
                // Reset the input, output, and operations box for a clean start.
                resetViewsAndOperands();
            } else { // If the switch is in "basic" mode (unchecked):
                setViewForBasic(); // Switch to basic mode view.
                // Reset the input, output, and operations box for a clean start.
                resetViewsAndOperands();
            }
            // Optional: Provide feedback to the user about the mode change (for better UX).
            Toast.makeText(view.getContext(),
                    basicScientificSwitch.isChecked() ? "Switched to Scientific Mode" : "Switched to Basic Mode",
                    Toast.LENGTH_SHORT).show();
        };
    }


    /**
     * Configures the calculator view for scientific mode.
     * This method updates the visibility of various UI elements
     * and adjusts the input type to allow for scientific calculations.
     */
    public void setViewForScientific() {
        // Hide the operation view when in scientific mode
        operationView.setVisibility(View.GONE);

        // Show the parentheses buttons, as they are relevant in scientific calculations
        buttonLeftParen.setVisibility(View.VISIBLE);
        buttonRightParen.setVisibility(View.VISIBLE);

        // Change the newNumberView input type to text to allow for scientific notation
        newNumberView.setInputType(InputType.TYPE_CLASS_TEXT);

        // Set the operations listener to handle scientific operations
        setOperationListener(scientificOperationsListener);

        // Optional: Clear the input display to avoid confusion when switching modes
        newNumberView.setText("");
    }


    /**
     * Configures the calculator view for basic mode.
     * This method updates the visibility of various UI elements
     * and adjusts the input type to allow for basic calculations.
     */
    private void setViewForBasic() {
        // Show the operation view when switching to basic mode
        operationView.setVisibility(View.VISIBLE);

        // Hide the parentheses buttons, as they are not needed in basic calculations
        buttonLeftParen.setVisibility(View.GONE);
        buttonRightParen.setVisibility(View.GONE);

        // Change the newNumberView input type back to allow signed numbers only
        newNumberView.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);

        // Set the operations listener to handle normal (basic) operations
        setOperationListener(operationListener);

        // Optional: Clear the input display to avoid confusion when switching modes
        newNumberView.setText("");
    }


    /**
     * Resets the calculator's display and operands to their initial state.
     * This method clears the operation view, result view, and input field,
     * and resets the operand to zero.
     */
    private void resetViewsAndOperands() {
        // Reset the operation display to the default state
        operationView.setText("=");

        // Clear the result and input fields
        resultView.setText("");
        newNumberView.setText("");

        // Reset the operand to zero
        this.operand = 0.0;  // Use 0.0 for clarity as a double type
    }


    /**
     * Returns an OnClickListener for scientific operation buttons.
     * Handles operations based on the button clicked, including
     * evaluating equations, appending parentheses, and managing
     * the input text.
     *
     * @return An OnClickListener that processes scientific operations.
     */
    private View.OnClickListener getScientificOperationListener() {
        return view -> {
            Button button = (Button) view;  // Cast the view to a Button
            String buttonText = button.getText().toString();  // Get the button text
            String currentText = newNumberView.getText().toString();  // Get current input text

            // Handle evaluation when the "=" button is pressed
            if (buttonText.equals("=")) {
                try {
                    equationCalculator = new EquationCalculator(currentText);  // Create an equation calculator instance
                    double result = Double.parseDouble(equationCalculator.equals());  // Evaluate the equation
                    resultView.setText(String.valueOf(result));  // Display the result
                } catch (Exception e) {
                    resultView.setText("Error");  // Display error message on exception
                }
                newNumberView.setText("");  // Clear input after evaluation
                return;  // Exit the listener
            }

            // Check if the button is a parenthesis
            if (buttonText.equals("(") || buttonText.equals(")")) {
                newNumberView.append(buttonText);  // Append parenthesis to input
                return;  // Exit the listener
            }

            // Handle replacing the last operation if necessary
            if (isLastCharacterOperation(currentText)) {
                currentText = currentText.substring(0, currentText.length() - 1);  // Remove the last character if it's an operation
                newNumberView.setText(currentText);  // Update input text
            }

            // Append the new operation or digit
            newNumberView.append(buttonText);  // Add the new button text to the input
        };
    }


    /**
     * Helper method to determine if the last character of the given text is an operator.
     *
     * @param text The input string to check.
     * @return true if the last character is an operator (+, -, *, /); false otherwise.
     */
    private boolean isLastCharacterOperation(String text) {
        if (text.isEmpty()) return false;  // Return false if the text is empty
        char lastChar = text.charAt(text.length() - 1);  // Get the last character of the text
        return "+-*/".indexOf(lastChar) != -1;  // Check if the last character is a valid operator
    }


    /**
     * OnClickListener for storing the current operand value in memory.
     *
     * This listener checks if the current operand is not null,
     * and if valid, stores it in the operandInMemory variable
     * and shows a toast message indicating the value stored.
     *
     * @return A View.OnClickListener that handles memory storage functionality.
     */
    private View.OnClickListener getMemoryStoreListener() {
        return view -> {
            // Check if the operand is not null before storing it in memory
            if (operand != null) {
                operandInMemory = operand;  // Store the current operand value in memory
                Toast.makeText(MainActivity.this, "Stored in memory: " + operandInMemory, Toast.LENGTH_SHORT).show();
            } else {
                // Optionally handle the case where operand is null
                Toast.makeText(MainActivity.this, "No value to store in memory.", Toast.LENGTH_SHORT).show();
            }
        };
    }


    /**
     * OnClickListener for retrieving the stored value from memory.
     *
     * This listener checks if the operandInMemory has a value,
     * and if valid, sets it as the current input in newNumberView
     * and shows a toast message indicating the retrieved value.
     *
     * @return A View.OnClickListener that handles memory retrieval functionality.
     */
    private View.OnClickListener getMemoryRetrieveListener() {
        return view -> {
            // Check if operandInMemory has a value to retrieve
            if (operandInMemory != null) {
                // Set the retrieved value as the current input
                newNumberView.setText(operandInMemory.toString());
                Toast.makeText(MainActivity.this, "Retrieved from memory: " + operandInMemory, Toast.LENGTH_SHORT).show();
            } else {
                // Optionally handle the case where there is nothing stored in memory
                Toast.makeText(MainActivity.this, "No value in memory to retrieve.", Toast.LENGTH_SHORT).show();
            }
        };
    }


    /**
     * Sets the provided operation listener for all operation buttons.
     *
     * This method assigns the given OnClickListener to the buttons
     * for addition, subtraction, multiplication, division, and equals
     * operations, allowing them to share the same click behavior.
     *
     * @param operationListener The OnClickListener to be set for operation buttons.
     */
    private void setOperationListener(View.OnClickListener operationListener) {
        // Assign the operation listener to each operation button
        buttonAdd.setOnClickListener(operationListener);
        buttonSub.setOnClickListener(operationListener);
        buttonMultiply.setOnClickListener(operationListener);
        buttonDivide.setOnClickListener(operationListener);
        buttonEquals.setOnClickListener(operationListener);
    }


    /**
     * Returns an OnClickListener for handling operations (e.g., +, -, *, /).
     * This listener retrieves the operation from the clicked button,
     * fetches the current input value, and performs the specified
     * operation on it. If the input value is invalid, it clears the input.
     *
     * @return An OnClickListener for operation buttons.
     */
    private View.OnClickListener getOperationListener() {
        return view -> {
            // Get the button that was clicked
            Button button = (Button) view;
            String operation = button.getText().toString();  // Get the operation text
            String value = newNumberView.getText().toString();  // Get the current input value

            try {
                // Attempt to convert the input value to a Double
                Double doubleValue = Double.valueOf(value);
                // Perform the operation with the parsed double value
                performOperation(doubleValue, operation);
            } catch (NumberFormatException e) {
                // Clear the input field if the value is not a valid number
                newNumberView.setText("");
            }

            // Update the pending operation and the operation view
            pendingOperation = operation;
            operationView.setText(pendingOperation);
        };
    }

    /**
     * Sets the provided OnClickListener for all digit buttons (0-9)
     * and the decimal button.
     *
     * This method binds the digitListener to each button so that
     * they will execute the same action when clicked, allowing for
     * easy handling of digit inputs.
     *
     * @param digitListener The OnClickListener to be set for digit buttons.
     */
    private void setDigitListener(View.OnClickListener digitListener) {
        // Set the digit listener for each digit button
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


    /**
     * Returns an OnClickListener for digit buttons that appends the
     * digit represented by the button to the newNumberView when clicked.
     *
     * This listener can be set on any button representing a digit (0-9)
     * or a decimal point, allowing for seamless user input of numbers.
     *
     * @return An OnClickListener that appends the digit to newNumberView.
     */
    private View.OnClickListener getDigitListener() {
        return view -> {
            // Cast the view to a Button to get the digit's text
            Button button = (Button) view;

            // Append the text of the button (the digit) to newNumberView
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
