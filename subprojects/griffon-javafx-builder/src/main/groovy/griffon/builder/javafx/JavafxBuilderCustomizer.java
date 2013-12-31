/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package griffon.builder.javafx;

import griffon.builder.javafx.factory.ApplicationFactory;
import griffon.inject.DependsOn;
import groovy.util.Factory;
import groovyx.javafx.SceneGraphBuilder;
import org.codehaus.griffon.runtime.core.view.AbstractBuilderCustomizer;

import javax.inject.Named;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Andres Almiray
 */
@Named("javafx")
@DependsOn({"core"})
@SuppressWarnings("rawtypes")
public class JavafxBuilderCustomizer extends AbstractBuilderCustomizer {
    @SuppressWarnings("unchecked")
    public JavafxBuilderCustomizer() {
        SceneGraphBuilder builder = new SceneGraphBuilder();

        Map<String, Factory> factories = new LinkedHashMap<>(builder.getFactories());
        factories.put("application", new ApplicationFactory());
        setFactories(factories);
        setVariables(builder.getVariables());
        setMethods(builder.getExplicitMethods());
        setProps(builder.getExplicitProperties());
        setAttributeDelegates(builder.getAttributeDelegates());
        setPreInstantiateDelegates(builder.getPreInstantiateDelegates());
        setPostInstantiateDelegates(builder.getPostInstantiateDelegates());
        setPostNodeCompletionDelegates(builder.getPostNodeCompletionDelegates());
        // setDisposalClosures(builder.getDisposalClosures());
        setMethodMissingDelegate(builder.getMethodMissingDelegate());
        setPropertyMissingDelegate(builder.getPropertyMissingDelegate());
    }
}
