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

package org.openecomp.sdcrests.applicationconfig.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.openecomp.sdcrests.applicationconfiguration.types.ConfigurationDataDto;
import org.springframework.validation.annotation.Validated;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Path("/v1.0/application-configuration")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "Application Configuration")
@Validated
public interface ApplicationConfiguration {

  @POST
  @Path("/")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @ApiOperation(value = "Insert JSON schema into application config table")
  Response insertToTable(@QueryParam("namespace") String namespace, @QueryParam("key") String key,
                         @Multipart("value") InputStream fileContainingSchema);


  @GET
  @Path("/{namespace}/{key}")
  @ApiOperation(value = "Get JSON schema by namespace and key",
      response = ConfigurationDataDto.class)
  Response getFromTable(@PathParam("namespace") String namespace, @PathParam("key") String key);


  @GET
  @Path("/{namespace}")
  @ApiOperation(value = "Get List of keys and values by namespace",
      responseContainer = "List")
  Response getListOfConfigurationByNamespaceFromTable(@PathParam("namespace") String namespace);

}
