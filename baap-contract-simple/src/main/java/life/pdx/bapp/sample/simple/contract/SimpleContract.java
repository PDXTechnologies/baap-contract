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

package life.pdx.bapp.sample.simple.contract;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.pdxtech.baap.api.contract.BaapContext;
import biz.pdxtech.baap.api.contract.IContract;
import biz.pdxtech.baap.api.contract.Transaction;
import biz.pdxtech.baap.api.contract.TransactionResp;

@Path("/pdx.dapp/sample/simple")
public class SimpleContract implements IContract {
    
    private Logger log = LoggerFactory.getLogger(SimpleContract.class);
    
    @Override
    @POST
    @Path("/query")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    @Consumes({"application/pdx-baap"})
    public InputStream query(@Context BaapContext ctx, byte[] qstr) {
		log.info("SimpleContract query start...");
        return new ByteArrayInputStream(qstr);
    }
    
    @Override
    @POST
    @Path("/exec")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({"application/pdx-baap"})
    public TransactionResp exec(@Context BaapContext ctx, Transaction tx) {
		log.info("SimpleContract exec start...");
        log.info("txid: {}", ctx.txid());
        TransactionResp resp = new TransactionResp();
        resp.setTxId(ctx.txid());
        
        resp.putState("a", "a".getBytes()).putState("b", "b".getBytes()).putState("c", "c".getBytes());
        
//        resp.addNotif(TransactionResp.Notif.of("a", new byte[1], ctx.txid()))
//                .addNotif(TransactionResp.Notif.of("b", new byte[1], ctx.txid()));
//        
//        resp.getNotify().forEach(a -> {
//            System.out.println(a.getPbk());
//            System.out.println(new String(a.getData()));
//            System.out.println(a.getTxid());
//        });
        
        if (ctx.stream() != null) {
            ctx.stream().forEach((k, v) -> {
                try {
                	log.info("key: {}, value: {}" , k, IOUtils.toString(v, "utf-8"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
		log.info("SimpleContract exec end...");
        return resp;
    }
    
    
}
