package caculator.bianfl.cn.abccaculator.equation;

import caculator.bianfl.cn.abccaculator.utils.NumberFormat;

/**
 * Created by 福利 on 2018/2/21.
 * 一元三次方程
 */

public class OneUnknownThreeTimeEquation implements Equation {
    private int quotietyNum;
    private String[] quotietyLabels;//系数
    private String result;
    private String expressions;
    private double[] quotietys;//系数的值
    public OneUnknownThreeTimeEquation(){
        quotietyLabels = new String[]{"a","b","c","d"};
        quotietyNum = quotietyLabels.length;
        expressions = "aX³+bX²+cX+d=0";
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
    public CharSequence getExpression() {
        return expressions;
    }

    /**
     * 返回方程组的所有解
     *
     * @return
     */
    @Override
    public String getResults(){
        double r1, r2, r3;
        try {
            double a = quotietys[0];
            double b = quotietys[1];
            double c = quotietys[2];
            double d = quotietys[3];
            double A = b*b - 3*a*c;
            double B = b*c - 9*a*d;
            double C =c*c - 3*b*d;
            double Δ = B*B - 4*A*C;

            if (a == 0d){
                throw new Exception("a不能为0");
            }
            if (A==B && A==0){
                r1 = -1*b/3/a;
                result = "X₁ = X₂ = X₃ = " + NumberFormat.format(String.valueOf(r1));
            }else if (Δ > 0){
                double Y1 = A*b +3*a*((-1*B + Math.sqrt(Δ))/2);
                double Y2 = A*b +3*a*((-1*B - Math.sqrt(Δ))/2);
                r1 = (-1*b-(cubeRoot(Y1)+cubeRoot(Y2)))/3/a;
                double t1,t2;
                t1 = (-1*b + 0.5 *(cubeRoot(Y1)+cubeRoot(Y2)))/3/a;
                t2 = (Math.sqrt(3)/2*(cubeRoot(Y1)-cubeRoot(Y2)))/3/a;
                t1 = Double.parseDouble(NumberFormat.format(String.valueOf(t1)));
                t2 = Double.parseDouble(NumberFormat.format(String.valueOf(t2)));

                result = "X₁ = " + NumberFormat.format(String.valueOf(r1))+
                        "\nX₂ = " + t1 + "+" + t2+"i"+
                        "\nX₃ = " + t1 + "-" + t2+"i";
            }else if (Δ  == 0){
                double K = B/A;
                r1 = -1*b/a+K;
                r2 = -1*K/2;
                result = "X₁ = "+NumberFormat.format(String.valueOf(r1))+
                        "X₂ = X₃ = "+NumberFormat.format(String.valueOf(r2));
            }else if (Δ < 0){
                double T = (2*A*b-3*a*B)/2/Math.sqrt(A*A*A);
                double θ = Math.acos(T);
                r1 = (-1*b - 2*Math.sqrt(A)*Math.cos(θ/3))/3/a;
                r2 = (-1*b + Math.sqrt(A)*(Math.cos(θ/3)+Math.sqrt(3)*Math.sin(θ/3)))/3/a;
                r3 = (-1*b + Math.sqrt(A)*(Math.cos(θ/3)-Math.sqrt(3)*Math.sin(θ/3)))/3/a;
                result = "X₁ = " + NumberFormat.format(String.valueOf(r1))+
                        "\nX₂ = " + NumberFormat.format(String.valueOf(r2))+
                        "\nX₃ = " + NumberFormat.format(String.valueOf(r3));
            }else{
                result = "无解";
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

    /**
     * 计算数的立方根
     * @param d
     * @return
     */
    private double cubeRoot(double d){
		if (d < 0) {
			return -1*Math.pow(Math.abs(d), 1.0/3);
		}
        return Math.pow(d, 1.0/3);
    }
}
