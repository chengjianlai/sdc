///*-
// * ============LICENSE_START=======================================================
// * SDC
// * ================================================================================
// * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
// * ================================================================================
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * ============LICENSE_END=========================================================
// */
//
//package org.openecomp.sdcrests.vsp.rest;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.apache.cxf.jaxrs.ext.multipart.Attachment;
//import org.apache.cxf.jaxrs.ext.multipart.Multipart;
//import org.springframework.validation.annotation.Validated;
//
//import javax.validation.constraints.NotNull;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.HeaderParam;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//import static org.openecomp.sdcrests.common.RestConstants.USER_ID_HEADER_PARAM;
//import static org.openecomp.sdcrests.common.RestConstants.USER_MISSING_ERROR_MSG;
//
//@Path("/v1.0/vendor-software-products")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
//@Api(value = "Vendor Software Products")
//@Validated
//public interface ComponentMonitoringForSwaggerFileUpload {
//
//
//
//  @Path("/{vspId}/versions/{versionId}/components/{componentId}/uploads/type{type}")
//  @Consumes(MediaType.MULTIPART_FORM_DATA)
//  @ApiOperation(value = "Upload file for component by type")
//  Response upload(@Multipart("upload") Attachment attachment,
//                  @ApiParam(value = "Vendor software product Id") @PathParam("vspId")
//                      String vspId,
//                  @ApiParam(value = "Vendor software product version Id")
//                  @PathParam("versionId") String versionId,
//                  @ApiParam(value = "Component Id") @PathParam("componentId") String
//                      componentId,
//                  @ApiParam(value = "Upload Type") @PathParam("type") String type,
//                  @NotNull(message = USER_MISSING_ERROR_MSG)
//                  @HeaderParam(USER_ID_HEADER_PARAM) String user) throws Exception;
//
// }