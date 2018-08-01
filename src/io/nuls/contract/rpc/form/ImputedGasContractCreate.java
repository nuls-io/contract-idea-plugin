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
@ApiModel(value = "估算创建智能合约的Gas消耗的表单数据")
public class ImputedGasContractCreate {

    @ApiModelProperty(name = "sender", value = "交易创建者", required = true)
    private String sender;
    @ApiModelProperty(name = "price", value = "执行合约单价", required = true)
    private long price;
    @ApiModelProperty(name = "password", value = "交易创建者账户密码", required = true)
    private String password;
    @ApiModelProperty(name = "contractCode", value = "智能合约代码(字节码的Hex编码字符串)", required = true)
    private String contractCode;
    @ApiModelProperty(name = "args", value = "参数列表", required = false)
    private String[] args;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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
