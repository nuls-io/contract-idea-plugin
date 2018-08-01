/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.contract.rpc.resource;

import io.nuls.contract.rpc.form.*;
import io.nuls.contract.rpc.model.ContractAccountUtxoDto;
import io.nuls.contract.rpc.model.ContractResultDto;
import io.nuls.contract.rpc.model.ContractTransactionDto;
import io.nuls.contract.rpc.model.ContractTransactionInfoDto;
import io.nuls.contract.rpc.result.ContractBalanceResult;
import io.nuls.contract.rpc.result.ContractCreateResult;
import io.nuls.contract.rpc.result.ContractInfoResult;
import io.nuls.kernel.model.RpcClientResult;
import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author: PierreLuo
 */
@Path("/contract")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "/contract", description = "contract")
public interface ContractResource {

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "创建智能合约")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult<ContractCreateResult> createContract(@ApiParam(name = "createForm", value = "创建智能合约", required = true) ContractCreate create);

    @POST
    @Path("/imputedgas/create")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "估算创建智能合约的Gas消耗")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult imputedGasCreateContract(@ApiParam(name = "imputedGasCreateForm", value = "估算创建智能合约的Gas消耗", required = true) ImputedGasContractCreate create);

    @POST
    @Path("/call")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "调用智能合约")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult callContract(@ApiParam(name = "callFrom", value = "调用智能合约", required = true) ContractCall call);

    @POST
    @Path("/constant")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "调用不上链的智能合约函数")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult callConstantContract(
            @ApiParam(name = "constantCallForm", value = "调用不上链的智能合约函数表单数据", required = true) ContractConstantCall constantCall);

    @POST
    @Path("/imputedgas/call")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "估算调用智能合约的Gas消耗")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult imputedGasCallContract(@ApiParam(name = "imputedGasCallForm", value = "估算调用智能合约的Gas消耗", required = true) ImputedGasContractCall call);

    @POST
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "删除智能合约")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult deleteContract(@ApiParam(name = "deleteFrom", value = "删除智能合约", required = true) ContractDelete delete);

    @GET
    @Path("/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "验证是否为合约地址")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult validateContractAddress(@ApiParam(name = "address", value = "地址", required = true)
                                            @PathParam("address") String address);

    @GET
    @Path("/info/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "获取智能合约信息")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult<ContractInfoResult> getContractInfo(@ApiParam(name = "address", value = "合约地址", required = true) @PathParam("address") String contractAddress);

    @GET
    @Path("/balance/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "获取智能合约余额")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult<ContractBalanceResult> getContractBalance(@ApiParam(name = "address", value = "合约地址", required = true) @PathParam("address") String contractAddress);

    @GET
    @Path("/result/{hash}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "获取智能合约执行结果")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success", response = ContractResultDto.class)
    })
    RpcClientResult<ContractResultDto> getContractTxResult(@ApiParam(name = "hash", value = "交易hash", required = true)
                                                           @PathParam("hash") String hash);

    @GET
    @Path("/tx/{hash}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "获取智能合约交易详情")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success", response = ContractTransactionDto.class)
    })
    RpcClientResult<ContractTransactionDto> getContractTx(@ApiParam(name = "hash", value = "交易hash", required = true)
                                                          @PathParam("hash") String hash);

    @GET
    @Path("/limit/{address}/{limit}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "根据address和limit查询合约UTXO")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success", response = ContractAccountUtxoDto.class)
    })
    RpcClientResult getUtxoByAddressAndLimit(
            @ApiParam(name = "address", value = "地址", required = true) @PathParam("address") String address,
            @ApiParam(name = "limit", value = "数量", required = true) @PathParam("limit") Integer limit);

    @GET
    @Path("/amount/{address}/{amount}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "根据address和amount查询合约UTXO")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success", response = ContractAccountUtxoDto.class)
    })
    RpcClientResult getUtxoByAddressAndAmount(
            @ApiParam(name = "address", value = "地址", required = true) @PathParam("address") String address,
            @ApiParam(name = "amount", value = "金额", required = true) @PathParam("amount") Long amount);

    @GET
    @Path("/tx/list/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "获取智能合约的交易列表")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success", response = ContractTransactionInfoDto.class)
    })
    RpcClientResult getTxList(
            @ApiParam(name = "address", value = "地址", required = true) @PathParam("address") String address);

}
