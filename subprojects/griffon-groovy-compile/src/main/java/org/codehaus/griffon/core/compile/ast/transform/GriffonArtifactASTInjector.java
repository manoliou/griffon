/*
 * Copyright 2008-2014 the original author or authors.
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
package org.codehaus.griffon.core.compile.ast.transform;

import griffon.core.GriffonApplication;
import griffon.core.artifact.GriffonClass;
import org.codehaus.griffon.core.compile.BaseConstants;
import org.codehaus.groovy.ast.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PUBLIC;
import static org.codehaus.griffon.core.compile.ast.GriffonASTUtils.*;

/**
 * @author Andres Almiray
 * @since 2.0.0
 */
public class GriffonArtifactASTInjector extends AbstractASTInjector implements BaseConstants {
    private static final ClassNode GRIFFON_APPLICATION_TYPE = makeClassSafe(GriffonApplication.class);
    private static final ClassNode GRIFFON_CLASS_TYPE = makeClassSafe(GriffonClass.class);
    private static final ClassNode LOGGER_TYPE = makeClassSafe(Logger.class);
    private static final ClassNode LOGGER_FACTORY_TYPE = makeClassSafe(LoggerFactory.class);
    private static final ClassNode INJECT_TYPE = makeClassSafe(Inject.class);

    public void inject(@Nonnull ClassNode classNode, @Nonnull String artifactType) {
        FieldNode applicationField = injectField(classNode, APPLICATION, PRIVATE, GRIFFON_APPLICATION_TYPE, null, false);

        ConstructorNode constructor = classNode.addConstructor(
            PUBLIC,
            params(param(GRIFFON_APPLICATION_TYPE, APPLICATION)),
            NO_EXCEPTIONS,
            assigns(field(applicationField), var(APPLICATION))
        );
        constructor.addAnnotation(new AnnotationNode(INJECT_TYPE));

        // GriffonClass getGriffonClass()
        injectMethod(classNode, new MethodNode(
            "getGriffonClass",
            PUBLIC,
            GRIFFON_CLASS_TYPE,
            NO_PARAMS,
            NO_EXCEPTIONS,
            returns(call(
                applicationProperty(classNode, "artifactManager"),
                "findGriffonClass",
                args(call(THIS, "getClass", NO_ARGS))))
        ));
        String loggerCategory = "griffon.app." + artifactType + "." + classNode.getName();
        FieldNode loggerField = injectField(classNode,
            "this$logger",
            FINAL | PRIVATE,
            LOGGER_TYPE,
            call(
                LOGGER_FACTORY_TYPE,
                "getLogger",
                args(constx(loggerCategory)))
        );

        // Logger getLog()
        injectMethod(classNode, new MethodNode(
            "getLog",
            PUBLIC,
            LOGGER_TYPE,
            NO_PARAMS,
            NO_EXCEPTIONS,
            returns(field(loggerField))
        ));

        ThreadingAwareASTTransformation.apply(classNode);
        MVCAwareASTTransformation.apply(classNode);
        ResourcesAwareASTTransformation.apply(classNode);
    }
}
