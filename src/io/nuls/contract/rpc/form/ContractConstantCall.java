package io.nuls.contract.rpc.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@ApiModel(value = "调用不上链的智能合约函数表单数据")
public class ContractConstantCall {

    @ApiModelProperty(name = "contractAddress", value = "智能合约地址", required = true)
    private String contractAddress;
    @ApiModelProperty(name = "methodName", value = "方法名", required = true)
    private String methodName;
    @ApiModelProperty(name = "methodDesc", value = "方法签名，如果方法名不重复，可以不传", required = false)
    private String methodDesc;
    @ApiModelProperty(name = "args", value = "参数列表", required = false)
    private String[] args;

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public void setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
