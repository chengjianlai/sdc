/*
 * Copyright © 2016-2017 European Support Limited
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

package org.openecomp.sdc.logging.slf4j;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openecomp.sdc.logging.api.Logger;
import org.openecomp.sdc.logging.api.LoggerFactory;

/**
 * Make sure the logger factory returns correct loggers.
 *
 * @author EVITALIY
 * @since 08 Jan 18
 */
public class LoggerFactoryTest {

    private static final String CLASS_NAME = LoggerFactoryTest.class.getName();

    @Test
    public void returnSlf4jLoggerWhenGetByClass() {
        Logger logger = LoggerFactory.getLogger(LoggerFactoryTest.class);
        assertEquals(SLF4JLoggerWrapper.class, logger.getClass());
        assertEquals(CLASS_NAME, logger.getName());
    }

    @Test
    public void returnSlf4jLoggerWhenGetByName() {
        Logger logger = LoggerFactory.getLogger(CLASS_NAME);
        assertEquals(SLF4JLoggerWrapper.class, logger.getClass());
        assertEquals(CLASS_NAME, logger.getName());
    }

    @Test(expected = NullPointerException.class)
    public void throwNpeWhenGetByNameWithNull() {
        LoggerFactory.getLogger((String) null);
    }

    @Test(expected = NullPointerException.class)
    public void throwNpeWhenGetByClassWithNull() {
        LoggerFactory.getLogger((Class<LoggerFactoryTest>) null);
    }
}
