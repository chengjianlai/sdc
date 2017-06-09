/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
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
 * ============LICENSE_END=========================================================
 */

package org.openecomp.sdc.vendorsoftwareproduct.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.openecomp.core.model.dao.ServiceModelDao;
import org.openecomp.core.model.types.ServiceElement;
import org.openecomp.core.utilities.file.FileContentHandler;
import org.openecomp.core.utilities.file.FileUtils;
import org.openecomp.core.utilities.json.JsonUtil;
import org.openecomp.sdc.activityLog.ActivityLogManager;
import org.openecomp.sdc.activitylog.dao.type.ActivityLogEntity;
import org.openecomp.sdc.common.errors.CoreException;
import org.openecomp.sdc.common.errors.Messages;
import org.openecomp.sdc.common.utils.CommonUtil;
import org.openecomp.sdc.common.utils.SdcCommon;
import org.openecomp.sdc.datatypes.error.ErrorLevel;
import org.openecomp.sdc.datatypes.error.ErrorMessage;
import org.openecomp.sdc.healing.api.HealingManager;
import org.openecomp.sdc.healing.types.HealCode;
import org.openecomp.sdc.heat.datatypes.structure.HeatStructureTree;
import org.openecomp.sdc.heat.datatypes.structure.ValidationStructureList;
import org.openecomp.sdc.heat.services.tree.HeatTreeManager;
import org.openecomp.sdc.heat.services.tree.HeatTreeManagerUtil;
import org.openecomp.sdc.logging.api.Logger;
import org.openecomp.sdc.logging.api.LoggerFactory;
import org.openecomp.sdc.logging.api.annotations.Metrics;
import org.openecomp.sdc.logging.context.impl.MdcDataDebugMessage;
import org.openecomp.sdc.logging.types.LoggerServiceName;
import org.openecomp.sdc.logging.types.LoggerTragetServiceName;
import org.openecomp.sdc.tosca.datatypes.ToscaServiceModel;
import org.openecomp.sdc.translator.services.heattotosca.HeatToToscaUtil;
import org.openecomp.sdc.validation.util.ValidationManagerUtil;
import org.openecomp.sdc.vendorsoftwareproduct.OrchestrationTemplateCandidateManager;
import org.openecomp.sdc.vendorsoftwareproduct.dao.OrchestrationTemplateDao;
import org.openecomp.sdc.vendorsoftwareproduct.dao.VendorSoftwareProductDao;
import org.openecomp.sdc.vendorsoftwareproduct.dao.VendorSoftwareProductInfoDao;
import org.openecomp.sdc.vendorsoftwareproduct.dao.type.OrchestrationTemplateCandidateData;
import org.openecomp.sdc.vendorsoftwareproduct.dao.type.UploadData;
import org.openecomp.sdc.vendorsoftwareproduct.dao.type.VspDetails;
import org.openecomp.sdc.vendorsoftwareproduct.errors.OrchestrationTemplateNotFoundErrorBuilder;
import org.openecomp.sdc.vendorsoftwareproduct.services.composition.CompositionDataExtractor;
import org.openecomp.sdc.vendorsoftwareproduct.services.composition.CompositionEntityDataManager;
import org.openecomp.sdc.vendorsoftwareproduct.services.filedatastructuremodule.CandidateService;
import org.openecomp.sdc.vendorsoftwareproduct.services.utils.CandidateEntityBuilder;
import org.openecomp.sdc.vendorsoftwareproduct.types.OrchestrationTemplateActionResponse;
import org.openecomp.sdc.vendorsoftwareproduct.types.UploadFileResponse;
import org.openecomp.sdc.vendorsoftwareproduct.types.ValidationResponse;
import org.openecomp.sdc.vendorsoftwareproduct.types.candidateheat.FilesDataStructure;
import org.openecomp.sdc.vendorsoftwareproduct.utils.VendorSoftwareProductUtils;
import org.openecomp.sdc.versioning.dao.types.Version;
import org.openecomp.sdcrests.activitylog.types.ActivityType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrchestrationTemplateCandidateManagerImpl
    implements OrchestrationTemplateCandidateManager {
  private static MdcDataDebugMessage mdcDataDebugMessage = new MdcDataDebugMessage();
  private static final Logger logger =
      LoggerFactory.getLogger(OrchestrationTemplateCandidateManagerImpl.class);

  private VendorSoftwareProductDao vendorSoftwareProductDao;
  private VendorSoftwareProductInfoDao vspInfoDao;
  private OrchestrationTemplateDao orchestrationTemplateDataDao;
  private CandidateService candidateService;
  private HealingManager healingManager;
  private CompositionDataExtractor compositionDataExtractor;
  private ServiceModelDao<ToscaServiceModel, ServiceElement> serviceModelDao;
  private CompositionEntityDataManager compositionEntityDataManager;
  private ActivityLogManager activityLogManager;

  public OrchestrationTemplateCandidateManagerImpl(
      VendorSoftwareProductDao vendorSoftwareProductDao, VendorSoftwareProductInfoDao
      vspInfoDao,
      OrchestrationTemplateDao orchestrationTemplateDataDao,
      CandidateService candidateService, HealingManager healingManager,
      CompositionDataExtractor compositionDataExtractor,
      ServiceModelDao<ToscaServiceModel, ServiceElement> serviceModelDao,
      CompositionEntityDataManager compositionEntityDataManager,
      ActivityLogManager activityLogManager) {
    this.vendorSoftwareProductDao = vendorSoftwareProductDao;
    this.vspInfoDao = vspInfoDao;
    this.orchestrationTemplateDataDao = orchestrationTemplateDataDao;
    this.candidateService = candidateService;
    this.healingManager = healingManager;
    this.compositionDataExtractor = compositionDataExtractor;
    this.serviceModelDao = serviceModelDao;
    this.compositionEntityDataManager = compositionEntityDataManager;
    this.activityLogManager = activityLogManager;
  }

  @Override
  @Metrics
  public UploadFileResponse upload(String vspId, Version version, InputStream heatFileToUpload,
                                   String user) {
    mdcDataDebugMessage.debugEntryMessage("VSP id", vspId);

    VspDetails vspDetails = getVspDetails(vspId, version);

    UploadFileResponse uploadFileResponse = new UploadFileResponse();
    if (isNotEmptyFileToUpload(heatFileToUpload, uploadFileResponse)) {
      return uploadFileResponse;
    }

    byte[] uploadedFileData = FileUtils.toByteArray(heatFileToUpload);
    if (isInvalidRawZipData(uploadFileResponse, uploadedFileData)) {
      return uploadFileResponse;
    }

    Optional<FileContentHandler> optionalContentMap =
        getZipContentMap(uploadFileResponse, uploadedFileData);
    if (!optionalContentMap.isPresent()) {
      logger.error(Messages.ZIP_CONTENT_MAP.getErrorMessage());
      uploadFileResponse
          .addStructureError(
              SdcCommon.UPLOAD_FILE,
              new ErrorMessage(ErrorLevel.ERROR, Messages.ZIP_CONTENT_MAP.getErrorMessage()));

      mdcDataDebugMessage.debugExitMessage("VSP id", vspId);
      return uploadFileResponse;
    }

    if (!MapUtils.isEmpty(uploadFileResponse.getErrors())) {

      mdcDataDebugMessage.debugExitMessage("VSP id", vspId);
      return uploadFileResponse;
    }
    try {
      OrchestrationTemplateCandidateData candidateData =
          new CandidateEntityBuilder(candidateService)
              .buildCandidateEntityFromZip(vspDetails, uploadedFileData, optionalContentMap.get(),
                  uploadFileResponse.getErrors(), user);
      candidateService.updateCandidateUploadData(candidateData, vspDetails.getId());
    } catch (Exception exception) {
      logger.error(Messages.ZIP_CONTENT_MAP.getErrorMessage());
      uploadFileResponse
          .addStructureError(
              SdcCommon.UPLOAD_FILE,
              new ErrorMessage(ErrorLevel.ERROR, exception.getMessage()));

      mdcDataDebugMessage.debugExitMessage("VSP id", vspId);
      return uploadFileResponse;
    }

    mdcDataDebugMessage.debugExitMessage("VSP id", vspId);
    return uploadFileResponse;
  }

  @Override
  public OrchestrationTemplateActionResponse process(String vspId,
                                                     Version version, String user) {
    mdcDataDebugMessage.debugEntryMessage("VSP id", vspId);

    Optional<OrchestrationTemplateCandidateData> candidate =
        fetchCandidateDataEntity(vspId, version);
    if (!candidate.isPresent()) {
      throw new CoreException(new OrchestrationTemplateNotFoundErrorBuilder(vspId).build());
    }

    OrchestrationTemplateActionResponse response = new OrchestrationTemplateActionResponse();
    UploadFileResponse uploadFileResponse = new UploadFileResponse();
    OrchestrationTemplateCandidateData candidateDataEntity = candidate.get();
    Optional<FileContentHandler> fileContent =
        getZipContentMap(uploadFileResponse, candidateDataEntity.getContentData().array());
    if (!fileContent.isPresent()) {
      response.addStructureErrors(uploadFileResponse.getErrors());
      mdcDataDebugMessage.debugExitMessage("VSP id", vspId);
      return response;
    }

    Map<String, List<ErrorMessage>> uploadErrors = uploadFileResponse.getErrors();
    FileContentHandler fileContentMap = fileContent.get();
    FilesDataStructure structure =
        JsonUtil.json2Object(candidateDataEntity.getFilesDataStructure(), FilesDataStructure.class);

    if (CollectionUtils.isNotEmpty(structure.getUnassigned())) {
      response.addErrorMessageToMap(SdcCommon.UPLOAD_FILE,
          Messages.FOUND_UNASSIGNED_FILES.getErrorMessage(), ErrorLevel.ERROR);

      mdcDataDebugMessage.debugExitMessage("VSP id", vspId);
      return response;
    }

    VspDetails vspDetails =
        getVspDetails(vspId, version);

    String manifest = candidateService.createManifest(vspDetails, structure);
    fileContentMap.addFile(SdcCommon.MANIFEST_NAME, manifest.getBytes());

    HeatStructureTree tree = createAndValidateHeatTree(response, fileContentMap);

    Optional<ByteArrayInputStream> zipByteArrayInputStream = candidateService
        .fetchZipFileByteArrayInputStream(vspId, candidateDataEntity, manifest, uploadErrors);
    if (!zipByteArrayInputStream.isPresent()) {
      return response;
    }
    deleteUploadDataAndContent(vspId, version);
    saveHotData(vspId, version, zipByteArrayInputStream.get(), fileContentMap, tree);

    ToscaServiceModel toscaServiceModel =
        HeatToToscaUtil.loadAndTranslateTemplateData(fileContentMap)
            .getToscaServiceModel();
    if (toscaServiceModel != null) {
      serviceModelDao.storeServiceModel(vspId, version, toscaServiceModel);
      compositionEntityDataManager.saveCompositionData(vspId, version,
          compositionDataExtractor.extractServiceCompositionData(toscaServiceModel));
    }
    uploadFileResponse.addStructureErrors(uploadErrors);

    ActivityLogEntity activityLogEntity = new ActivityLogEntity(vspId,  String.valueOf(version.getMajor()+1),
            ActivityType.UPLOAD_HEAT.toString(), user, true, "", "");
    activityLogManager.addActionLog(activityLogEntity, user);

    mdcDataDebugMessage.debugExitMessage("VSP id", vspId);
    return response;
  }


  @Override
  public Optional<FilesDataStructure> getFilesDataStructure(
      String vspId, Version version, String user) {
    mdcDataDebugMessage.debugEntryMessage("VSP id", vspId);

    Optional<FilesDataStructure> candidateFileDataStructure =
        candidateService.getOrchestrationTemplateCandidateFileDataStructure(vspId, version);
    if (candidateFileDataStructure.isPresent()) {
      return candidateFileDataStructure;
    } else {
      Map<String, Object> healingParams = getHealingParamsAsMap(vspId, version, user);

      mdcDataDebugMessage
          .debugExitMessage("VSP id", vspId);
      return (Optional<FilesDataStructure>) healingManager
          .heal(HealCode.FILE_DATA_STRUCTURE_HEALER, healingParams);
    }
  }

  @Override

  public ValidationResponse updateFilesDataStructure(String vspId,
                                                     Version version, String user,
                                                     FilesDataStructure fileDataStructure) {
    mdcDataDebugMessage.debugEntryMessage("VSP id", vspId);

    ValidationResponse response = new ValidationResponse();
    Optional<List<ErrorMessage>> validateErrors =
        candidateService.validateFileDataStructure(fileDataStructure);
    if (validateErrors.isPresent()) {
      List<ErrorMessage> errorMessages = validateErrors.get();
      if (CollectionUtils.isNotEmpty(errorMessages)) {
        Map<String, List<ErrorMessage>> errorsMap = new HashMap<>();
        errorsMap.put(SdcCommon.UPLOAD_FILE, errorMessages);
        response.setUploadDataErrors(errorsMap, LoggerServiceName.Update_Manifest,
            LoggerTragetServiceName.VALIDATE_FILE_DATA_STRUCTURE);

        mdcDataDebugMessage
            .debugExitMessage("VSP id", vspId);
        return response;
      }
    }
    candidateService.updateOrchestrationTemplateCandidateFileDataStructure(vspId, version,
        fileDataStructure);

    mdcDataDebugMessage
        .debugExitMessage("VSP id", vspId);
    return response;
  }

  @Override

  public Optional<byte[]> get(String vspId, Version version, String user)
      throws IOException {
    mdcDataDebugMessage.debugEntryMessage("VSP id", vspId);

    VspDetails vspDetails =
        getVspDetails(vspId, version);

    Optional<OrchestrationTemplateCandidateData> candidateDataEntity =
        fetchCandidateDataEntity(vspId, version);

    if (!candidateDataEntity.isPresent()) {
      ErrorMessage errorMessage = new ErrorMessage(ErrorLevel.ERROR,
          Messages.NO_ZIP_FILE_WAS_UPLOADED_OR_ZIP_NOT_EXIST.getErrorMessage());
      logger.error(errorMessage.getMessage());

      mdcDataDebugMessage
          .debugExitMessage("VSP id", vspId);
      return Optional.empty();
    }

    FilesDataStructure structure = JsonUtil
        .json2Object(candidateDataEntity.get().getFilesDataStructure(), FilesDataStructure.class);
    String manifest = candidateService.createManifest(vspDetails, structure);

    mdcDataDebugMessage
        .debugExitMessage("VSP id", vspId);
    return Optional.ofNullable(candidateService
        .replaceManifestInZip(candidateDataEntity.get().getContentData(), manifest, vspId));
  }

  private Optional<OrchestrationTemplateCandidateData> fetchCandidateDataEntity(
      String vspId, Version version) {
    return Optional
        .ofNullable(candidateService.getOrchestrationTemplateCandidate(vspId, version));
  }

  private HeatStructureTree createAndValidateHeatTree(OrchestrationTemplateActionResponse response,
                                                      FileContentHandler fileContentMap) {
    VendorSoftwareProductUtils.addFileNamesToUploadFileResponse(fileContentMap, response);
    Map<String, List<ErrorMessage>> validationErrors =
        ValidationManagerUtil.initValidationManager(fileContentMap).validate();
    response.getErrors().putAll(validationErrors);

    HeatTreeManager heatTreeManager = HeatTreeManagerUtil.initHeatTreeManager(fileContentMap);
    heatTreeManager.createTree();
    heatTreeManager.addErrors(validationErrors);
    return heatTreeManager.getTree();
  }

  private void saveHotData(String vspId, Version activeVersion, InputStream uploadedFileData,
                           FileContentHandler fileContentMap, HeatStructureTree tree) {
    Map<String, Object> manifestAsMap =
        (Map<String, Object>) JsonUtil.json2Object(fileContentMap.getFileContent(
            SdcCommon.MANIFEST_NAME), Map.class);

    UploadData uploadData = new UploadData();
    uploadData.setPackageName((String) manifestAsMap.get("name"));
    uploadData.setPackageVersion((String) manifestAsMap.get("version"));
    uploadData.setContentData(ByteBuffer.wrap(FileUtils.toByteArray(uploadedFileData)));
    uploadData.setValidationDataStructure(new ValidationStructureList(tree));
    orchestrationTemplateDataDao.updateOrchestrationTemplateData(vspId, uploadData);
  }

  private void deleteUploadDataAndContent(String vspId, Version version) {
    //fixme change this when more tables are zusammenized
    vendorSoftwareProductDao.deleteUploadData(vspId, version);
  }

  private boolean isInvalidRawZipData(UploadFileResponse uploadFileResponse,
                                      byte[] uploadedFileData) {
    Optional<ErrorMessage> errorMessage;
    errorMessage = candidateService.validateRawZipData(uploadedFileData);
    if (errorMessage.isPresent()) {
      uploadFileResponse.addStructureError(SdcCommon.UPLOAD_FILE, errorMessage.get());
      return true;
    }
    return false;
  }

  private boolean isNotEmptyFileToUpload(InputStream heatFileToUpload,
                                         UploadFileResponse uploadFileResponse) {
    Optional<ErrorMessage> errorMessage =
        candidateService.validateNonEmptyFileToUpload(heatFileToUpload);
    if (errorMessage.isPresent()) {
      uploadFileResponse.addStructureError(SdcCommon.UPLOAD_FILE, errorMessage.get());
      return true;
    }
    return false;
  }

  private Optional<FileContentHandler> getZipContentMap(UploadFileResponse uploadFileResponse,
                                                        byte[] uploadedFileData) {
    FileContentHandler contentMap = null;
    try {
      contentMap = CommonUtil.loadUploadFileContent(uploadedFileData);
    } catch (IOException exception) {
      uploadFileResponse.addStructureError(
          SdcCommon.UPLOAD_FILE,
          new ErrorMessage(ErrorLevel.ERROR, Messages.INVALID_ZIP_FILE.getErrorMessage()));
    } catch (CoreException coreException) {
      uploadFileResponse.addStructureError(
          SdcCommon.UPLOAD_FILE, new ErrorMessage(ErrorLevel.ERROR, coreException.getMessage()));
    }
    return Optional.ofNullable(contentMap);
  }

  // todo *************************** move to reusable place! *************************

  private Map<String, Object> getHealingParamsAsMap(String vspId, Version version, String user) {
    Map<String, Object> healingParams = new HashMap<>();

    healingParams.put(SdcCommon.VSP_ID, vspId);
    healingParams.put(SdcCommon.VERSION, version);
    healingParams.put(SdcCommon.USER, user);

    return healingParams;
  }

  public VspDetails getVspDetails(String vspId, Version version) {
    VspDetails vspDetails = vspInfoDao.get(new VspDetails(vspId, version));
    vspDetails.setValidationData(orchestrationTemplateDataDao.getValidationData(vspId, version));
    return vspDetails;
  }

}