package org.river.nuls.model;

public class NulsContract implements TreeItem {
    /**账户地址*/
    private String address;

    /**备注*/
    private String remark;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static NulsContract byDefault(){
        NulsContract contract = new NulsContract();
        contract.setAddress("the address");
        return contract;
    }

    public String toString(){
        return this.address;
    }
}
