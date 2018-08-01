package io.nuls.contract.rpc.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @desription:
 * @author: PierreLuo
 * @date: 2018/4/20
 */
@ApiModel(value = "创建智能合约表单数据")
public class ContractCreate extends ContractBase {

    @ApiModelProperty(name = "contractCode", value = "智能合约代码(字节码的Hex编码字符串)", required = true)
    private String contractCode;
    @ApiModelProperty(name = "args", value = "参数列表", required = false)
    private String[] args;

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public void args(String... args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
