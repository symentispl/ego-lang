/**
 * Copyright © 2020 Segfault (wiktor@segfault.events,jarek@segfault.events)
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
package segfault.ego.sourcegen;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import javax.lang.model.element.Modifier;
import segfault.ego.parser.AtomLiteral;
import segfault.ego.parser.EgoAlg;
import segfault.ego.parser.Expr;
import segfault.ego.parser.ListLiteral;
import segfault.ego.parser.NumberLiteral;
import segfault.ego.parser.StringLiteral;

public class SourceGenEgoAlgo implements EgoAlg<String, String, String, String> {
    @Override
    public String listLiteral(ListLiteral literal) {
        var list = literal.exprs().stream()
                .map(expr -> switch (expr) {
                    case AtomLiteral a -> atomLiteral(a);
                    default -> throw new IllegalStateException("Unexpected value: " + expr);
                })
                .collect(joining(","));
        return "List.of(" + list + ")";
    }

    @Override
    public String atomLiteral(AtomLiteral a) {
        return format("""
                atom("%s")""", a.atom());
    }

    @Override
    public String numberLiteral(NumberLiteral n) {
        return Long.toString(n.number());
    }

    @Override
    public String stringLiteral(StringLiteral s) {
        return null;
    }

    Object sourceGen(ListLiteral literal) {
        var javaClass = TypeSpec.classBuilder("main")
                .addMethod(MethodSpec.methodBuilder("main")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(
                                ParameterSpec.builder(String[].class, "args").build())
                        .returns(Void.TYPE)
                        .addCode(CodeBlock.builder().add(listLiteral(literal)).build())
                        .build())
                .build();

        var javaFile = JavaFile.builder("segfault.example", javaClass).addStaticImport(Expr.class, "atom");
        try {
            javaFile.build().writeTo(System.out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return javaFile;
    }
}
