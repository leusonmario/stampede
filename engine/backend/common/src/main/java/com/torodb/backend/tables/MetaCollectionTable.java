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

package com.torodb.backend.tables;

import com.torodb.backend.meta.TorodbSchema;
import com.torodb.backend.tables.records.MetaCollectionRecord;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;

import java.util.Arrays;
import java.util.List;

@SuppressFBWarnings(
    value = "HE_HASHCODE_NO_EQUALS",
    justification =
    "Equals comparation is done in TableImpl class, which compares schema, name and fields")
@SuppressWarnings({"checkstyle:LineLength", "checkstyle:AbbreviationAsWordInName",
    "checkstyle:MemberName"})
public abstract class MetaCollectionTable<R extends MetaCollectionRecord> extends SemanticTable<R> {

  private static final long serialVersionUID = 740755688;

  public static final String TABLE_NAME = "collection";

  public enum TableFields {
    DATABASE("database"),
    NAME("name"),
    IDENTIFIER("identifier"),;

    public final String fieldName;

    TableFields(String fieldName) {
      this.fieldName = fieldName;
    }

    @Override
    public String toString() {
      return fieldName;
    }
  }

  /**
   * The class holding records for this type
   *
   * @return
   */
  @Override
  public abstract Class<R> getRecordType();

  /**
   * The column <code>torodb.collection.database</code>.
   */
  public final TableField<R, String> DATABASE =
      createDatabaseField();

  /**
   * The column <code>torodb.collection.name</code>.
   */
  public final TableField<R, String> NAME =
      createNameField();

  /**
   * The column <code>torodb.collection.identifier</code>.
   */
  public final TableField<R, String> IDENTIFIER =
      createIdentifierField();

  protected abstract TableField<R, String> createDatabaseField();

  protected abstract TableField<R, String> createNameField();

  protected abstract TableField<R, String> createIdentifierField();

  private final UniqueKeys<R> uniqueKeys;

  /**
   * Create a <code>torodb.collection</code> table reference
   */
  public MetaCollectionTable() {
    this(TABLE_NAME, null);
  }

  protected MetaCollectionTable(String alias, Table<R> aliased) {
    this(alias, aliased, null);
  }

  protected MetaCollectionTable(String alias, Table<R> aliased, Field<?>[] parameters) {
    super(alias, TorodbSchema.TORODB, aliased, parameters, "");

    this.uniqueKeys = new UniqueKeys<R>(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UniqueKey<R> getPrimaryKey() {
    return uniqueKeys.COLLECTION_PKEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<UniqueKey<R>> getKeys() {
    return Arrays.<UniqueKey<R>>asList(uniqueKeys.COLLECTION_PKEY);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MetaCollectionTable<R> as(String alias);

  /**
   * Rename this table
   */
  public abstract MetaCollectionTable<R> rename(String name);

  public UniqueKeys<R> getUniqueKeys() {
    return uniqueKeys;
  }

  @SuppressWarnings({"checkstyle:AbbreviationAsWordInName", "checkstyle:MemberName"})
  public static class UniqueKeys<KeyRecordT extends MetaCollectionRecord> extends AbstractKeys {

    private final UniqueKey<KeyRecordT> COLLECTION_PKEY;

    private UniqueKeys(MetaCollectionTable<KeyRecordT> collectionsTable) {
      COLLECTION_PKEY = createUniqueKey(collectionsTable, collectionsTable.DATABASE,
          collectionsTable.NAME);
    }
  }
}
