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

import java.util.List;
import java.util.Map;

/**
 * A class binding represents a method call that stores state. Each class binding's Java class must
 * have exactly one public constructor and one public method excluding those inherited directly
 * from {@link Object}. The canonical type name parameters provided must match those of the
 * constructor and method combined. The constructor for a class binding's Java class will be called
 * when the binding method is called for the first time at which point state may be stored for the
 * arguments passed into the constructor. The method for a binding class will be called each time
 * the binding method is called and may use the previously stored state.
 *
 * @deprecated As of 2.2, because supporting inclusive language, replaced by {@link WhitelistClassBinding}
 */
public class WhitelistClassBinding {

    final AllowlistClassBinding allowlistClassBinding;

    /** Information about where this constructor was allowlisted from. */
    public final String origin;

    /** The Java class name this class binding targets. */
    public final String targetJavaClassName;

    /** The method name for this class binding. */
    public final String methodName;

    /** The canonical type name for the return type. */
    public final String returnCanonicalTypeName;

    /**
     * A {@link List} of {@link String}s that are the Painless type names for the parameters of the
     * constructor which can be used to look up the Java constructor through reflection.
     */
    public final List<String> canonicalTypeNameParameters;

    /** The {@link Map} of annotations for this class binding. */
    public final Map<Class<?>, Object> painlessAnnotations;

    /** Standard constructor. All values must be not {@code null}. */
    public WhitelistClassBinding(
        String origin,
        String targetJavaClassName,
        String methodName,
        String returnCanonicalTypeName,
        List<String> canonicalTypeNameParameters,
        List<Object> painlessAnnotations
    ) {
        allowlistClassBinding = new AllowlistClassBinding(
            origin,
            targetJavaClassName,
            methodName,
            returnCanonicalTypeName,
            canonicalTypeNameParameters,
            painlessAnnotations
        );
        this.origin = allowlistClassBinding.origin;
        this.targetJavaClassName = allowlistClassBinding.targetJavaClassName;
        this.methodName = allowlistClassBinding.methodName;
        this.returnCanonicalTypeName = allowlistClassBinding.returnCanonicalTypeName;
        this.canonicalTypeNameParameters = allowlistClassBinding.canonicalTypeNameParameters;
        this.painlessAnnotations = allowlistClassBinding.painlessAnnotations;
    }
}
