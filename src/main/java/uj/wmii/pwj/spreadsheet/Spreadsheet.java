package uj.wmii.pwj.spreadsheet;

public class Spreadsheet {

    public String[][] calculate(String[][] input) {
        int rows = input.length;
        int cols = input[0].length;
        String[][] result = new String[rows][cols];
        Integer[][] cache = new Integer[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                result[r][c] = String.valueOf(eval(input, r, c, cache));
            }
        }
        return result;
    }

    private int eval(String[][] src, int row, int col, Integer[][] cache) {
        if (cache[row][col] != null)
            return cache[row][col];

        String cell = src[row][col].trim();
        int value;

        //if number
        if (cell.matches("-?\\d+")) {
            value = Integer.parseInt(cell);
        }
        //if cell
        else if (cell.startsWith("$")) {
            int[] pos = parseRef(cell);
            value = eval(src, pos[0], pos[1], cache);
        }
        //if expression
        else if (cell.startsWith("=")) {
            value = evalFormula(src, cell, cache);
        }
        else {
            return -1; //should never hit
        }

        cache[row][col] = value;
        return value;
    }

    private int evalFormula(String[][] src, String formula, Integer[][] cache) {
        formula = formula.substring(1);
        String name = formula.substring(0, formula.indexOf('('));
        String args = formula.substring(formula.indexOf('(') + 1, formula.indexOf(')'));
        String[] parts = args.split(",");

        if (parts.length != 2)
            return -1; //should never hit

        int left = parseValue(src, parts[0].trim(), cache);
        int right = parseValue(src, parts[1].trim(), cache);

        return switch (name) {
            case "ADD" -> left + right;
            case "SUB" -> left - right;
            case "MUL" -> left * right;
            case "DIV" -> right == 0 ? 0 : left / right;
            case "MOD" -> right == 0 ? 0 : left % right;
            default -> -1; //should never hit
        };
    }

    private int parseValue(String[][] src, String token, Integer[][] cache) {
        if (token.matches("-?\\d+"))
            return Integer.parseInt(token);
        if (token.startsWith("$")) {
            int[] pos = parseRef(token);
            return eval(src, pos[0], pos[1], cache);
        }
        return -1; //should never hit
    }

    private int[] parseRef(String ref) {
        ref = ref.substring(1).toUpperCase();
        int col = 0;
        int i = 0;
        while (i < ref.length() && Character.isLetter(ref.charAt(i))) {
            col = col * 26 + (ref.charAt(i) - 'A' + 1);
            i++;
        }
        col--;
        int row = Integer.parseInt(ref.substring(i)) - 1;
        return new int[]{row, col};
    }
}
