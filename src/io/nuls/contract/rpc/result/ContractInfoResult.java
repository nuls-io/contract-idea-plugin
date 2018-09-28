package io.nuls.contract.rpc.result;

import io.nuls.contract.vm.program.ProgramMethod;
import io.nuls.kernel.model.ErrorData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class ContractInfoResult extends ErrorData {

    private String createTxHash;
    private String isCollect;
    private String address;
    private String creater;
    private String balance;
    private String txCount;
    private String createTime;
    private String blockHeight;
    private boolean isNrc20;
    private String nrc20TokenName;
    private String nrc20TokenSymbol;
    private String decimals;
    private String totalSupply;
    private String status;
    private List<ProgramMethod> method;

    public String getCreateTxHash() {
        return createTxHash;
    }

    public void setCreateTxHash(String createTxHash) {
        this.createTxHash = createTxHash;
    }

    public String getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(String isCollect) {
        this.isCollect = isCollect;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTxCount() {
        return txCount;
    }

    public void setTxCount(String txCount) {
        this.txCount = txCount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(String blockHeight) {
        this.blockHeight = blockHeight;
    }

    public boolean isNrc20() {
        return isNrc20;
    }

    public void setNrc20(boolean nrc20) {
        isNrc20 = nrc20;
    }
    
    public String getNrc20TokenName() {
        return nrc20TokenName;
    }

    public void setNrc20TokenName(String nrc20TokenName) {
        this.nrc20TokenName = nrc20TokenName;
    }

    public String getNrc20TokenSymbol() {
        return nrc20TokenSymbol;
    }

    public void setNrc20TokenSymbol(String nrc20TokenSymbol) {
        this.nrc20TokenSymbol = nrc20TokenSymbol;
    }

    public String getDecimals() {
        return decimals;
    }

    public void setDecimals(String decimals) {
        this.decimals = decimals;
    }

    public String getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(String totalSupply) {
        this.totalSupply = totalSupply;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ProgramMethod> getMethod() {
        return method;
    }

    public void setMethod(List<ProgramMethod> method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
