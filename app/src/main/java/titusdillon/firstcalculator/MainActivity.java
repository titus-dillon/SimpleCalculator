package titusdillon.firstcalculator;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> equation = new ArrayList<>();
    String operators = "+-*/";
    boolean drop = false;

    void cleanEquation() {
        equation.clear();
        equation.add("0");
    }

    void setDrop(boolean drop) {
        this.drop = drop;
    }

    boolean checkDrop() {
        return drop;
    }

    void calculateMultDiv(String operator) {

        int operatorLocation = equation.indexOf(operator);
        double num1 = Double.parseDouble(equation.get(operatorLocation - 1));
        double num2 = Double.parseDouble(equation.get(operatorLocation + 1));
        double result = 0;

        if (operator == "*") {
            result = num1 * num2;
        } else {
            result = num1 / num2;
        }

        // Replaces the operator with the result from the operation then removes the operands
        // before and after the operator location's previous position.
        // NOTE: Although the third operation may seem to remove the new result placed there,
        // after the removal of the first operand the index of the second, will be one value
        // lower: equivalent to the value of [operatorLocation]
        equation.set(operatorLocation, Double.toString(result));
        equation.remove(operatorLocation - 1);
        equation.remove(operatorLocation);
    }
    void calculate(View view, TextView screen) {

        while (equation.contains("*")) {
            calculateMultDiv("*");
        }
        while (equation.contains("/")) {
            calculateMultDiv("/");
        }
        while (equation.contains("-")) {
            int op = equation.indexOf("-");
            equation.set(op + 1, "-" + equation.get(op + 1));
            equation.remove(op);
        }
        while (equation.contains("+")) {
            equation.remove("+");
        }

        double total = 0.0;
        for (int i = 0; i < equation.size(); i++) {
            total += Double.parseDouble(equation.get(i));
        }
        screen.append("\n");
        screen.append(Double.toString(total));
    }

    // Appends numerical inputs to end of last entry until an operator is entered where it
    // will then enter the operator as a new index then add another index for numerical
    // input.
    void onButtonClick(View view, TextView box, String symbol) {

        if (operators.contains(symbol)) {
            equation.add(symbol);
            equation.add("0");
        }
        else {
            int currentNumber = equation.size() - 1;
            equation.set(currentNumber, equation.get(currentNumber) + symbol);
        }

        box.append(symbol);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cleanEquation();

        final TextView screen = (TextView) findViewById(R.id.textView);
        screen.setMovementMethod(new ScrollingMovementMethod());

        Button buttonequals = (Button) findViewById(R.id.buttonequals);
        Button buttonclr = (Button) findViewById(R.id.buttonclr);
        Button buttons[] = {(Button) findViewById(R.id.button0),
                            (Button) findViewById(R.id.button1),
                            (Button) findViewById(R.id.button2),
                            (Button) findViewById(R.id.button3),
                            (Button) findViewById(R.id.button4),
                            (Button) findViewById(R.id.button5),
                            (Button) findViewById(R.id.button6),
                            (Button) findViewById(R.id.button7),
                            (Button) findViewById(R.id.button8),
                            (Button) findViewById(R.id.button9),
                            (Button) findViewById(R.id.buttonplus),
                            (Button) findViewById(R.id.buttonminus),
                            (Button) findViewById(R.id.buttontimes),
                            (Button) findViewById(R.id.buttondivide)};

        final String inputs[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "-", "*", "/"};

        buttonequals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculate(view, screen);
                setDrop(true);
                cleanEquation();
            }
        });

        buttonclr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screen.setText("");
                cleanEquation();
            }
        });

        for (int i = 0; i < buttons.length; i++) {
            final int x = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkDrop()) {
                        screen.append("\n");
                    }
                    onButtonClick(view, screen, inputs[x]);
                    setDrop(false);
                }
            });
        }
    }
}