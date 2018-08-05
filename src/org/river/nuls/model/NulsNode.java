package org.river.nuls.model;

public class NulsNode implements TreeItem {
    /**申请账户的地址 */
    private String agentAddress;

    /**打包地址 */
    private String packingAddress;

    /**结算地址 */
    private String rewardAddress;

    /**佣金比例 */
    private Double commissionRate;

    /**参与共识需要的总金额 */
    private Integer deposit;

    /**密码 */
    private String password;

    /**备注 */
    private String remark;

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getPackingAddress() {
        return packingAddress;
    }

    public void setPackingAddress(String packingAddress) {
        this.packingAddress = packingAddress;
    }

    public String getRewardAddress() {
        return rewardAddress;
    }

    public void setRewardAddress(String rewardAddress) {
        this.rewardAddress = rewardAddress;
    }

    public Double getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(Double commissionRate) {
        this.commissionRate = commissionRate;
    }

    public Integer getDeposit() {
        return deposit;
    }

    public void setDeposit(Integer deposit) {
        this.deposit = deposit;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static NulsNode byDefault(){
        NulsNode node = new NulsNode();
        node.setAgentAddress("");
        return node;
    }

    public String toString(){
        return this.agentAddress;
    }

}
