package caculator.bianfl.cn.abccaculator.beans;

/**
 * Created by 福利 on 2016/9/20.
 */
public class MathObject {
    private String expression, unknown, result,formulaName;
    private int id;

    public MathObject(){}
    public MathObject(String result,String unknown){
        this(0,result,unknown,null,null);
    }
    public MathObject(String result,String unknown,String expression,String formulaName){
        this(0,result,unknown,expression,formulaName);
    }
    public MathObject(int id,String result,String unknown,String expression,String formulaName){
        this.id = id;
        this.result = result;
        this.unknown = unknown;
        this.expression = expression;
        this.formulaName = formulaName;
    }
    public String getFormulaName() {
        return formulaName;
    }

    public void setFormulaName(String formulaName) {
        this.formulaName = formulaName;
    }

    public String getExpression() {
        return expression;
    }

    public String getResult() {
        return result;
    }

    public String getUnknown() {
        return unknown;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setExpression(String expression){
        this.expression = expression;
    }
    public void setUnknown(String unknown){
        this.unknown = unknown;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o instanceof MathObject){
            MathObject mo = (MathObject) o;
            return (this.getFormulaName().equals(mo.getFormulaName()) &&
                    this.getUnknown().equals(mo.getUnknown()) &&
                    this.getExpression().equals(mo.getExpression()) &&
                    this.getResult().equals(mo.getResult()));
        }else {
            return false;
        }

    }
}
