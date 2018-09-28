package io.nuls.contract.rpc.resource;

import feign.Feign;
import feign.gson.GsonEncoder;
import feign.jaxrs.JAXRSContract;
import io.nuls.contract.rpc.form.ContractCall;
import io.nuls.contract.rpc.form.ContractCreate;
import io.nuls.contract.rpc.form.ImputedGasContractCall;
import io.nuls.contract.rpc.form.ImputedGasContractCreate;
import io.nuls.contract.rpc.model.ContractTransactionDto;
import io.nuls.kernel.model.RpcClientResult;
import org.apache.commons.codec.binary.Hex;
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
//        String path = ContractResourceTest.class.getResource("contract.txt").getFile();
//        String code = FileUtils.readFileToString(new File(path));
        String path = "C:\\workspace\\nuls-contract-sample\\target\\nuls-contract-sample-1.0-SNAPSHOT.jar";
        byte[] bytes = FileUtils.readFileToByteArray(new File(path));
        String code = Hex.encodeHexString(bytes);
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
    public void testImputedGasCreateContract() throws IOException {
        String path = ContractResourceTest.class.getResource("contract.txt").getFile();
        String code = FileUtils.readFileToString(new File(path));
        ImputedGasContractCreate create = new ImputedGasContractCreate();
        create.setSender("Nse3fNhvidpAez94HDhX8x83mbYSMwTh");
        //create.setPassword("abcd1234");
        //create.setSender("Nse4HuKNKfeGAbv3jssujku65JEKVcna");
        //create.setPassword("jjlc1234");
        //create.setGasLimit(100000);//每次输入
        create.setPrice(1);//每次输入
        //create.setRemark("备注");//每次输入
        create.setArgs(null);//每次输入
        create.setContractCode(code);
        RpcClientResult result = contractResource.imputedGasCreateContract(create);
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
        call.setContractAddress("NseCPGu1b33XVLQyiyL8H6mZocVfsPY4");
        call.setMethodName("testForEach");//选择的方法名
        call.setMethodDesc("");//选择的方法desc
        //call.setArgs(new String[]{"Nse3fNhvidpAez94HDhX8x83mbYSMwTh", "10"});//每次输入
        //call.setArgs(new String[]{"Nse3fNhvidpAez94HDhX8x83mbYSMwTh"});
        RpcClientResult result = contractResource.callContract(call);
        System.out.println(result);
    }

    @Test
    public void testImputedGasCallContract() {
        ImputedGasContractCall call = new ImputedGasContractCall();
        call.setSender("Nse3fNhvidpAez94HDhX8x83mbYSMwTh");
        //call.setPassword("abcd1234");
        //call.setGasLimit(100000);//每次输入
        call.setPrice(1);//每次输入
        call.setValue(0);//每次输入
        //call.setRemark("备注");//每次输入
        call.setContractAddress("NseMfweHY4fG97FTFiNYiyD49o8yc8eP");
        call.setMethodName("getStoredData");//选择的方法名
        call.setMethodDesc("");//选择的方法desc
        //call.setArgs(new String[]{"Nse3fNhvidpAez94HDhX8x83mbYSMwTh", "10"});//每次输入
        //call.setArgs(new String[]{"Nse3fNhvidpAez94HDhX8x83mbYSMwTh"});
        RpcClientResult result = contractResource.imputedGasCallContract(call);
        System.out.println(result);
    }

    @Test
    public void testGetContractTx() {
        RpcClientResult<ContractTransactionDto> result = contractResource.getContractTx("00204adb4bc1b343103486bfaab8d984e6f88690707d89506bfb226f4b72f26be59f");
        System.out.println(result);
        String contractResult = null;
        String errorMessage = null;
        if (result.isSuccess()) {
            if (result.getData().getStatus() == 1) {
                if (!result.getData().getContractResult().isSuccess()) {
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
    public void testGetContractInfo() {
        RpcClientResult result = contractResource.getContractInfo("NseCPGu1b33XVLQyiyL8H6mZocVfsPY4", null);
        System.out.println(result);
    }

}
