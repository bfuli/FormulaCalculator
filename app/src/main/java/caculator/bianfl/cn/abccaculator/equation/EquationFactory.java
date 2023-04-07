package caculator.bianfl.cn.abccaculator.equation;

/**
 * Created by 福利 on 2018/6/11.
 */

public class EquationFactory {
    public static Equation createEquation(int i) {
        Equation equation;
        switch (i) {
            case 0:
                equation = new OneUnknownOneTimeEquation();
                break;
            case 1:
                equation = new OneUnknownTwoTimeEquation();
                break;
            case 2:
                equation = new OneUnknownThreeTimeEquation();
                break;
            case 3:
                equation = new TwoUnknownOneTimeEquation();
                break;
            case 4:
                equation = new ThreeUnknownOneTimeEquation();
                break;
            default:
                equation = new OneUnknownOneTimeEquation();
                break;
        }
        return equation;
    }
}
