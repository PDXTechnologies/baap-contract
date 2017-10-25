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

package life.pdx.bapp.sample.oobm.contract;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.pdxtech.baap.api.contract.BaapContext;
import biz.pdxtech.baap.api.contract.IContract;
import biz.pdxtech.baap.api.contract.Transaction;
import biz.pdxtech.baap.api.contract.TransactionResp;

@Path("/biz.pdxtech/sample/oobmtest")
public class SampleContract implements IContract {

	private Logger logger = LoggerFactory.getLogger(SampleContract.class);

    @Override
    @POST
    @Path("/query")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    @Consumes({"application/pdx-baap"})
    public InputStream query(@Context BaapContext ctx, byte[] qstr) {
    	logger.info("OOBM SimpleContract query start...");
//    	byte [] a = "return from server: ".getBytes();
//    	byte [] result = new byte[qstr.length+a.length];
//    	System.arraycopy(a, 0, result, 0, a.length);
//    	System.arraycopy(qstr, 0, result, a.length, qstr.length);
        return new ByteArrayInputStream(qstr);
    }
    
    @Override
    @POST
    @Path("/exec")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({"application/pdx-baap"})
    public TransactionResp exec(@Context BaapContext ctx, Transaction tx) {
    	logger.info("OOBM SimpleContract exec start...");
		String txId = ctx.txid();
		logger.info("txId: {}", txId);
		Map<String, InputStream> map = ctx.stream();
		logger.info("stream: {}", map);
		if (map != null) {
			map.entrySet().stream().forEach(t->{
				try {
					logger.info("key: {}, stream content: {}", t.getKey(), IOUtils.toString(t.getValue(), Charset.forName("utf-8")));
				} catch (IOException e) {
				}
			});
		}
		TransactionResp resp = new TransactionResp();
        resp.setTxId(ctx.txid());
        resp.setStatus("ok");
    	logger.info("OOBM SimpleContract exec end...");
		return resp;
	}
}
