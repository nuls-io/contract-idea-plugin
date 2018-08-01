package io.nuls.contract.rpc.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @desription:
 * @author: PierreLuo
 * @date: 2018/7/18
 */
@ApiModel(value = "估算调用智能合约的Gas消耗的表单数据")
public class ImputedGasContractCall {

    @ApiModelProperty(name = "sender", value = "交易创建者", required = true)
    private String sender;
    @ApiModelProperty(name = "contractAddress", value = "智能合约地址", required = true)
    private String contractAddress;
    @ApiModelProperty(name = "value", value = "交易附带的货币量", required = false)
    private long value;
    @ApiModelProperty(name = "methodName", value = "方法名", required = true)
    private String methodName;
    @ApiModelProperty(name = "methodDesc", value = "方法签名，如果方法名不重复，可以不传", required = false)
    private String methodDesc;
    @ApiModelProperty(name = "price", value = "执行合约单价", required = true)
    private long price;
    @ApiModelProperty(name = "password", value = "交易创建者账户密码", required = true)
    private String password;
    @ApiModelProperty(name = "args", value = "参数列表", required = false)
    private String[] args;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
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

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
