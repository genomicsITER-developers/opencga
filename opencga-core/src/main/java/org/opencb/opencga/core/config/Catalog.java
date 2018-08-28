/*
 * Copyright 2015-2017 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.opencga.core.config;

/**
 * Created by pfurio on 01/02/17.
 */
public class Catalog {

    @Deprecated
    private long offset;
    private CatalogDBCredentials database;
    private SearchConfiguration search;

    public Catalog() {
    }

    public Catalog(CatalogDBCredentials database, SearchConfiguration search) {
        this.database = database;
        this.search = search;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Catalog{");
        sb.append("offset=").append(offset);
        sb.append(", database=").append(database);
        sb.append(", search=").append(search);
        sb.append('}');
        return sb.toString();
    }

    @Deprecated
    public long getOffset() {
        return offset;
    }

    @Deprecated
    public Catalog setOffset(long offset) {
        this.offset = offset;
        return this;
    }

    public CatalogDBCredentials getDatabase() {
        return database;
    }

    public Catalog setDatabase(CatalogDBCredentials database) {
        this.database = database;
        return this;
    }

    public SearchConfiguration getSearch() {
        return search;
    }

    public Catalog setSearch(SearchConfiguration search) {
        this.search = search;
        return this;
    }
}
