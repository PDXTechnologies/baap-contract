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

package life.pdx.dapp.sample.ledger.contract;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.pdxtech.daap.api.contract.ADapp;
import biz.pdxtech.daap.api.contract.Transaction;
import biz.pdxtech.daap.api.contract.TransactionResp;

@Path("/pdx.dapp/sample/ledger")
@Produces(MediaType.APPLICATION_JSON)
@ADapp
public class Contract {
    
    private Logger log = LoggerFactory.getLogger(Contract.class);
    
    @POST
    @Path("/query")
    @Consumes("application/pdx-daap")
    public List<Transaction> query(@Context HttpHeaders headers, Transaction tx) {
        log.info("enter SimpleContract::query");
        List<Transaction> trs = new ArrayList<Transaction>();
        Transaction tr = new Transaction();
        tr.setBody("test070703".getBytes());
        tr.setToken("token".getBytes());
        trs.add(tr);
        return trs;
    }
    
    @POST
    @Path("/apply")
    @Consumes("application/pdx-daap")
    public TransactionResp apply(@Context HttpHeaders headers, Transaction tx) {
        log.info("enter SimpleContract::applyTx");
        TransactionResp tre = new TransactionResp();
        
        tre.setReason("SimpleContract success");
        tre.setStatus("200");
        tre.setDappId(tx.getDst().toString());
        Map<String, byte[]> stateMap = new HashMap<>();
        stateMap.put("txId", headers.getHeaderString("X-DaaP-TXID").getBytes());
        tre.setState(stateMap);
        
        return tre;
    }
}
