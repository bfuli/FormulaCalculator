package caculator.bianfl.cn.abccaculator.equation;

import caculator.bianfl.cn.abccaculator.utils.NumberFormat;

/**
 * Created by 福利 on 2018/2/21.
 * 一元一次方程
 */

public class OneUnknownOneTimeEquation implements Equation {
    private int quotietyNum;
    private String[] quotietyLabels;//系数
    private String result;
    private String expressions;
    private double[] quotietys;//系数的值
    public OneUnknownOneTimeEquation(){
        quotietyLabels = new String[]{"a","b"};
        quotietyNum = quotietyLabels.length;
        expressions = "aX+b=0";
        quotietys = new double[quotietyNum];//设置时必须严格按照 a,b,c的顺序设置
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
    public CharSequence[] getQuotietyLabels() {
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
    public CharSequence getResults(){
        double r;
        try {
            double a = quotietys[0];
            double b = quotietys[1];
            if (a == 0d){
                throw new Exception("a不能为0");
            }
            r = (-1)*b / a;
            result = "X = " + NumberFormat.format(String.valueOf(r));
        }catch (Exception e){
            result = "无解";
        }
        return result;
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
