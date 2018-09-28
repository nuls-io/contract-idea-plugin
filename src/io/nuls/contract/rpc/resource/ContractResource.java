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

import io.nuls.contract.entity.ContractInfoDto;
import io.nuls.contract.rpc.form.*;
import io.nuls.contract.rpc.model.*;
import io.nuls.contract.rpc.result.ContractBalanceResult;
import io.nuls.contract.rpc.result.ContractCreateResult;
import io.nuls.contract.rpc.result.ContractInfoResult;
import io.nuls.kernel.model.RpcClientResult;
import io.swagger.annotations.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
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
    @Path("/constructor")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "获取智能合约构造函数")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success", response = ContractInfoDto.class)
    })
    RpcClientResult<ContractInfoDto> contractConstructor(@ApiParam(name = "createForm", value = "创建智能合约", required = true) ContractCode code);

    @POST
    @Path("/precreate")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "测试创建智能合约")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult preCreateContract(@ApiParam(name = "preCreateForm", value = "测试创建智能合约", required = true) PreContractCreate create);

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
    @Path("/transfer")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "向智能合约地址转账")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult transfer(@ApiParam(name = "transferFrom", value = "向合约地址转账", required = true) ContractTransfer transfer);

    @POST
    @Path("/transfer/fee")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "向智能合约地址转账手续费")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult transferFee(@ApiParam(name = "transferFeeFrom", value = "向合约地址转账手续费", required = true) ContractTransferFee transferFee);

    @POST
    @Path("/view")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "调用不上链的智能合约函数")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult invokeViewContract(
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
    @Path("/imputedprice")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "估算智能合约的price")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult imputedPrice(@ApiParam(name = "imputedPriceForm", value = "估算智能合约的price", required = true) ImputedPrice imputedPrice);

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
    RpcClientResult<ContractInfoResult> getContractInfo(
            @ApiParam(name = "address", value = "合约地址", required = true) @PathParam("address") String contractAddress,
            @ApiParam(name = "accountAddress", value = "钱包账户地址", required = false) @QueryParam("accountAddress") String accountAddress);

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
    @Path("/limit/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "根据address和limit查询合约UTXO")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success", response = ContractAccountUtxoDto.class)
    })
    RpcClientResult getUtxoByAddressAndLimit(
            @ApiParam(name = "address", value = "地址", required = true) @PathParam("address") String address,
            @ApiParam(name = "limit", value = "数量(不填查所有)", required = false) @QueryParam("limit") Integer limit);

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
    @Path("/tx/list/{contractAddress}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "获取智能合约的交易列表")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success", response = ContractTransactionInfoDto.class)
    })
    RpcClientResult getTxList(
            @ApiParam(name = "contractAddress", value = "智能合约地址", required = true)
            @PathParam("contractAddress") String contractAddress,
            @ApiParam(name = "pageNumber", value = "页码", required = true)
            @QueryParam("pageNumber") Integer pageNumber,
            @ApiParam(name = "pageSize", value = "每页条数", required = false)
            @QueryParam("pageSize") Integer pageSize,
            @ApiParam(name = "accountAddress", value = "钱包账户地址")
            @QueryParam("accountAddress") String accountAddress);

    @GET
    @Path("/token/list/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "获取NRC20合约的资产列表")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success", response = ContractTokenInfoDto.class)
    })
    RpcClientResult getTokenList(
            @ApiParam(name = "address", value = "钱包账户地址", required = true)
            @PathParam("address") String address,
            @ApiParam(name = "pageNumber", value = "页码", required = true)
            @QueryParam("pageNumber") Integer pageNumber,
            @ApiParam(name = "pageSize", value = "每页条数", required = false)
            @QueryParam("pageSize") Integer pageSize);

    @POST
    @Path("/collection")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "收藏智能合约地址/修改备注名称")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult contractCollection(@ApiParam(name = "collection", value = "收藏智能合约地址/修改备注名称", required = true) ContractCollection collection);

    @POST
    @Path("/collection/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "取消收藏智能合约地址")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult collectionCancel(@ApiParam(name = "collectionBase", value = "取消收藏参数", required = true) ContractAddressBase collection);

    @GET
    @Path("/wallet/list/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "获取钱包账户的合约地址列表(账户创建的合约以及钱包收藏的合约)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success", response = ContractAddressDto.class)
    })
    RpcClientResult getContractCollectionList(
            @ApiParam(name = "address", value = "钱包账户地址", required = true)
            @PathParam("address") String address,
            @ApiParam(name = "pageNumber", value = "页码", required = true)
            @QueryParam("pageNumber") Integer pageNumber,
            @ApiParam(name = "pageSize", value = "每页条数", required = false)
            @QueryParam("pageSize") Integer pageSize);

    @POST
    @Path("/unconfirmed/failed/remove")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "删除创建失败的未确认合约")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    RpcClientResult removeFailedUnconfirmed(@ApiParam(name = "ContractAddressBase", value = "删除未确认的合约", required = true)
                                                    ContractAddressBase addressBase);


    @GET
    @Path("/export/{address}")
    @ApiOperation(value = "导出合约编译代码的jar包 ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")
    })
    void export(@ApiParam(name = "address", value = "账户地址", required = true)
                @PathParam("address") String address,
                @Context HttpServletResponse response);

}
