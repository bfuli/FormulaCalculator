package caculator.bianfl.cn.abccaculator.equation;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.SubscriptSpan;

import Jama.Matrix;
import caculator.bianfl.cn.abccaculator.utils.NumberFormat;

/**
 * Created by 福利 on 2018/2/21.
 * 三元一次方程
 */

public class ThreeUnknownOneTimeEquation implements Equation {
    private int quotietyNum;
    private String[] quotietyLabels;//系数
    private String result;
    private String expressions;
    private double[] quotietys;//系数的值
    private double[][] mq;//系数二维数组，用于生成矩阵

    public ThreeUnknownOneTimeEquation() {
        quotietyLabels = new String[]{"a₁", "b₁", "c₁", "d₁", "a₂", "b₂", "c₂", "d₂", "a₃", "b₃", "c₃", "d₃"};
        quotietyNum = quotietyLabels.length;
        expressions = "a₁X+b₁Y+c₁Z=d₁" +
                "\na₂X+b₂Y+c₂Z=d₂" +
                "\na₃X+b₃Y+c₃Z=d₃";
        quotietys = new double[quotietyNum];//设置时必须严格按照 a,b,c,d的顺序设置
        mq = new double[3][3];
    }

    /**
     * 返回方程系数个数
     *
     * @return
     */
    @Override
    public int getQuotietyNum() {
        return quotietyNum;
    }

    /**
     * 返回方程系数标签数组
     *
     * @return
     */
    @Override
    public String[] getQuotietyLabels() {
        return quotietyLabels;
    }

    /**
     * 返回方程组中每个方程表达式
     *
     * @return
     */
    @Override
    public CharSequence getExpression() {
        return expressions;
    }

    /**
     * 返回方程组的所有解
     *
     * @return
     */
    @Override
    public String getResults() {
        double x, y, z;
        initArray();//初始化数组数据
        Matrix mD = new Matrix(mq);
        double D = mD.det();
        if (D == 0) {
            result = "无解";
        } else {
            mq[0][0] = quotietys[3];//将a1替换为d1
            mq[1][0] = quotietys[7];//将a2替换为d2
            mq[2][0] = quotietys[11];//将a2替换为d2
            mD = new Matrix(mq);
            double Dx = mD.det();
            x = Dx / D;

            initArray();
            mq[0][1] = quotietys[3];//将b1替换为d1
            mq[1][1] = quotietys[7];//将b2替换为d2
            mq[2][1] = quotietys[11];//将b2替换为d2
            mD = new Matrix(mq);
            double Dy = mD.det();
            y = Dy / D;

            initArray();
            mq[0][2] = quotietys[3];//将c1替换为d1
            mq[1][2] = quotietys[7];//将c2替换为d2
            mq[2][2] = quotietys[11];//将c2替换为d2
            mD = new Matrix(mq);
            double Dz = mD.det();
            z = Dz / D;
            result = "X = " + NumberFormat.format(String.valueOf(x)) +
                    "\nY = " + NumberFormat.format(String.valueOf(y)) +
                    "\nZ = " + NumberFormat.format(String.valueOf(z));
        }
        return result;
    }

    /**
     * 初始化数组数据
     */
    private final void initArray(){
        mq[0] = new double[]{quotietys[0], quotietys[1], quotietys[2]};//a1,b1,c1
        mq[1] = new double[]{quotietys[4], quotietys[5], quotietys[6]};//a2,b2,c2
        mq[2] = new double[]{quotietys[8], quotietys[9], quotietys[10]};//a3,b3,c3
    }
    /**
     * 设置所有的系数
     *
     * @param d
     */
    @Override
    public void setQuotietys(double... d) {
        for (int i = 0; i < d.length; i++) {
            quotietys[i] = d[i];
        }
    }
}
