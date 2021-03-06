/*
 * Copyright © 2016-2018 European Support Limited
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

package org.openecomp.sdcrests.vsp.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.openecomp.sdcrests.vendorsoftwareproducts.types.FileDataStructureDto;
import org.openecomp.sdcrests.vendorsoftwareproducts.types.UploadFileResponseDto;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.openecomp.sdcrests.common.RestConstants.USER_ID_HEADER_PARAM;
import static org.openecomp.sdcrests.common.RestConstants.USER_MISSING_ERROR_MSG;

@Path("/v1.0/vendor-software-products/{vspId}/versions/{versionId}/orchestration-template-candidate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Orchestration Template Candidate")
@Validated
public interface OrchestrationTemplateCandidate extends VspEntities {

  @POST
  @Path("/")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  Response upload(
      @PathParam("vspId") String vspId,
      @ApiParam(value = "Version Id") @PathParam("versionId") String versionId,
      @Multipart("upload") Attachment fileToUpload,
      @NotNull(message = USER_MISSING_ERROR_MSG) @HeaderParam(USER_ID_HEADER_PARAM) String user);

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @ApiOperation(value = "Get uploaded Network Package file",
      notes = "Downloads in uploaded Network Package file",
      response = File.class)
  Response get(
      @PathParam("vspId") String vspId,
      @ApiParam(value = "Version Id") @PathParam("versionId") String versionId,
      @NotNull(message = USER_MISSING_ERROR_MSG) @HeaderParam(USER_ID_HEADER_PARAM) String user)
      throws IOException;

  @DELETE
  @Path("/")
  @ApiOperation(value = "Delete orchestration template candidate file and its files data structure")
  Response abort(
      @PathParam("vspId") String vspId,
      @ApiParam(value = "Version Id") @PathParam("versionId") String versionId)
      throws Exception;

  @PUT
  @Path("/process")
  @ApiOperation(value = "process Orchestration Template Candidate",
      response = UploadFileResponseDto.class)
  Response process(
      @PathParam("vspId") String vspId,
      @ApiParam(value = "Version Id") @PathParam("versionId") String versionId,
      @NotNull(message = USER_MISSING_ERROR_MSG) @HeaderParam(USER_ID_HEADER_PARAM) String user)
      throws InvocationTargetException, IllegalAccessException;

  @PUT
  @Path("/manifest")
  @ApiOperation(value = "Update an existing vendor software product")
  Response updateFilesDataStructure(
      @PathParam("vspId") String vspId,
      @ApiParam(value = "Version Id") @PathParam("versionId") String versionId,
      @Valid FileDataStructureDto fileDataStructureDto,
      @NotNull(message = USER_MISSING_ERROR_MSG) @HeaderParam(USER_ID_HEADER_PARAM) String user)
      throws Exception;

  @GET
  @Path("/manifest")
  @ApiOperation(value = "Get uploaded HEAT file files data structure",
      notes = "Downloads the latest HEAT package",
      response = FileDataStructureDto.class)
  Response getFilesDataStructure(
      @PathParam("vspId") String vspId,
      @ApiParam(value = "Version Id") @PathParam("versionId") String versionId,
      @NotNull(message = USER_MISSING_ERROR_MSG) @HeaderParam(USER_ID_HEADER_PARAM) String user)
      throws Exception;
}
