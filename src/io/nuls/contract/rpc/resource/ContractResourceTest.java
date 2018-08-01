package io.nuls.contract.rpc.resource;

import feign.Feign;
import feign.gson.GsonEncoder;
import feign.jaxrs.JAXRSContract;
import io.nuls.contract.rpc.form.ContractCall;
import io.nuls.contract.rpc.form.ContractCreate;
import io.nuls.contract.rpc.model.ContractTransactionDto;
import io.nuls.kernel.model.RpcClientResult;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ContractResourceTest {

    private ContractResource contractResource;

    @Before
    public void setUp() {
        contractResource = Feign.builder()
                .encoder(new GsonEncoder())
                .decoder(new ResultfGsonDecoder())
                .contract(new JAXRSContract())
                .target(ContractResource.class, "http://127.0.0.1:8001/api");
    }

    @Test
    public void testCreateContract() throws IOException {
        String path = ContractResourceTest.class.getResource("contract.txt").getFile();
        String code = FileUtils.readFileToString(new File(path));
        ContractCreate create = new ContractCreate();
        create.setSender("Nse3fNhvidpAez94HDhX8x83mbYSMwTh");
        create.setPassword("abcd1234");
        //create.setSender("Nse4HuKNKfeGAbv3jssujku65JEKVcna");
        //create.setPassword("jjlc1234");
        create.setGasLimit(100000);//每次输入
        create.setPrice(1);//每次输入
        create.setRemark("备注");//每次输入
        create.setArgs(null);//每次输入
        create.setContractCode(code);
        RpcClientResult result = contractResource.createContract(create);
        System.out.println(result);
    }

    @Test
    public void testCallContract() {
        ContractCall call = new ContractCall();
        call.setSender("Nse3fNhvidpAez94HDhX8x83mbYSMwTh");
        call.setPassword("abcd1234");
        call.setGasLimit(100000);//每次输入
        call.setPrice(1);//每次输入
        call.setValue(0);//每次输入
        call.setRemark("备注");//每次输入
        call.setContractAddress("NseBdjuAhSsBTPjuEUsL5HD1vUPcDpTu");
        call.setMethodName("getStoredData");//选择的方法名
        call.setMethodDesc("");//选择的方法desc
        //call.setArgs(new String[]{"Nse3fNhvidpAez94HDhX8x83mbYSMwTh", "10"});//每次输入
        //call.setArgs(new String[]{"Nse3fNhvidpAez94HDhX8x83mbYSMwTh"});
        RpcClientResult result = contractResource.callContract(call);
        System.out.println(result);
    }

    @Test
    public void testGetContractTx() {
        RpcClientResult<ContractTransactionDto> result = contractResource.getContractTx("0020dd9ea1e26ae0fd700f921f7ec8d1663f76e8bd527b996057e26a4835a88d5490");
        System.out.println(result);
        String contractResult = null;
        String errorMessage = null;
        if (result.isSuccess()) {
            if (result.getData().getStatus() == 1) {
                if (result.getData().getContractResult().isError()) {
                    errorMessage = result.getData().getContractResult().getErrorMessage();
                } else {
                    //真正成功
                    contractResult = result.getData().getContractResult().getResult();
                }
            } else {
                //交易待确认
            }
        } else {
            errorMessage = result.getErrorData().getMsg() + "(" + result.getErrorData().getCode() + ")";
        }
        System.out.println("contractResult: " + contractResult);
        System.out.println("errorMessage: " + errorMessage);
    }

    @Test
    public void testValidateContractAddress() {
        RpcClientResult result = contractResource.validateContractAddress("Nse3fNhvidpAez94HDhX8x83mbYSMwTh");
        System.out.println(result);
    }

    @Test
    public void testGetTxList() {
        RpcClientResult result = contractResource.getTxList("Nse4HuKNKfeGAbv3jssujku65JEKVcna");
        System.out.println(result);
    }

    @Test
    public void testGetContractInfo() {
        RpcClientResult result = contractResource.getContractInfo("NseM452AXgNxMRQZc57Buwv4C2zD9ym5");
        System.out.println(result);
    }

}
