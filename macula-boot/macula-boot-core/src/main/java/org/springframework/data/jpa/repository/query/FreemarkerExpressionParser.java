/*
 *  Copyright (c) 2010-2019   the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.springframework.data.jpa.repository.query;

import com.mongodb.lang.Nullable;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateAwareExpressionParser;

/**
 * <p>
 * <b>FreemarkerExpressionParser</b> Freemaker表达式解析器
 * </p>
 *
 * @author Rain
 * @since 2019-01-27
 */
public class FreemarkerExpressionParser extends TemplateAwareExpressionParser {
    @Override
    protected Expression doParseExpression(String s, @Nullable ParserContext parserContext) throws ParseException {
        return null;
    }
}
