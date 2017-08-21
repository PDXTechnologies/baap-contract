/*************************************************************************
 * Copyright (C) 2016 PDX Technologies, Inc. <jz@pdxtech.biz>
 *
 * This file is part of PDX DaaP API project.
 *
 * DaaP Container is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * DaaP Container is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *************************************************************************/

package life.pdx.dapp.sample.oobm.contract;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.oobm.common.constant.OOBMMetaName;
import org.oobm.common.utils.CryptoUtil;
import org.oobm.common.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import biz.pdxtech.daap.api.contract.IDapp;
import biz.pdxtech.daap.api.contract.IDappExt;
import biz.pdxtech.daap.api.contract.Transaction;
import biz.pdxtech.daap.api.contract.TransactionResp;
import biz.pdxtech.daap.common.cryptohash.Keccak256;
import life.pdx.dapp.sample.oobm.contract.constant.Constant;
import life.pdx.dapp.sample.oobm.contract.constant.DaapHeader;
import life.pdx.dapp.sample.oobm.contract.constant.StatusEnum;

@Path("/biz.pdxtech/sample/oobm")
public class SampleContract implements IDapp, IDappExt {

	private Logger logger = LoggerFactory.getLogger(SampleContract.class);

	@POST
	@Path("/query")
	@Consumes({ "application/pdx-daap" })
	@Produces({ "application/json" })
	public List<Transaction> query(@Context HttpHeaders httpHeaders, Transaction transaction) {
		// pass
		return null;
	}

	@POST
	@Path("/apply")
	@Consumes({ "application/pdx-daap" })
	@Produces({ "application/json" })
	public TransactionResp apply(@Context HttpHeaders httpHeaders, Transaction transaction) {
		String txId = httpHeaders.getHeaderString(DaapHeader.TXID.getContent());
		logger.info("txId: {}", txId);
		TransactionResp resp = newResp(getDappId(SampleContract.class), txId);
		
		Map<String, byte[]> meta = transaction.getMeta();
		meta.forEach((k,v)->{logger.info("meta, key: {}, value: {}", k, Hex.toHexString(v));});
		byte [] b  = meta.get(OOBMMetaName.OOBMHASH.getName());
		logger.info("hashes: {}",  Hex.toHexString(b));
		Map<?, ?> hashes = JsonUtil.fromJson(b, Map.class);
		hashes.forEach((k,v)->{logger.info("Hash, key: {}, value: {}", k, v);});

		String jobId =	new String(meta.get(OOBMMetaName.OOBMID.getName()), Charset.forName(Constant.DEFAULT_CHARSET));
		String dataFile	=	httpHeaders.getHeaderString("OOBM-SAMPLE-DATA");
		logger.info("jobId: {}, data: {}", jobId, dataFile);
		try {
			List<String> list = IOUtils.readLines(new FileInputStream(dataFile));
			logger.info("file content: ");
			list.forEach(t-> logger.info(t));
			
			byte [] content = IOUtils.toByteArray(new FileInputStream(dataFile));
			Keccak256 digest = new Keccak256();
			digest.update(content, 0, content.length);
			byte[] hash  = digest.digest();
			logger.info("hash: "+CryptoUtil.toBase64(hash));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			returnResp(StatusEnum.FAIL.getValue(), "ERROR", resp);
		}
		return returnResp(StatusEnum.SUCCESS.getValue(), "OK", resp);
	}


	@POST
	@Consumes({ "application/octet-stream" })
	@Path("/oobm")
	@Produces({ "application/json" })
	public Response oobm(@Context HttpHeaders httpHeaders, InputStream input) {
		return Response.ok().build();
	}

	private TransactionResp newResp(String dst, String txId) {
        TransactionResp resp = new TransactionResp();
        resp.setDappId(dst);
        resp.setTxId(txId);
        resp.setTimestamp(System.currentTimeMillis());
        return resp;
    }
	
	private TransactionResp returnResp(String status, String reason, TransactionResp resp) {
        resp.setStatus(status);
        resp.setReason(reason);
        return resp;
    }
	
	private String getDappId(Class clazz) {
		try {
			Annotation an = clazz.getAnnotation(javax.ws.rs.Path.class);
			Class<? extends Annotation> type = an.annotationType();
			Method meth = type.getMethod("value", (Class<?>[]) null);
			Object path = meth.invoke(an, (Object[]) null);
			return (String) path;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
