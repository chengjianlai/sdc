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

'use strict';
import { PropertyBEModel, PropertyFEModel } from "../../models";

export class InstanceBePropertiesMap {
    [instanceId: string]: Array<PropertyBEModel>;
}

export class InstanceFePropertiesMap {
    [instanceId: string]: Array<PropertyFEModel>;
}

export class InstancePropertiesAPIMap {
    componentInstanceProperties: InstanceBePropertiesMap;
    componentInstanceInputsMap: InstanceBePropertiesMap;
    groupProperties: InstanceBePropertiesMap;
    policyProperties: InstanceBePropertiesMap;

    constructor(inputsMapData: InstanceBePropertiesMap, propertiesMapData: InstanceBePropertiesMap, groupPropertiesMapData: InstanceBePropertiesMap, policyPropertiesMapData: InstanceBePropertiesMap) {
        this.componentInstanceInputsMap = inputsMapData ? inputsMapData: new InstanceBePropertiesMap();
        this.componentInstanceProperties = propertiesMapData ? propertiesMapData: new InstanceBePropertiesMap();
        this.groupProperties = groupPropertiesMapData ? groupPropertiesMapData : new InstanceBePropertiesMap();
        this.policyProperties = policyPropertiesMapData ? policyPropertiesMapData : new InstanceBePropertiesMap();
    }

}
