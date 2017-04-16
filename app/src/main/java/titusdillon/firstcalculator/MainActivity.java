package titusdillon.firstcalculator;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

//TODO: BUG - text at top of screen gets cut off and extra space gets added to bottom - cant scroll up to see previous work

public class MainActivity extends AppCompatActivity {

    ArrayList<String> equation = new ArrayList<>();
    String operators = "+-\u00d7\u00f7^()";
    boolean newLineStandby = false;

    ScrollView scroll;

    void cleanEquation() {
        equation.clear();
        equation.add("0");
    }

    void printAnswer(LinearLayout screen) {

        TextView answerText = new TextView(getApplicationContext());
        answerText.setText("= " + equation.get(0));
        answerText.setTextSize(40);
        answerText.setTextColor(Color.BLACK);
        answerText.setBackgroundColor(Color.rgb(230,230,255));

        screen.addView(answerText);
    }

    void removeOldSymbol(TextView screen) {
        CharSequence text = screen.getText();
        text = text.subSequence(0, text.length() - 1);

        screen.setText(text);
    }

    // double calculateRoot(int nthroot, double number) {}

    double calculateExponent(double base, double exponent) {

        if (exponent == 0) {
            return 1.0;
        }

        /* TODO: implement fraction exponent
        if (exponent > 0 && exponent < 1) {
            return calculateRoot() * calculateExponent(base, 0);
        }
        */

        return base * calculateExponent(base, exponent - 1);
    }

    void calculateMultDivPow(String operator, ArrayList<String> problem) {

        int operatorLocation = problem.indexOf(operator);
        double num1 = Double.parseDouble(problem.get(operatorLocation - 1));
        double num2 = Double.parseDouble(problem.get(operatorLocation + 1));
        double result;

        switch (operator) {
            case "^":
                result = calculateExponent(num1, num2);
                break;
            case "\u00d7":
                result = num1 * num2;
                break;
            case "\u00f7":
                result = num1 / num2;
                break;
            default:
                result = 0;
                break;
        }

        /*
        * Replaces the operator with the result from the operation then removes the operands before
        * and after the operator location's previous position.
        * NOTE: Although the third operation may seem to remove the new result placed there, after
        * the removal of the first operand the index of the second will be one value lower:
        * equivalent to the value of [operatorLocation]
        */
        problem.set(operatorLocation, Double.toString(result));
        problem.remove(operatorLocation - 1);
        problem.remove(operatorLocation);
    }

    void calculate(ArrayList<String> problem) {

        int i;

        /*
        * Recursively digs out from rightmost open parenthesis solving each problem until the first
        * close parenthesis it finds. Replaces entire parenthesis statement with calculated answer
        * and repeats until no parentheses are found.
        * NOTE: Both the first while and if statements handle parentheses.
        */
        while (problem.contains("(")) {
            ArrayList<String> subProblem = new ArrayList<>();
            for (i = problem.lastIndexOf("(")+1; i < problem.size(); i++) {
                subProblem.add(problem.get(i));
            }

            calculate(subProblem);
            for (i = problem.lastIndexOf("("); !problem.get(i).equals(")"); i++) {
                // This space is needed so "i" increments (for some reason)
            }

            for (; i > problem.lastIndexOf("("); i--) {
                problem.remove(i);
            }

            problem.set(problem.lastIndexOf("("), subProblem.get(0));
        }
        if (problem.contains(")")) {
            ArrayList<String> subProblem = new ArrayList<>();
            for (i = 0; i < problem.indexOf(")"); i++) {
                subProblem.add(problem.get(i));
            }

            calculate(subProblem);
            for (i = problem.indexOf(")"); i != 0; i--) {
                problem.remove(i);
            }

            problem.set(i, subProblem.get(0));
            return;
        }
        // Converts numbers proceeded by - into negative number
        while (problem.contains("-")) {
            int op = problem.indexOf("-");
            problem.set(op + 1, problem.get(op) + problem.get(op + 1));
            problem.remove(op);
        }

        while (problem.contains("^")) {
            calculateMultDivPow("^", problem);
        }
        while (problem.contains("\u00d7")) {
            calculateMultDivPow("\u00d7", problem);
        }
        while (problem.contains("\u00f7")) {
            calculateMultDivPow("\u00f7", problem);
        }
        while (problem.contains("+")) {
            problem.remove("+");
        }

        double total = 0.0;
        for (i = 0; i < problem.size(); i++) {
            total += Double.parseDouble(problem.get(i));
        }

        // Allows user to continue input from previous calculation
        problem.clear();
        problem.add(Double.toString(total));
    }

