package caculator.bianfl.cn.abccaculator.equation;

import caculator.bianfl.cn.abccaculator.utils.NumberFormat;

/**
 * Created by 福利 on 2018/2/21.
 * 二元一次方程组
 */

public class TwoUnknownOneTimeEquation implements Equation {
    private int quotietyNum;
    private String[] quotietyLabels;//系数
    private String result;
    private String expressions;
    private double[] quotietys;//系数的值
    public TwoUnknownOneTimeEquation(){
        quotietyLabels = new String[]{"a₁","a₂","b₁","b₂","c₁","c₂"};
        quotietyNum = quotietyLabels.length;
        expressions = "a₁X+b₁Y=c₁\n" +
                "a₂X+b₂Y=c₂";
        quotietys = new double[quotietyNum];//设置时必须严格按照 a,b,c,d的顺序设置
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
    public String getExpression() {
        return expressions;
    }

    /**
     * 返回方程组的所有解
     *
     * @return
     */
    @Override
    public String getResults(){
        double x,y;
        try {
            double a1 = quotietys[0];
            double a2 = quotietys[1];
            double b1 = quotietys[2];
            double b2 = quotietys[3];
            double c1 = quotietys[4];
            double c2 = quotietys[5];
            if ((a1*b2-b1*a2)==0 && (a1*c2-c1*a2) == 0){
                result = "方程组有无数解";
            }else if((a1*b2-b1*a2) == 0 && (a1*c2-c1*a2) != 0){
                result = "无解";
            }else{
                y = (a1*c2-c1*a2)/(a1*b2-b1*a2);
                if(a1 == 0 && a2 != 0){
                    x = (c2-b2*y)/a2;
                }else{
                    x = (c1-b1*y)/a1;
                }
                result = "X = " + NumberFormat.format(String.valueOf(x)) +
                        "\nY = " + NumberFormat.format(String.valueOf(y));
            }
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
