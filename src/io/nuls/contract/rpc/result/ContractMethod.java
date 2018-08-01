package io.nuls.contract.rpc.result;

import io.nuls.kernel.model.ErrorData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class ContractMethod extends ErrorData {

    private String name;

    private String desc;

    private List<String> args;

    private String returnArg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public String getReturnArg() {
        return returnArg;
    }

    public void setReturnArg(String returnArg) {
        this.returnArg = returnArg;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
