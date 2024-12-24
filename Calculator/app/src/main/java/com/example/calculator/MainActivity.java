package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView display;
    private String input = "";
    private boolean isOperatorPressed = false;
    private boolean isEqualsPressed = false;

    private Button[] numberButtons;
    private Button[] operatorButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);
        display.setText("0");

        numberButtons = new Button[]{
                findViewById(R.id.button0), findViewById(R.id.button1), findViewById(R.id.button2),
                findViewById(R.id.button3), findViewById(R.id.button4), findViewById(R.id.button5),
                findViewById(R.id.button6), findViewById(R.id.button7), findViewById(R.id.button8),
                findViewById(R.id.button9)
        };

        for (Button button : numberButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNumberClick(((Button) v).getText().toString());
                }
            });
        }

        operatorButtons = new Button[]{
                findViewById(R.id.buttonAdd), findViewById(R.id.buttonSubtract),
                findViewById(R.id.buttonMultiply), findViewById(R.id.buttonDivide),
                findViewById(R.id.buttonOpenBracket), findViewById(R.id.buttonCloseBracket)
        };

        for (Button button : operatorButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOperatorClick(((Button) v).getText().toString());
                }
            });
        }

        Button buttonClear = findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClearClick();
            }
        });

        Button buttonEquals = findViewById(R.id.buttonEquals);
        buttonEquals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEqualsClick();
            }
        });
    }

    private void onNumberClick(String number) {
        if (isEqualsPressed) {
            input = "";
            isEqualsPressed = false;
            enableNumberButtons(true);
        }

        if (isOperatorPressed) {
            input += " ";
            isOperatorPressed = false;
        }

        input += number;
        display.setText(input);
        showToast(number);
    }

    private void onOperatorClick(String op) {
        if (op.equals("(") || op.equals(")")) {
            if (op.equals("(") && (input.isEmpty() || isOperator(input.charAt(input.length() - 1)))) {
                input += "0";
            }
            input += op;
        } else {
            if (!input.isEmpty() && !isOperator(input.charAt(input.length() - 1))) {
                input += " " + op + " ";
                isOperatorPressed = true;
                isEqualsPressed = false;
            }
        }
        display.setText(input);
        showToast(op);
    }

    private void onClearClick() {
        input = "";
        display.setText("0");
        enableNumberButtons(true);
        showToast("C");
    }

    private void onEqualsClick() {
        if (!input.isEmpty()) {
            try {
                String formattedInput = input.replaceAll(" ", "");
                int result = evaluateExpression(formattedInput);
                input = String.valueOf(result);
                display.setText(input);
                isEqualsPressed = true;
                enableNumberButtons(false);
                showToast("=");
            } catch (Exception e) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int evaluateExpression(String expression) throws Exception {
        return evaluatePostfix(convertToPostfix(expression));
    }

    private String convertToPostfix(String infix) {
        StringBuilder output = new StringBuilder();
        Stack<Character> operators = new Stack<>();
        infix = infix.replaceAll("\\s+", "");

        for (char token : infix.toCharArray()) {
            if (Character.isDigit(token)) {
                output.append(token);
            } else if (token == '(') {
                operators.push(token);
            } else if (token == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    output.append(' ').append(operators.pop());
                }
                operators.pop();
            } else if (token == '+' || token == '-' || token == '*' || token == '/') {
                output.append(' ');
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    output.append(operators.pop()).append(' ');
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            output.append(' ').append(operators.pop());
        }

        return output.toString();
    }

    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return 0;
        }
    }

    private int evaluatePostfix(String postfix) throws Exception {
        Stack<Integer> stack = new Stack<>();
        String[] tokens = postfix.split(" ");

        for (String token : tokens) {
            if (token.isEmpty()) continue;
            try {
                int number = Integer.parseInt(token);
                stack.push(number);
            } catch (NumberFormatException e) {
                int operand2 = stack.pop();
                int operand1 = stack.pop();
                switch (token) {
                    case "+":
                        stack.push(operand1 + operand2);
                        break;
                    case "-":
                        stack.push(operand1 - operand2);
                        break;
                    case "*":
                        stack.push(operand1 * operand2);
                        break;
                    case "/":
                        if (operand2 != 0) {
                            stack.push(operand1 / operand2);
                        } else {
                            throw new ArithmeticException("Cannot divide by zero");
                        }
                        break;
                }
            }
        }

        return stack.pop();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private void enableNumberButtons(boolean enable) {
        for (Button button : numberButtons) {
            button.setEnabled(enable);
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
