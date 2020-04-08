/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.synapse.v2019_06_01_preview;

import rx.Completable;
import com.microsoft.azure.management.synapse.v2019_06_01_preview.implementation.IntegrationRuntimeCredentialsInner;
import com.microsoft.azure.arm.model.HasInner;

/**
 * Type representing IntegrationRuntimeCredentials.
 */
public interface IntegrationRuntimeCredentials extends HasInner<IntegrationRuntimeCredentialsInner> {
    /**
     * Sync integration runtime credentials.
     * Force the integration runtime to synchronize credentials across integration runtime nodes, and this will override the credentials across all worker nodes with those available on the dispatcher node. If you already have the latest credential backup file, you should manually import it (preferred) on any self-hosted integration runtime node than using this API directly.
     *
     * @param resourceGroupName The name of the resource group. The name is case insensitive.
     * @param workspaceName The name of the workspace
     * @param integrationRuntimeName Integration runtime name
     * @throws IllegalArgumentException thrown if parameters fail the validation
     * @return the observable for the request
     */
    Completable syncAsync(String resourceGroupName, String workspaceName, String integrationRuntimeName);

}