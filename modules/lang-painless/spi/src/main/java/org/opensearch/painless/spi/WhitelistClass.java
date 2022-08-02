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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class represents the equivalent of a Java class in Painless complete with super classes,
 * constructors, methods, and fields. There must be a one-to-one mapping of class names to Java
 * classes. Though, since multiple allowlists may be combined into a single allowlist for a
 * specific context, as long as multiple classes representing the same Java class have the same
 * class name and have legal constructor/method overloading they can be merged together.
 *
 * Classes in Painless allow for arity overloading for constructors and methods. Arity overloading
 * means that multiple constructors are allowed for a single class as long as they have a different
 * number of parameters, and multiples methods with the same name are allowed for a single class
 * as long as they have the same return type and a different number of parameters.
 *
 * Classes will automatically extend other allowlisted classes if the Java class they represent is a
 * subclass of other classes including Java interfaces.
 *
 * @deprecated As of 2.2, because supporting inclusive language, replaced by {@link AllowlistClass}
 */
@Deprecated
public final class WhitelistClass {

    final AllowlistClass allowlistClass;

    /** Information about where this class was allow-listed from. */
    public final String origin;

    /** The Java class name this class represents. */
    public final String javaClassName;

    /** The {@link List} of allowlisted ({@link AllowlistConstructor}s) available to this class. */
    public final List<WhitelistConstructor> whitelistConstructors;

    /** The {@link List} of allowlisted ({@link AllowlistMethod}s) available to this class. */
    public final List<WhitelistMethod> whitelistMethods;

    /** The {@link List} of allowlisted ({@link AllowlistField}s) available to this class. */
    public final List<WhitelistField> whitelistFields;

    /** The {@link Map} of annotations for this class. */
    public final Map<Class<?>, Object> painlessAnnotations;

    /** Standard constructor. All values must be not {@code null}. */
    public WhitelistClass(
        String origin,
        String javaClassName,
        List<WhitelistConstructor> whitelistConstructors,
        List<WhitelistMethod> whitelistMethods,
        List<WhitelistField> whitelistFields,
        List<Object> painlessAnnotations
    ) {
        // Construct new lists of type Allowlist from Whitelist
        List<AllowlistConstructor> allowlistConstructors = whitelistConstructors.stream()
            .map(e -> e.allowlistConstructor)
            .collect(Collectors.toList());
        List<AllowlistMethod> allowlistMethods = whitelistMethods.stream().map(e -> e.allowlistMethod).collect(Collectors.toList());
        List<AllowlistField> allowlistFields = whitelistFields.stream().map(e -> e.allowlistField).collect(Collectors.toList());

        allowlistClass = new AllowlistClass(
            origin,
            javaClassName,
            allowlistConstructors,
            allowlistMethods,
            allowlistFields,
            painlessAnnotations
        );
        this.origin = allowlistClass.origin;
        this.javaClassName = allowlistClass.javaClassName;
        this.whitelistConstructors = Collections.unmodifiableList(Objects.requireNonNull(whitelistConstructors));
        this.whitelistMethods = Collections.unmodifiableList(Objects.requireNonNull(whitelistMethods));
        this.whitelistFields = Collections.unmodifiableList(Objects.requireNonNull(whitelistFields));
        this.painlessAnnotations = allowlistClass.painlessAnnotations;
    }
}
