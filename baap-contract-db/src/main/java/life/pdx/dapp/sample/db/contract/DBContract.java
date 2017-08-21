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

package life.pdx.dapp.sample.db.contract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import biz.pdxtech.daap.api.contract.ADapp;
import biz.pdxtech.daap.api.contract.IDapp;
import biz.pdxtech.daap.api.contract.Transaction;
import biz.pdxtech.daap.api.contract.TransactionResp;
import life.pdx.dapp.sample.db.dto.TxRespStatus;
import life.pdx.dapp.sample.db.entity.DBData;
import life.pdx.dapp.sample.db.entity.DBTransaction;
import life.pdx.dapp.sample.db.entity.DBTxMeta;
import life.pdx.dapp.sample.db.repository.DBDataRepository;
import life.pdx.dapp.sample.db.repository.DBTransactionRepository;
import life.pdx.dapp.sample.db.repository.DBTxMetaRepository;
import life.pdx.dapp.sample.db.util.DBUtil;
import life.pdx.dapp.sample.db.util.JacksonUtils;

@Path("/pdx.dapp/sample/db")
@Produces(MediaType.APPLICATION_JSON)
@Named
@Transactional
@ADapp
@Singleton
public class DBContract implements IDapp {

	private Logger log = LoggerFactory.getLogger(DBContract.class);

	@Autowired
	DBTransactionRepository dbTransactionRepository;

	@Autowired
	DBDataRepository dbDataRepository;
	@Autowired
	DBTxMetaRepository dbTxMetaRepository;

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
		if (tx == null) {
			return new ArrayList<Transaction>();
		}
		List<Transaction> trs = new ArrayList<Transaction>();
		try {
			HashMap<String, Object> queryMap = JacksonUtils.fromJson(new String(tx.getBody()), HashMap.class);
			String type = (String) queryMap.get("type");
			if (type.equals("1")) {
				String metaKey = (String) queryMap.get("metaKey");
				List<DBTxMeta> list = dbTxMetaRepository.findByMetaKey(metaKey);
				Transaction restx = new Transaction();
				restx.setBody(JacksonUtils.toJson(list).getBytes());
				trs.add(restx);
			} else if (type.equals("2")) {
				String txId = (String) queryMap.get("txId");
				DBTransaction dttx = dbTransactionRepository.findByTxId(txId);
				Transaction restx = new Transaction();
				restx.setBody(dttx.getBody());
				trs.add(restx);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return trs;
	}

	/**
	 * Execuate a TX.
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
		// 交易id
		List<String> txIds = headers.getRequestHeader("X-DaaP-TXID");
		// 公钥
		List<String> pubkeys = headers.getRequestHeader("X-DaaP-SPUK");

		String txId = "txid";
		String pubkey = "pubkey";
		if (txIds != null && txIds.size() > 0) {
			txId = txIds.get(0);
		}
		if (pubkeys != null && pubkeys.size() > 0) {
			pubkey = pubkeys.get(0);
		}
		log.info("receive tx, id: {}", txId);
		DBTransaction dbTx = new DBTransaction();
		dbTx.setBody(tx.getBody());
		dbTx.setTxId(txId);
		dbTx.setPublicKey(pubkey);
		dbTransactionRepository.save(dbTx);

		DBData dbTxdata = new DBData();
		dbTxdata.setTxId(txId);
		dbTxdata.setPublickey(pubkey);
		dbTxdata.setAddress(DBUtil.getAddrByPubKey(Hex.decode(pubkey)));
		dbDataRepository.save(dbTxdata);

		Map<String, byte[]> meta = tx.getMeta();
		for (Map.Entry<String, byte[]> entry : meta.entrySet()) {
			DBTxMeta dbmeta = new DBTxMeta();
			dbmeta.setTxId(txId);
			dbmeta.setMetaKey(entry.getKey());
			dbmeta.setMetaValue(entry.getValue());
			dbTxMetaRepository.save(dbmeta);

		}
		TransactionResp tre = new TransactionResp();
		tre.setDappId(DBUtil.getDappId(this.getClass()));
		tre.setTimestamp(System.currentTimeMillis());
		tre.setStatus(TxRespStatus.SUCCESS.getName());
		tre.setReason("transaction success!");
		return tre;
	}

	public List<String> saveNewTx(List<String> olds, List<DBTransaction> txs) {
		for (DBTransaction tx : txs) {
			if (!olds.contains(tx.getTxId())) {
				olds.add(tx.getTxId());
			}
		}
		return olds;
	}
}