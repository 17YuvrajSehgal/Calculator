package com.cosc3p97.calculator;

import java.math.BigDecimal;

/**
 * Reference: https://github.com/Jhunrel25/PEMDAS-Calculator-Program/blob/main/Calculator.java
 * This class is responsible for evaluating mathematical expressions
 * provided in the form of strings. It supports various operations
 * including addition, subtraction, multiplication, division,
 * exponents, and parentheses handling.
 */
public class EquationCalculator {

    private String[] tokens; // Array of tokens representing the expression
    private int size;        // Number of tokens in the expression

    /**
     * Constructor that initializes the EquationCalculator with
     * a given mathematical expression.
     *
     * @param expression The mathematical expression to be evaluated.
     */
    public EquationCalculator(String expression) {
        this.tokens = this.addSpaces(expression).split("\\s");
        this.size = this.tokens.length;
    }

    /**
     * Adds spaces around operators and parentheses in the expression
     * for easier tokenization.
     *
     * @param expression The original mathematical expression.
     * @return The modified expression with spaces.
     */
    private String addSpaces(String expression) {
        return
                expression
                        .replaceAll("(?<=[\\d])(?=[\\^*/+-])", " ")
                        .replaceAll("(?<=[\\^*/+-])(?=[\\d])", " ")
                        .replaceAll("(?<=[)])(?=[\\^*/+-])", " ")
                        .replaceAll("(?<=[\\^*/+-])(?=[(])", " ")
                        .replaceAll("(?<=[(])(?=[\\d])", " ")
                        .replaceAll("(?<=[\\d])(?=[)])", " ")
                        .replaceAll("(?<=[(])(?=[(])", " ")
                        .replaceAll("(?<=[)])(?=[)])", " ");
    }

    /**
     * Removes a token at the specified index and shifts the remaining
     * tokens to the left.
     *
     * @param index The index of the token to remove.
     */
    private void removeAToken(int index) {
        while (index < (this.size - 1))
            this.tokens[index] = this.tokens[1 + (index++)];

        this.size--; // Decrease the size of tokens
    }

    /**
     * Removes the last two tokens that have been used during the evaluation.
     *
     * @param index The index of the token to start removal.
     */
    private void removeAlreadyUsedTokens(int index) {
        for (byte i = 0; (i < 2); ++i)
            this.removeAToken(index);
    }

    /**
     * Replaces the token at the specified index with the given value.
     *
     * @param index The index of the token to replace.
     * @param value The value to replace the token with.
     */
    private void addComputationValueOfTokens(int index, String value) {
        this.tokens[index] = value;
    }

    /**
     * Handles the evaluation of expressions inside parentheses recursively.
     *
     * @param index The index of the opening parenthesis.
     * @return The evaluated result of the expression inside the parentheses.
     */
    private String parenthesis(int index) {
        StringBuilder parenthesisValue = new StringBuilder();

        // Loop until the closing parenthesis is found
        while (!(this.tokens[index + 1].equals(")"))) {
            if (this.tokens[index + 1].equals("(")) {
                // Recursively evaluate nested parentheses
                parenthesisValue
                        .append(this.parenthesis(index + 1))
                        .append(" ");

                this.removeAToken(index + 1);
            } else {
                parenthesisValue
                        .append(this.tokens[index + 1])
                        .append(" ");

                this.removeAToken(index + 1);
            }
        }
        this.removeAToken(index + 1); // Remove the closing parenthesis

        // Replace the evaluated parentheses expression in tokens
        this.addComputationValueOfTokens(
                (index),
                new EquationCalculator(parenthesisValue.toString())
                        .equals()
        );

        return this.tokens[index]; // Return the evaluated value
    }

    // Methods for each mathematical operation: exponents, multiplication,
    // division, addition, and subtraction

    /**
     * Evaluates an exponentiation operation at the specified index.
     *
     * @param index The index of the exponentiation operator.
     */
    private void exponents(int index) {
        String value = new BigDecimal(this.tokens[index - 1])
                .pow(Integer.parseInt(this.tokens[index + 1]))
                .toString();

        this.addComputationValueOfTokens((index - 1), (value));
        this.removeAlreadyUsedTokens(index);
    }

    /**
     * Evaluates a multiplication operation at the specified index.
     *
     * @param index The index of the multiplication operator.
     */
    private void multiplication(int index) {
        String value = new BigDecimal(this.tokens[index - 1])
                .multiply(new BigDecimal(this.tokens[index + 1]))
                .toString();

        this.addComputationValueOfTokens((index - 1), (value));
        this.removeAlreadyUsedTokens(index);
    }

    /**
     * Evaluates a division operation at the specified index.
     *
     * @param index The index of the division operator.
     */
    private void division(int index) {
        String value = new BigDecimal(this.tokens[index - 1])
                .divide(new BigDecimal(this.tokens[index + 1]))
                .toString();

        this.addComputationValueOfTokens((index - 1), (value));
        this.removeAlreadyUsedTokens(index);
    }

    /**
     * Evaluates an addition operation at the specified index.
     *
     * @param index The index of the addition operator.
     */
    private void addition(int index) {
        String value = new BigDecimal(this.tokens[index - 1])
                .add(new BigDecimal(this.tokens[index + 1]))
                .toString();

        this.addComputationValueOfTokens((index - 1), (value));
        this.removeAlreadyUsedTokens(index);
    }

    /**
     * Evaluates a subtraction operation at the specified index.
     *
     * @param index The index of the subtraction operator.
     */
    private void subtraction(int index) {
        String value = new BigDecimal(this.tokens[index - 1])
                .subtract(new BigDecimal(this.tokens[index + 1]))
                .toString();

        this.addComputationValueOfTokens((index - 1), (value));
        this.removeAlreadyUsedTokens(index);
    }

    /**
     * Processes the order of operations based on standard mathematical precedence.
     *
     * @return The final result of the evaluated expression.
     */
    private String orderOfOperation() {
        if (this.size == 0)
            return null; // No tokens to evaluate

        if (this.size == 1)
            return this.tokens[0]; // Only one token

        // Evaluate parentheses first
        for (int i = 0; (i < this.size); ++i)
            if (this.tokens[i].equals("("))
                this.parenthesis(i--);

        // Evaluate exponents
        for (int i = 0; (i < this.size); ++i)
            if (this.tokens[i].equals("^"))
                this.exponents(i--);

        // Evaluate multiplication and division
        for (int i = 0; (i < this.size); ++i)
            if (this.tokens[i].equals("*"))
                this.multiplication(i--);
        for (int i = 0; (i < this.size); ++i)
            if (this.tokens[i].equals("/"))
                this.division(i--);

        // Evaluate addition and subtraction
        for (int i = 0; (i < this.size); ++i)
            if (this.tokens[i].equals("+"))
                this.addition(i--);
        for (int i = 0; (i < this.size); ++i)
            if (this.tokens[i].equals("-"))
                this.subtraction(i--);

        return this.tokens[0]; // Return the final result
    }

    /**
     * Evaluates the expression and returns the result as a string.
     *
     * @return The evaluated result of the expression.
     */
    public String equals() {
        // If the result is too long, return it without trailing zeros
        if (this.orderOfOperation().length() > 15)
            return new BigDecimal(this.orderOfOperation()).stripTrailingZeros().toString();

        return this.orderOfOperation(); // Return the result
    }
}
