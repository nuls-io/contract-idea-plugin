package io.nuls.contract.idea.plugin.model;

import io.nuls.contract.vm.program.ProgramMethod;
import io.nuls.contract.vm.program.ProgramMethodArg;

import java.util.List;

public class NulsContractMethod {
    private String name;

    private String desc;

    private List<ProgramMethodArg> args;

    private String returnArg;

    private boolean view;

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

    public List<ProgramMethodArg> getArgs() {
        return args;
    }

    public void setArgs(List<ProgramMethodArg> args) {
        this.args = args;
    }

    public String getReturnArg() {
        return returnArg;
    }

    public void setReturnArg(String returnArg) {
        this.returnArg = returnArg;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public static NulsContractMethod fromApiResult(ProgramMethod contractMethod) {
        NulsContractMethod method = new NulsContractMethod();
        if (contractMethod != null) {
            method.setName(contractMethod.getName());
            method.setDesc(contractMethod.getDesc());
            method.setArgs(contractMethod.getArgs());
            method.setReturnArg(contractMethod.getReturnArg());
            method.setView(contractMethod.isView());
        }
        return method;
    }

    @Override
    public String toString() {
        return this.name + " - " + this.desc;
    }
}
