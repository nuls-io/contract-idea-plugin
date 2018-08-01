package org.river.nuls.model;

import io.nuls.contract.rpc.result.ContractMethod;

import java.util.List;

public class NulsContractMethod {
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

    public static NulsContractMethod fromApiResult(ContractMethod contractMethod){
        NulsContractMethod method = new NulsContractMethod();
        if (contractMethod != null){
            method.setName(contractMethod.getName());
            method.setDesc(contractMethod.getDesc());
            method.setArgs(contractMethod.getArgs());
            method.setReturnArg(contractMethod.getReturnArg());
        }
        return method;
    }

    @Override
    public String toString() {
        return this.name + " - " + this.desc;
    }
}
