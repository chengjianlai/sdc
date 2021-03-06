package org.openecomp.sdc.validation.impl.validators.heatresource;

import org.apache.commons.collections4.MapUtils;
import org.openecomp.core.validation.ErrorMessageCode;
import org.openecomp.core.validation.errors.ErrorMessagesFormatBuilder;
import org.openecomp.core.validation.types.GlobalValidationContext;
import org.openecomp.sdc.common.errors.Messages;
import org.openecomp.sdc.datatypes.error.ErrorLevel;
import org.openecomp.sdc.heat.datatypes.model.HeatResourcesTypes;
import org.openecomp.sdc.heat.datatypes.model.Resource;
import org.openecomp.sdc.validation.ResourceValidator;
import org.openecomp.sdc.validation.ValidationContext;
import org.openecomp.sdc.validation.type.HeatResourceValidationContext;
import org.openecomp.sdc.validation.type.ValidatorConstants;

import java.util.List;
import java.util.Map;

/**
 * Created by TALIO on 2/28/2017.
 */
public class ContrailNetworkPolicyResourceValidator implements ResourceValidator {
  private static final ErrorMessageCode ERROR_CODE_HNP1 = new ErrorMessageCode("HNP1");
  private static final ErrorMessageCode ERROR_CODE_HNP2 = new ErrorMessageCode("HNP2");

  @Override
  public void validate(String fileName, Map.Entry<String, Resource> resourceEntry,
                       GlobalValidationContext globalContext, ValidationContext validationContext) {
    validateNetworkPolicyIsUsed(fileName, resourceEntry, globalContext,
            (HeatResourceValidationContext) validationContext);

  }

  private static void validateNetworkPolicyIsUsed(String fileName,
                                                  Map.Entry<String, Resource> resourceEntry,
                                                  GlobalValidationContext globalContext,
                                                  HeatResourceValidationContext validationContext) {
    Map<String, Map<String, List<String>>> referencedNetworkAttachPoliciesResources =
            validationContext.getFileLevelResourceDependencies()
                    .get(HeatResourcesTypes.CONTRAIL_NETWORK_RULE_RESOURCE_TYPE.getHeatResource());

    if (MapUtils.isEmpty(referencedNetworkAttachPoliciesResources)) {
      globalContext
              .addMessage(
                      fileName,
                      ErrorLevel.WARNING,
                      ErrorMessagesFormatBuilder
                              .getErrorWithParameters(ERROR_CODE_HNP1,
                                      Messages.RESOURCE_NOT_IN_USE.getErrorMessage(),
                                      ValidatorConstants.Network_Policy, resourceEntry.getKey()));
      return;
    }

    handleNetworkAttachPolicyReferences(fileName, resourceEntry,
            referencedNetworkAttachPoliciesResources, globalContext);
  }

  private static void handleNetworkAttachPolicyReferences(String fileName,
                                                          Map.Entry<String, Resource> resourceEntry,
                                                          Map<String, Map<String, List<String>>> pointedNetworkAttachPolicies,
                                                          GlobalValidationContext globalContext) {

    Map<String, List<String>> resourcesPointingToCurrNetworkAttachPolicy =
            pointedNetworkAttachPolicies.get(resourceEntry.getKey());
    if (isNetworkAttachPolicyNotInUse(resourcesPointingToCurrNetworkAttachPolicy)) {
      globalContext
              .addMessage(
                      fileName,
                      ErrorLevel.WARNING,
                      ErrorMessagesFormatBuilder
                              .getErrorWithParameters(ERROR_CODE_HNP2,
                                      Messages.RESOURCE_NOT_IN_USE.getErrorMessage(),
                                      ValidatorConstants.Network_Policy, resourceEntry.getKey()));
    }
  }

  private static boolean isNetworkAttachPolicyNotInUse(
          Map<String, List<String>> resourcesPointingToCurrNetworkAttachPolicy) {
    return MapUtils.isEmpty(resourcesPointingToCurrNetworkAttachPolicy);
  }

}
