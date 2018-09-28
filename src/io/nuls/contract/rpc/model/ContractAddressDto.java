package io.nuls.contract.rpc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @desription:
 * @author: PierreLuo
 * @date: 2018/8/15
 */
@ApiModel(value = "ContractAddressDtoJSON")
public class ContractAddressDto {

    @ApiModelProperty(name = "contractAddress", value = "合约地址")
    private String contractAddress;

    @ApiModelProperty(name = "isCreate", value = "是否自己创建的合约")
    private boolean isCreate;

    @ApiModelProperty(name = "createTime", value = "创建时间")
    private long createTime;

    @ApiModelProperty(name = "height", value = "确认高度")
    private long height;

    @ApiModelProperty(name = "confirmCount", value = "确认次数")
    private long confirmCount;

    @ApiModelProperty(name = "remarkName", value = "备注名称")
    private String remarkName;

    @ApiModelProperty(name = "status", value = "合约状态")
    private int status;

    @ApiModelProperty(name = "msg", value = "错误信息")
    private String msg;

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public long getConfirmCount() {
        return confirmCount;
    }

    public void setConfirmCount(long confirmCount) {
        this.confirmCount = confirmCount;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }

    public boolean isCreate() {
        return isCreate;
    }

    public void setCreate(boolean create) {
        isCreate = create;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
