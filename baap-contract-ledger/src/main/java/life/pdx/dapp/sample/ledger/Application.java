/*************************************************************************
 * Copyright (C) 2016 PDX Technologies, Inc. <jz@pdxtech.biz>
 *
 * This file is part of PDX DaaP Container project.
 *
 * DaaP Container is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * DaaP Container is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *************************************************************************/

package life.pdx.dapp.sample.ledger;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import biz.pdxtech.daap.api.contract.TransactionReader;

@ApplicationPath("rest")
public class Application extends ResourceConfig {
	public Application() {
		packages("life.pdx.dapp.sample.ledger.contract").
		register(MultiPartFeature.class).
		register(JacksonFeature.class).
		register(TransactionReader.class);
	}
}
