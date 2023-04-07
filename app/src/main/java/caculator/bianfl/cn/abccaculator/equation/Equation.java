package caculator.bianfl.cn.abccaculator.equation;

/**
 * Created by 福利 on 2018/2/21.
 * 解方程的方程接口，包括元、次，获取方程解，获取方程组中每个方程
 */

public interface Equation {
    /**
     * 返回方程系数个数
     * @return
     */
    int getQuotietyNum();

    /**
     * 返回方程系数标签数组
     * @return
     */
    CharSequence[] getQuotietyLabels();
    /**
     * 返回方程组中每个方程表达式 格式化输出
     * @return
     */
    CharSequence getExpression();

    /**
     * 返回方程组的所有解,并格式化输出
     * @return
     */
    CharSequence getResults();

    /**
     * 设置所有的系数
     * @param d
     */
    void setQuotietys(double...d);
}
