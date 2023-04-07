package caculator.bianfl.cn.abccaculator.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.EmptyStackException;
import java.util.Stack;

public class Caculator {
    public static final char asin = 'ㄱ';
    public static final char acos = 'ㄳ';
    public static final char atan = 'ㄴ';
    public static final char lg = 'ㄵ';
    public static final char ln = 'ㄶ';
    public static final char sin = 'ㄷ';
    public static final char cos = 'ㄹ';
    public static final char tan = 'ㄽ';

    public static final String Caculate(String IFX, boolean useRadian){// 后缀表达式求值
        String PFX[] ;
        try {
            PFX = TrnsInToSufix(translateFushu(replaceSysCode(IFX) + "="));
        } catch (EmptyStackException e) {
            return "syntax error";
        }
        int i = 0;
        double x1, x2, n;
        BigDecimal b1, b2;
        String str;
        Stack<String> s = new Stack<>();
        for (int j = 0; j < PFX.length; j++) {
            str = PFX[i];
            switch (str.charAt(0)) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    s.push(str);
                    break;
                case '+':
                    x1 = Double.parseDouble(s.pop());
                    x2 = Double.parseDouble(s.pop());
                    b1 = new BigDecimal(Double.toString(x1));
                    b2 = new BigDecimal(Double.toString(x2));
                    n = b1.add(b2).doubleValue();
                    s.push(String.valueOf(n));
                    break;
                case '-':
                    x1 = Double.parseDouble(s.pop());
                    x2 = Double.parseDouble(s.pop());
                    b1 = new BigDecimal(Double.toString(x1));
                    b2 = new BigDecimal(Double.toString(x2));
                    n = b2.subtract(b1).doubleValue();
                    s.push(String.valueOf(n));
                    break;
                case '*':
                    x1 = Double.parseDouble(s.pop());
                    x2 = Double.parseDouble(s.pop());
                    b1 = new BigDecimal(Double.toString(x1));
                    b2 = new BigDecimal(Double.toString(x2));
                    n = b1.multiply(b2).doubleValue();
                    s.push(String.valueOf(n));
                    break;
                case '/':
                    x1 = Double.parseDouble(s.pop());
                    x2 = Double.parseDouble(s.pop());
                    b1 = new BigDecimal(Double.toString(x1));
                    b2 = new BigDecimal(Double.toString(x2));
                    n = b2.divide(b1, 16, RoundingMode.HALF_UP).doubleValue();
                    s.push(String.valueOf(n));
                    break;
                case sin:
                    x1 = Double.parseDouble(s.pop());
                    if (useRadian) {//使用弧度计算
                        n = Math.sin(x1);
                    } else {//不使用弧度计算，30°
                        n = Math.sin(x1 * Math.PI / 180);
                    }
                    s.push(String.valueOf(n));
                    break;
                case cos:
                    x1 = Double.parseDouble(s.pop());
                    if (useRadian) {//使用弧度计算
                        n = Math.cos(x1);
                    } else {//不使用弧度计算，30°
                        n = Math.cos(x1 * Math.PI / 180);
                    }
                    s.push(String.valueOf(n));
                    break;
                case tan:
                    x1 = Double.parseDouble(s.pop());
                    if (useRadian) {//使用弧度计算
                        n = Math.tan(x1);
                    } else {//不使用弧度计算，30°
                        n = Math.tan(x1 * Math.PI / 180);
                    }
                    s.push(String.valueOf(n));
                    break;
                case asin:
                    x1 = Double.parseDouble(s.pop());
                    n = Math.asin(x1);
                    s.push(String.valueOf(n));
                    break;
                case acos:
                    x1 = Double.parseDouble(s.pop());
                    n = Math.acos(x1);
                    s.push(String.valueOf(n));
                    break;
                case atan:
                    x1 = Double.parseDouble(s.pop());
                    n = Math.atan(x1);
                    s.push(String.valueOf(n));
                    break;
                case lg:
                    x1 = Double.parseDouble(s.pop());
                    n = Math.log10(x1);
                    s.push(String.valueOf(n));
                    break;
                case ln:
                    x1 = Double.parseDouble(s.pop());
                    n = Math.log(x1);
                    s.push(String.valueOf(n));
                    break;
                case '√':
                    x1 = Double.parseDouble(s.pop());
                    n = Math.sqrt(x1);
                    s.push(String.valueOf(n));
                    break;// 开方
                case '^':
                    x1 = Double.parseDouble(s.pop());
                    x2 = Double.parseDouble(s.pop());
                    n = Math.pow(x2, x1);
                    s.push(String.valueOf(n));
                    break;
            }
            i++;
        }
        return s.pop();
    }

    //去除 负号，将2√2换成2*√2,2e换成2*e
    private static final String translateFushu(String str) {
        char[] chars = str.toCharArray();
        //去除负号
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '-') {
                if (i == 0) {
                    chars[i] = '~';//第一位如果是'-'，表示负号，中转 用'~' 暂时代替'-'
                } else {
                    if (chars[i - 1] == '(' || chars[i - 1] == '/') {//'-'前面一位是左括号或者除号，表示负号
                        chars[i] = '~';
                    }
                }
            } else if (chars[i] == '+') {
                if (i == 0) {
                    chars[i] = '`';//第一位如果是'+'，表示负号，中转 用'`' 暂时代替'+'
                } else {
                    if (chars[i - 1] == '(' || chars[i - 1] == '/') {//'-'前面一位是左括号或者除号，表示正号
                        chars[i] = '`';
                    }
                }
            } else if (chars[i] == '√') {

            }
        }
        String re = new String(chars);
        str = re.replaceAll("~", "0-").replaceAll("`", "0+");
        //将类似 2√2换成2*√2,2e换成2*e，2s-->2*s
        StringBuffer sb = new StringBuffer(str);
        char[] cha = str.toCharArray();
        int j = 0;
        for (int i = 0; i < cha.length; i++) {
            if (i > 0) {
                Character be = new Character(cha[i - 1]);
                Character ne = new Character(cha[i]);
                //拼接正则匹配字符串
                StringBuffer temp_sb = new StringBuffer("[");
                temp_sb.append(sin).append(cos).append(tan).append(asin).append(acos).
                        append(atan).append(lg).append(ln).append("πe√(]");//"[sScCtTgnπe√(]"
                if (be.toString().matches("[0-9)πe]") && ne.toString().matches(temp_sb.toString())) {
                    sb.insert(i + j, "*");
                    j++;
                }
                if (be.toString().matches("[)πe]") && ne.toString().matches("[0-9]")) {
                    sb.insert(i + j, "*");
                    j++;
                }
            }
        }
        //替换字符
        return sb.toString().
                replaceAll("π", String.valueOf(Math.PI)).
                replaceAll("e", String.valueOf(Math.E));
    }

    //拆分中缀表达式为后缀
    private static final String[] TrnsInToSufix(String middleS) {
        Stack<String> s1 = new Stack<>();//运算符
        Stack<String> s2 = new Stack<>();//操作数
        StringBuffer numBuffer = new StringBuffer();
        char ch;
        for (int i = 0; i < middleS.length(); ) {
            ch = middleS.charAt(i);
            switch (ch) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    while (Character.isDigit(ch) || ch == '.'){// 拼数
                        numBuffer.append(ch); // 追加字符
                        ch = middleS.charAt(++i);
                    }
                    s2.push(numBuffer.toString());
                    numBuffer = new StringBuffer(); // 清空已获取的运算数字
                    continue; // 这里要重新循环，因为i已经增加过了
                case '(':
                    s1.push("(");
                    break;
                case ')':
                    while (!s1.empty() && !s1.peek().equals("(")) {
                        s2.push(s1.pop());
                    }
                    if (!s1.empty()) {
                        s1.pop();
                    }
                    break;
                case '+':
                case '-':
                    while ((!s1.empty()) && s1.peek() != "(") {
                        s2.push(s1.pop());
                    }
                    s1.push(String.valueOf(ch));
                    break;
                case '*':
                case '/':
                    while (!s1.empty() && !(s1.peek().equals("(") || s1.peek().equals("+") || s1.peek().equals("-"))) {
                        s2.push(s1.pop());
                    }
                    s1.push(String.valueOf(ch));
                    break;
                case sin:// 三角函数
                case cos:// 三角函数
                case tan:// 三角函数
                case asin://反三角函数
                case acos://反三角函数
                case atan://反三角函数
                case lg://以10为底的对数函数
                case ln://以e为底的对数函数
                    while (!s1.empty() && (s1.peek().equals(String.valueOf(sin))
                            || s1.peek().equals(String.valueOf(cos)) || s1.peek().equals(String.valueOf(tan))
                            || s1.peek().equals(String.valueOf(asin)) || s1.peek().equals(String.valueOf(acos))
                            || s1.peek().equals(String.valueOf(atan)) || s1.peek().equals(String.valueOf(lg))
                            || s1.peek().equals(String.valueOf(ln)))) {
                        s2.push(s1.pop());
                    }
                    s1.push(String.valueOf(ch));
                    break;
                case '^':// 幂
                case '√':// 开方
                    while (!s1.empty() && !(s1.peek().equals("(") || s1.peek().equals("+") || s1.peek().equals("-")
                            || s1.peek().equals("*") || s1.peek().equals("/")
                            || s1.peek().equals("^") || s1.peek().equals("√"))) {
                        s2.push(s1.pop());
                    }
                    s1.push(String.valueOf((ch)));
                    break;
            }
            i++;
        }
        while (!s1.empty()) {
            s2.push(s1.pop());
        }
        String[] Afs = new String[s2.size()];
        for (int j = Afs.length - 1; j >= 0; j--) {
            Afs[j] = s2.pop();

        }
        return Afs;
    }

    //替换为可识别计算的字符
    public static final String replaceSysCode(String s) {
        String exp = s.
                replaceAll("×", "*").
                replaceAll("÷", "/").
                replaceAll("asin", String.valueOf(asin)).
                replaceAll("acos", String.valueOf(acos)).
                replaceAll("atan", String.valueOf(atan)).
                replaceAll("lg", String.valueOf(lg)).
                replaceAll("ln", String.valueOf(ln)).
                replaceAll("sin", String.valueOf(sin)).
                replaceAll("cos", String.valueOf(cos)).
                replaceAll("tan", String.valueOf(tan));
        return exp;
    }
}
