/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.painless.spi;

import org.opensearch.painless.spi.annotation.WhitelistAnnotationParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/** Loads and creates a {@link Allowlist} from one to many text files. */
public final class AllowlistLoader {

    /**
     * Loads and creates a {@link Allowlist} from one to many text files using only the base annotation parsers.
     * See {@link #loadFromResourceFiles(Class, Map, String...)} for information on how to structure an allowlist
     * text file.
     */
    public static Allowlist loadFromResourceFiles(Class<?> resource, String... filepaths) {
        return WhitelistLoader.loadFromResourceFiles(resource, filepaths);
    }

    /**
     * Loads and creates a {@link Allowlist} from one to many text files. The file paths are passed in as an array of
     * {@link String}s with a single {@link Class} to be be used to load the resources where each {@link String}
     * is the path of a single text file. The {@link Class}'s {@link ClassLoader} will be used to lookup the Java
     * reflection objects for each individual {@link Class}, {@link Constructor}, {@link Method}, and {@link Field}
     * specified as part of the allowlist in the text file.
     *
     * A single pass is made through each file to collect all the information about each class, constructor, method,
     * and field. Most validation will be done at a later point after all allowlists have been gathered and their
     * merging takes place.
     *
     * A painless type name is one of the following:
     * <ul>
     *     <li> def - The Painless dynamic type which is automatically included without a need to be
     *     allowlisted. </li>
     *     <li> fully-qualified Java type name - Any allowlisted Java class will have the equivalent name as
     *     a Painless type name with the exception that any dollar symbols used as part of inner classes will
     *     be replaced with dot symbols. </li>
     *     <li> short Java type name - The text after the final dot symbol of any specified Java class. A
     *     short type Java name may be excluded by using the 'no_import' attribute during Painless class parsing
     *     as described later. </li>
     * </ul>
     *
     * The following can be parsed from each allowlist text file:
     * <ul>
     *   <li> Blank lines will be ignored by the parser. </li>
     *   <li> Comments may be created starting with a pound '#' symbol and end with a newline. These will
     *   be ignored by the parser. </li>
     *   <li> Primitive types may be specified starting with 'class' and followed by the Java type name,
     *   an opening bracket, a newline, a closing bracket, and a final newline. </li>
     *   <li> Complex types may be specified starting with 'class' and followed by the fully-qualified Java
     *   class name, optionally followed by a 'no_import' attribute, an opening bracket, a newline,
     *   constructor/method/field specifications, a closing bracket, and a final newline. Within a complex
     *   type the following may be parsed:
     *   <ul>
     *     <li> A constructor may be specified starting with an opening parenthesis, followed by a
     *     comma-delimited list of Painless type names corresponding to the type/class names for
     *     the equivalent Java parameter types (these must be allowlisted as well), a closing
     *     parenthesis, and a newline. </li>
     *     <li> A method may be specified starting with a Painless type name for the return type,
     *     followed by the Java name of the method (which will also be the Painless name for the
     *     method), an opening parenthesis, a comma-delimited list of Painless type names
     *     corresponding to the type/class names for the equivalent Java parameter types
     *     (these must be allowlisted as well), a closing parenthesis, and a newline. </li>
     *     <li> An augmented method may be specified starting with a Painless type name for the return
     *     type, followed by the fully qualified Java name of the class the augmented method is
     *     part of (this class does not need to be allowlisted), the Java name of the method
     *     (which will also be the Painless name for the method), an opening parenthesis, a
     *     comma-delimited list of Painless type names corresponding to the type/class names
     *     for the equivalent Java parameter types (these must be allowlisted as well), a closing
     *     parenthesis, and a newline. </li>
     *     <li>A field may be specified starting with a Painless type name for the equivalent Java type
     *     of the field, followed by the Java name of the field (which all be the Painless name
     *     for the field), and a newline. </li>
     *   </ul>
     *   <li> Annotations may be added starting with an at, followed by a name, optionally an opening brace,
     *   a parameter name, an equals, an opening quote, an argument value, a closing quote, (possibly repeated
     *   for multiple arguments,) and a closing brace. Multiple annotations may be added after a class (before
     *   the opening bracket), after a method, or after field. </li>
     * </ul>
     *
     * Note there must be a one-to-one correspondence of Painless type names to Java type/class names.
     * If the same Painless type is defined across multiple files and the Java class is the same, all
     * specified constructors, methods, and fields will be merged into a single Painless type. The
     * Painless dynamic type, 'def', used as part of constructor, method, and field definitions will
     * be appropriately parsed and handled. Painless complex types must be specified with the
     * fully-qualified Java class name. Method argument types, method return types, and field types
     * must be specified with Painless type names (def, fully-qualified, or short) as described earlier.
     *
     * The following example is used to create a single allowlist text file:
     *
     * {@code
     * # primitive types
     *
     * class int -> int {
     * }
     *
     * # complex types
     *
     * class my.package.Example @no_import {
     *   # constructors
     *   ()
     *   (int)
     *   (def, def)
     *   (Example, def)
     *
     *   # method
     *   Example add(int, def)
     *   int add(Example, Example)
     *   void example() @deprecated[use example 2 instead]
     *   void example2()
     *
     *   # augmented
     *   Example some.other.Class sub(Example, int, def)
     *
     *   # fields
     *   int value0
     *   int value1
     *   def value2
     * }
     * }
     */
    public static Allowlist loadFromResourceFiles(Class<?> resource, Map<String, WhitelistAnnotationParser> parsers, String... filepaths) {
        return WhitelistLoader.loadFromResourceFiles(resource, parsers, filepaths);
    }

    private AllowlistLoader() {}
}
