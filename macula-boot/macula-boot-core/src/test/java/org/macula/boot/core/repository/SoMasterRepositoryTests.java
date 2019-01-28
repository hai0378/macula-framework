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
package org.macula.boot.core.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.macula.boot.core.cache.support.config.CacheConfig;
import org.macula.boot.core.repository.support.SoMasterRepository;
import org.macula.boot.core.repository.support.domain.SoDetail;
import org.macula.boot.core.repository.support.domain.SoMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * <p> <b>SoMasterRepositoryTests</b> 非主键类型的外键关联，如果没有孩子，Hibernate4.3会出问题 </p>
 * HHH-11537这个KEY说明了这个问题，但是一直没有解决
 * 后来发现BatchingEntityLoaderBuilder中，默认修改了EntityLoader为Plan，通过设置hibernate.batch_fetch_style为PADDED，则不适用Plan包里面的EntityLoader就可以解决这个问题
 *
 * @author Rain
 * @author Wilson Luo
 * @version $Id: UserRepositoryTests.java 5354 2014-09-01 03:21:07Z wzp $
 * @since 2010-12-31
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {CacheConfig.class})
public class SoMasterRepositoryTests {

    @Autowired
    private SoMasterRepository soMasterRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testFindById() {

        org.macula.boot.ApplicationContext.setContainer(applicationContext);

        List<SoDetail> sds = new ArrayList<SoDetail>();
        SoMaster so = new SoMaster();
        so.setSoNo("ABC");
        so.setSoName("哈哈");
        so.setSoDetails(sds);

        SoDetail sd = new SoDetail();
        sd.setSoDetailName("孩子啊");
        sd.setLineNo("1");
        sd.setSoMaster(so);

        sds.add(sd);

        so = soMasterRepository.save(so);

        Optional<SoMaster> soNew = soMasterRepository.findById(so.getId());
        Assert.assertEquals(so.getSoNo(), soNew.get().getSoNo());

    }
}