    /*
    * Appends numerical inputs to end of last entry if it is numerical, otherwise adds input to new
    * index. Operators are added as new indices. Additional multiplication symbols are added to the
    * front and back of parenthesis depending on the outside values.
    */
    void handleInput(LinearLayout screen, String symbol) {

        String lastSymbol = equation.get(equation.size()-1);
        boolean isLastSymbolOperator = operators.contains(lastSymbol);
        boolean isLastSymbolZero = lastSymbol.equals("0");

        if (equation.size() == 1 && isLastSymbolZero) {
            equation.set(0, symbol);
        }
        else if (operators.contains(symbol)) {

            System.out.println("Symbol");
            // prevents operators from being inputted twice (exception of parentheses)
            // - removes ability for symbols to replace parentheses
            // TODO: BUG - "-" causes new input to be considered, and thus allows multiple operators to be consecutively inputted
            if (!symbol.equals("(") && !symbol.equals(")") && !symbol.equals("-") && isLastSymbolOperator) {

                if (symbol.equals(lastSymbol)) {
                    return;
                }
                else if (!lastSymbol.equals("(") && !lastSymbol.equals(")")) {
                    equation.remove(equation.size() - 1);
                    removeOldSymbol((TextView)screen.getChildAt(screen.getChildCount() - 1));
                }
            }
            // adds multiplication operator between parentheses & numbers
            if (symbol.equals("(") && ((!isLastSymbolOperator && !isLastSymbolZero) || lastSymbol.equals(")"))) {
                equation.add("\u00d7");
            }
            // changes double negatives to addition
            if (symbol.equals("-") && lastSymbol.equals("-")) {
                equation.set(equation.size() - 1, "+");
                TextView currentText = (TextView) screen.getChildAt(screen.getChildCount() - 1);
                currentText.append(symbol);
                return;
            }
            equation.add(symbol);
        }
        else {

            if (newLineStandby) {
                cleanEquation();
            }
            if (equation.get(equation.size()-1).equals(")")) {
                equation.add("\u00d7");
            }
            if (isLastSymbolOperator && !lastSymbol.equals("-")) {
                equation.add("");
            }
            int currentEntry = equation.size() - 1;
            equation.set(currentEntry, equation.get(currentEntry) + symbol);
        }
        TextView currentText = (TextView) screen.getChildAt(screen.getChildCount() - 1);
        currentText.append(symbol);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cleanEquation();

        scroll = (ScrollView) findViewById(R.id.scroll);
        final LinearLayout screen = (LinearLayout) findViewById(R.id.screen);

        scroll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scroll.post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

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
                            (Button) findViewById(R.id.buttondivide),
                            (Button) findViewById(R.id.buttonlparen),
                            (Button) findViewById(R.id.buttonrparen),
                            (Button) findViewById(R.id.buttonpower),
                            (Button) findViewById(R.id.buttondec)};

        final String inputs[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "-", "\u00d7", "\u00f7", "(", ")", "^", "."};

        buttonequals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculate(equation);
                printAnswer(screen);
                newLineStandby = true;
            }
        });

        buttonclr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView blankView = new TextView(getApplicationContext());
                blankView.setTextSize(40);
                blankView.setTextColor(Color.BLACK);

                screen.addView(blankView);
                cleanEquation();
            }
        });
        buttonclr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                screen.removeAllViews();
                cleanEquation();
                newLineStandby = true;
                return true;
            }
        });

        for (int i = 0; i < buttons.length; i++) {
            final int x = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (newLineStandby) {
                        TextView blankView = new TextView(getApplicationContext());
                        blankView.setTextSize(40);
                        blankView.setTextColor(Color.BLACK);

                        screen.addView(blankView);
                    }
                    handleInput(screen, inputs[x]);
                    newLineStandby = false;
                }
            });
        }
    }
}