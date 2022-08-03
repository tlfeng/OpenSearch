/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.painless.spi;

import org.opensearch.script.ScriptContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface PainlessExtension {

    /** @deprecated As of 2.2, because supporting inclusive language, replaced by {@link #getContextAllowlists()} */
    @Deprecated
    default Map<ScriptContext<?>, List<Whitelist>> getContextWhitelists() {
        throw new UnsupportedOperationException("Must be overridden");
    }

    // TODO: Remove the default implementation after removing the deprecated getContextWhitelists()
    default Map<ScriptContext<?>, List<Allowlist>> getContextAllowlists() {
        return getContextWhitelists().entrySet()
            .stream()
            .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().stream().map(e -> (Allowlist) e).collect(Collectors.toList())));
    }
}
