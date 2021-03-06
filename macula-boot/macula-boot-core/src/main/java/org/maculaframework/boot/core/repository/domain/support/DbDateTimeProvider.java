/*
 * Copyright 2004-2019 the original author or authors.
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
package org.maculaframework.boot.core.repository.domain.support;

import org.maculaframework.boot.core.utils.EnvironmentUtils;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.temporal.TemporalAccessor;
import java.util.Optional;

/**
 * <p> <b>DbDateTimeProvider</b> 从数据库获取当前时间 </p>
 *
 * @since 2012-8-28
 * @author zhengping_wang
 * @version $Id: DbDateTimeProvider.java 5252 2014-07-09 09:36:50Z wilson $
 */
public class DbDateTimeProvider implements DateTimeProvider {

    @Override
    public Optional<TemporalAccessor> getNow() {
        return Optional.of(EnvironmentUtils.getCurrentTime());
    }

}
