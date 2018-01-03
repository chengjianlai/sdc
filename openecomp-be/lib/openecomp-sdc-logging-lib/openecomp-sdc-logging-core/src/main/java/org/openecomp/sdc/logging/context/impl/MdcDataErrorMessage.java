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

package org.openecomp.sdc.logging.context.impl;

import org.openecomp.sdc.logging.types.LoggerConstants;
import org.slf4j.MDC;


public class MdcDataErrorMessage {

  private final String targetEntity;
  private final String targetServiceName;
  private final String errorDescription;
  private final String level;
  private final String errorCode;

  /**
   * Instantiates a new Mdc data error message.
   *
   * @param targetEntity      the target entity
   * @param targetServiceName the target service name
   * @param errorCategory     the error category
   * @param errorCode         the error code
   * @param errorDescription  the error description
   */
  public MdcDataErrorMessage(String targetEntity, String targetServiceName, String errorCategory,
                             String errorCode, String errorDescription) {
    this.level = errorCategory;
    this.errorCode = errorCode;
    this.targetEntity = targetEntity;
    this.targetServiceName = targetServiceName;
    this.errorDescription = errorDescription;

    this.setMdcValues();
  }

  /**
   * Create error message and update mdc.
   *
   * @param targetEntity      the target entity
   * @param targetServiceName the target service name
   * @param level             the level
   * @param errorCode         the error code
   * @param errorDescription  the error description
   */
  public static void createErrorMessageAndUpdateMdc(String targetEntity, String targetServiceName,
                                                    String level, String errorCode,
                                                    String errorDescription) {
    // don't remove this line although it seems useless
    new MdcDataErrorMessage(targetEntity, targetServiceName, level, errorCode, errorDescription);
  }

  public void setMdcValues() {
    MDC.put(LoggerConstants.ERROR_CATEGORY, this.level);
    MDC.put(LoggerConstants.ERROR_CODE, this.errorCode);
    MDC.put(LoggerConstants.TARGET_ENTITY, this.targetEntity);
    MDC.put(LoggerConstants.TARGET_SERVICE_NAME, this.targetServiceName);
    MDC.put(LoggerConstants.ERROR_DESCRIPTION, this.errorDescription);
  }
}