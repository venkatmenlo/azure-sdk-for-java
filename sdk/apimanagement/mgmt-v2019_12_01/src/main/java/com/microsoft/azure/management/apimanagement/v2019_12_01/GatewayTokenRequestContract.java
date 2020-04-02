/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.apimanagement.v2019_12_01;

import org.joda.time.DateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Gateway token request contract properties.
 */
public class GatewayTokenRequestContract {
    /**
     * The Key to be used to generate gateway token. Possible values include:
     * 'primary', 'secondary'.
     */
    @JsonProperty(value = "keyType", required = true)
    private KeyType keyType;

    /**
     * The Expiry time of the Token. Maximum token expiry time is set to 30
     * days. The date conforms to the following format: `yyyy-MM-ddTHH:mm:ssZ`
     * as specified by the ISO 8601 standard.
     */
    @JsonProperty(value = "expiry", required = true)
    private DateTime expiry;

    /**
     * Get the Key to be used to generate gateway token. Possible values include: 'primary', 'secondary'.
     *
     * @return the keyType value
     */
    public KeyType keyType() {
        return this.keyType;
    }

    /**
     * Set the Key to be used to generate gateway token. Possible values include: 'primary', 'secondary'.
     *
     * @param keyType the keyType value to set
     * @return the GatewayTokenRequestContract object itself.
     */
    public GatewayTokenRequestContract withKeyType(KeyType keyType) {
        this.keyType = keyType;
        return this;
    }

    /**
     * Get the Expiry time of the Token. Maximum token expiry time is set to 30 days. The date conforms to the following format: `yyyy-MM-ddTHH:mm:ssZ` as specified by the ISO 8601 standard.
     *
     * @return the expiry value
     */
    public DateTime expiry() {
        return this.expiry;
    }

    /**
     * Set the Expiry time of the Token. Maximum token expiry time is set to 30 days. The date conforms to the following format: `yyyy-MM-ddTHH:mm:ssZ` as specified by the ISO 8601 standard.
     *
     * @param expiry the expiry value to set
     * @return the GatewayTokenRequestContract object itself.
     */
    public GatewayTokenRequestContract withExpiry(DateTime expiry) {
        this.expiry = expiry;
        return this;
    }

}