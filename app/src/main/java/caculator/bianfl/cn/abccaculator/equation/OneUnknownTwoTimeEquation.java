package caculator.bianfl.cn.abccaculator.equation;

import caculator.bianfl.cn.abccaculator.utils.NumberFormat;

/**
 * Created by 福利 on 2018/2/21.
 * 一元二次方程
 */

public class OneUnknownTwoTimeEquation implements Equation {
    private int quotietyNum;
    private String[] quotietyLabels;//系数
    private String result;
    private String expressions;
    private double[] quotietys;//系数的值
    public OneUnknownTwoTimeEquation(){
        quotietyLabels = new String[]{"a","b","c"};
        quotietyNum = quotietyLabels.length;
        expressions = "aX²+bX+c=0";
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
        double r1,r2;
        try {
            double a = quotietys[0];
            double b = quotietys[1];
            double c = quotietys[2];
            if (a == 0d){
                throw new Exception("a不能为0");
            }
            if ((b*b - 4*a*c)<0){
                throw new Exception("(b²-4ac) < 0");
            }
            r1 = ((-1*b)+Math.sqrt(b*b - 4*a*c))/2/a;
            r2 = ((-1*b)-Math.sqrt(b*b - 4*a*c))/2/a;
            result = "X₁ = " + NumberFormat.format(String.valueOf(r1))+
                    "\nX₂ = " + NumberFormat.format(String.valueOf(r2));
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
