/*
 * ToroDB
 * Copyright © 2014 8Kdata Technology (www.8kdata.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.torodb.backend.postgresql.converters.jooq;

import com.torodb.backend.converters.jooq.DataTypeForKv;
import com.torodb.backend.converters.jooq.KvValueConverter;
import com.torodb.backend.converters.sql.SqlBinding;
import com.torodb.backend.postgresql.converters.sql.MongoTimestampRecordSqlBinding;
import com.torodb.backend.udt.MongoTimestampUDT;
import com.torodb.backend.udt.record.MongoTimestampRecord;
import com.torodb.kvdocument.types.KvType;
import com.torodb.kvdocument.types.MongoTimestampType;
import com.torodb.kvdocument.values.KvMongoTimestamp;
import com.torodb.kvdocument.values.heap.DefaultKvMongoTimestamp;

/**
 *
 */
public class MongoTimestampValueConverter implements
    KvValueConverter<MongoTimestampRecord, MongoTimestampRecord, KvMongoTimestamp> {

  private static final long serialVersionUID = 1251948867583783920L;

  public static final DataTypeForKv<KvMongoTimestamp> TYPE = DataTypeForKv.from(
      MongoTimestampUDT.MONGO_TIMESTAMP.getDataType(), new MongoTimestampValueConverter());

  @Override
  public KvType getErasuredType() {
    return MongoTimestampType.INSTANCE;
  }

  @Override
  public KvMongoTimestamp from(MongoTimestampRecord databaseObject) {
    return new DefaultKvMongoTimestamp(databaseObject.getSecs(), databaseObject.getCounter());
  }

  @Override
  public MongoTimestampRecord to(KvMongoTimestamp userObject) {
    return new MongoTimestampRecord(userObject.getSecondsSinceEpoch(), userObject.getOrdinal());
  }

  @Override
  public Class<MongoTimestampRecord> fromType() {
    return MongoTimestampRecord.class;
  }

  @Override
  public Class<KvMongoTimestamp> toType() {
    return KvMongoTimestamp.class;
  }

  @Override
  public SqlBinding<MongoTimestampRecord> getSqlBinding() {
    return MongoTimestampRecordSqlBinding.INSTANCE;
  }
}
