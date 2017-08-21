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

package life.pdx.daap.sample.state.contract;

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
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.pdxtech.daap.api.contract.ADapp;
import biz.pdxtech.daap.api.contract.Transaction;
import biz.pdxtech.daap.api.contract.TransactionResp;
import biz.pdxtech.daap.blockchainctx.BlockChainCtx;


@Path("/pdx.dapp/sample/state")
@Produces(MediaType.APPLICATION_JSON)
@ADapp
public class StateContract {

	private Logger log = LoggerFactory.getLogger(StateContract.class);
	private static final String STATE_NAME = "laststate";
	/**
	 * Query about a TX or option. Query string in Transaction.body using JUEL.
	 *
	 * @param query
	 *            Criteria in body using JUEL, possibly encrypted and then
	 *            base64-encoded
	 * @return
	 */
	@POST
	@Path("/query")
	@Consumes("application/pdx-daap")
	public List<Transaction> query(@Context HttpHeaders headers, Transaction tx) {
		List<Transaction> trs = new ArrayList<Transaction>();
		log.info("enter StateContract::query");
		Map<String, byte[]> meta = tx.getMeta();
		String txid = new String(meta.get("X-DaaP-TXID"));
		BlockChainCtx ctx = BlockChainCtx.getInstance(null);
		byte[] state = ctx.getState(STATE_NAME, txid);
		Transaction  tr = new Transaction();
		tr.setBody((STATE_NAME+":"+new String(state)).getBytes());
		trs.add(tr);
		return trs;
	}

	/**
	 * Execute a TX.
	 * 
	 * See also {@link TransactionResult}
	 * 
	 * @param data
	 * @return
	 */
	@POST
	@Path("/apply")
	@Consumes("application/pdx-daap")
	public TransactionResp apply(@Context HttpHeaders headers, Transaction tx) {
		log.info("enter StateContract::applyTx");
		MultivaluedMap meta = headers.getRequestHeaders();
		BlockChainCtx ctx = BlockChainCtx.getInstance(meta);
		log.info("chainid: {}, txId: {}", ctx.getCurrentChainId(), ctx.getCurrenTxId());
		
		/*
		 * get state  by stateKey
		 * 
		 */
		byte[] state = ctx.getState(STATE_NAME);
		if (state != null && state.length > 0) {
			log.info("state: name: {}, value: {}", STATE_NAME, new String(state));
		}
		else {
			log.info("no state: name: {}", STATE_NAME);
		}
		
		/*
		 * get transaction
		 */
		Transaction getTx = ctx.getTransaction(ctx.getCurrenTxId());
		
		
		TransactionResp tre = new TransactionResp();
		tre.setReason("State SimpleContract success");
		tre.setStatus("200");
		
		/*
		 * put state 
		 */
		 Map<String,byte[]> newstate = new HashMap<String,byte[]>();
		 newstate.put(STATE_NAME, ctx.getCurrenTxId().getBytes());
		 tre.setState(newstate);
		 
		return tre;
	}
}
