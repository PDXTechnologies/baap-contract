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

package life.pdx.bapp.sample.db.contract;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;

import biz.pdxtech.baap.api.contract.BaapContext;
import biz.pdxtech.baap.api.contract.IContract;
import biz.pdxtech.baap.api.contract.Transaction;
import biz.pdxtech.baap.api.contract.TransactionResp;
import life.pdx.bapp.sample.db.dto.TxRespStatus;
import life.pdx.bapp.sample.db.entity.DBData;
import life.pdx.bapp.sample.db.entity.DBTransaction;
import life.pdx.bapp.sample.db.entity.DBTxMeta;
import life.pdx.bapp.sample.db.repository.DBDataRepository;
import life.pdx.bapp.sample.db.repository.DBTransactionRepository;
import life.pdx.bapp.sample.db.repository.DBTxMetaRepository;
import life.pdx.bapp.sample.db.util.DBUtil;
import life.pdx.bapp.sample.db.util.JacksonUtils;

@Path("/pdx.bapp/sample/db")
public class DBContract implements IContract {

	private Logger log = LoggerFactory.getLogger(DBContract.class);

	@Autowired
	DBTransactionRepository dbTransactionRepository;

	@Autowired
	DBDataRepository dbDataRepository;
	@Autowired
	DBTxMetaRepository dbTxMetaRepository;

    @Override
    @POST
    @Path("/query")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    @Consumes({"application/pdx-baap"})
    public InputStream query(@Context BaapContext ctx, byte[] qstr) {
		log.info("DBContract query start...");
		HashMap<String, Object> queryMap = JacksonUtils.fromJson(new String(qstr), HashMap.class);
		String type = (String) queryMap.get("type");
		if (type.equals("1")) {
			String metaKey = (String) queryMap.get("metaKey");
			log.info("type: {}, metaKey: {}", type,  metaKey);
			List<DBTxMeta> list = dbTxMetaRepository.findByMetaKey(metaKey);
			log.info("list: {}, return: {}", list, JacksonUtils.toJson(list));
			return new ByteArrayInputStream(JacksonUtils.toJson(list).getBytes());
		} else if (type.equals("2")) {
			String txId = (String) queryMap.get("txId");
			log.info("type: {}, txId: {}", type,  txId);
			DBTransaction dttx = dbTransactionRepository.findByTxId(txId);
			log.info("dttx:{}, return: {}", dttx, JacksonUtils.toJson(dttx));
			return new ByteArrayInputStream(JacksonUtils.toJson(dttx).getBytes());
		} else {
			return new ByteArrayInputStream("".getBytes());
		}
    }

    @Override
    @POST
    @Path("/exec")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({"application/pdx-baap"})
    public TransactionResp exec(@Context BaapContext ctx, Transaction tx) {
		log.info("DBContract exec start...");
		String txId = ctx.txid();
		String pubkey = ctx.spuk();
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
		tre.setTxId(txId);
		tre.setTimestamp(System.currentTimeMillis());
		tre.setStatus(TxRespStatus.SUCCESS.getName());
		tre.setReason("transaction success!");
		log.info("return: {}", tre.getReason());
		log.info("DBContract exec end...");
		return tre;
    }

}