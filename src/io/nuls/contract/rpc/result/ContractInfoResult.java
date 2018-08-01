package io.nuls.contract.rpc.result;

import io.nuls.kernel.model.ErrorData;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class ContractInfoResult extends ErrorData {

    private String address;

    private String status;

    private List<ContractMethod> method;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ContractMethod> getMethod() {
        return method;
    }

    public void setMethod(List<ContractMethod> method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
