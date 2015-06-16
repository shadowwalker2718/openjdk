/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.misc;

import java.io.IOException;
import java.lang.module.ModuleReference;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleReader;
import java.lang.reflect.Module;
import java.util.Set;


/**
 * A helper class to allow JDK classes (and internal tests) to easily create
 * modules, update modules, and update the readability graph.
 *
 * The parameters that are package names in this API are the fully-qualified
 * names of the packages as defined in section 6.5.3 of <cite>The Java&trade;
 * Language Specification </cite>, for example, {@code "java.lang"}.
 */

public class Modules {
    private Modules() { }

    /**
     * Define a new module of the given name to be associated with the
     * given class loader.
     */
    public static Module defineModule(ClassLoader loader, String name,
                                      Set<String> packages)
    {
        ModuleDescriptor descriptor =
            new ModuleDescriptor.Builder(name).conceals(packages).build();
        ModuleReference mref = new ModuleReference(descriptor, null) {
            @Override
            public ModuleReader open() throws IOException {
                throw new IOException("No reader for module " +
                                      descriptor().toNameAndVersion());
            }
        };
        return SharedSecrets.getJavaLangReflectAccess().defineModule(loader, mref);
    }

    /**
     * Adds a package to a module's content.
     *
     * This method is a no-op if the module already contains the package.
     */
    public static void addPackage(Module m, String pn) {
        SharedSecrets.getJavaLangReflectAccess().addPackage(m, pn);
    }

    /**
     * Adds a read-edge so that module {@code m1} reads module {@code m1}.
     * Same as m1.addReads(m2) but without a permission check.
     */
    public static void addReads(Module m1, Module m2) {
        SharedSecrets.getJavaLangReflectAccess().addReadsModule(m1, m2);
    }

    /**
     * Update a module to export a package.
     * Same as m1.addExports(pkg, m2) but with the awesome power to
     * add unqualified exports, all without a permission check.
     *
     * @throws IllegalArgumentException if pkg is not a package in m1
     */
    public static void addExports(Module m1, String pn, Module m2) {
        SharedSecrets.getJavaLangReflectAccess().addExports(m1, pn, m2);
    }
}
