/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.automation.v2015_10_31.implementation;

import com.microsoft.azure.management.automation.v2015_10_31.Webhook;
import com.microsoft.azure.arm.model.implementation.CreatableUpdatableImpl;
import rx.Observable;
import com.microsoft.azure.management.automation.v2015_10_31.WebhookUpdateParameters;
import java.util.Map;
import com.microsoft.azure.management.automation.v2015_10_31.WebhookCreateOrUpdateParameters;
import org.joda.time.DateTime;
import com.microsoft.azure.management.automation.v2015_10_31.RunbookAssociationProperty;
import rx.functions.Func1;

class WebhookImpl extends CreatableUpdatableImpl<Webhook, WebhookInner, WebhookImpl> implements Webhook, Webhook.Definition, Webhook.Update {
    private final AutomationManager manager;
    private String resourceGroupName;
    private String automationAccountName;
    private String webhookName;
    private WebhookCreateOrUpdateParameters createParameter;
    private WebhookUpdateParameters updateParameter;

    WebhookImpl(String name, AutomationManager manager) {
        super(name, new WebhookInner());
        this.manager = manager;
        // Set resource name
        this.webhookName = name;
        //
        this.createParameter = new WebhookCreateOrUpdateParameters();
        this.updateParameter = new WebhookUpdateParameters();
    }

    WebhookImpl(WebhookInner inner, AutomationManager manager) {
        super(inner.name(), inner);
        this.manager = manager;
        // Set resource name
        this.webhookName = inner.name();
        // resource ancestor names
        this.resourceGroupName = IdParsingUtils.getValueFromIdByName(inner.id(), "resourceGroups");
        this.automationAccountName = IdParsingUtils.getValueFromIdByName(inner.id(), "automationAccounts");
        this.webhookName = IdParsingUtils.getValueFromIdByName(inner.id(), "webhooks");
        //
        this.createParameter = new WebhookCreateOrUpdateParameters();
        this.updateParameter = new WebhookUpdateParameters();
    }

    @Override
    public AutomationManager manager() {
        return this.manager;
    }

    @Override
    public Observable<Webhook> createResourceAsync() {
        WebhooksInner client = this.manager().inner().webhooks();
        return client.createOrUpdateAsync(this.resourceGroupName, this.automationAccountName, this.webhookName, this.createParameter)
            .map(new Func1<WebhookInner, WebhookInner>() {
               @Override
               public WebhookInner call(WebhookInner resource) {
                   resetCreateUpdateParameters();
                   return resource;
               }
            })
            .map(innerToFluentMap(this));
    }

    @Override
    public Observable<Webhook> updateResourceAsync() {
        WebhooksInner client = this.manager().inner().webhooks();
        return client.updateAsync(this.resourceGroupName, this.automationAccountName, this.webhookName, this.updateParameter)
            .map(new Func1<WebhookInner, WebhookInner>() {
               @Override
               public WebhookInner call(WebhookInner resource) {
                   resetCreateUpdateParameters();
                   return resource;
               }
            })
            .map(innerToFluentMap(this));
    }

    @Override
    protected Observable<WebhookInner> getInnerAsync() {
        WebhooksInner client = this.manager().inner().webhooks();
        return client.getAsync(this.resourceGroupName, this.automationAccountName, this.webhookName);
    }

    @Override
    public boolean isInCreateMode() {
        return this.inner().id() == null;
    }

    private void resetCreateUpdateParameters() {
        this.createParameter = new WebhookCreateOrUpdateParameters();
        this.updateParameter = new WebhookUpdateParameters();
    }

    @Override
    public DateTime creationTime() {
        return this.inner().creationTime();
    }

    @Override
    public String description() {
        return this.inner().description();
    }

    @Override
    public DateTime expiryTime() {
        return this.inner().expiryTime();
    }

    @Override
    public String id() {
        return this.inner().id();
    }

    @Override
    public Boolean isEnabled() {
        return this.inner().isEnabled();
    }

    @Override
    public DateTime lastInvokedTime() {
        return this.inner().lastInvokedTime();
    }

    @Override
    public String lastModifiedBy() {
        return this.inner().lastModifiedBy();
    }

    @Override
    public DateTime lastModifiedTime() {
        return this.inner().lastModifiedTime();
    }

    @Override
    public String name() {
        return this.inner().name();
    }

    @Override
    public Map<String, String> parameters() {
        return this.inner().parameters();
    }

    @Override
    public RunbookAssociationProperty runbook() {
        return this.inner().runbook();
    }

    @Override
    public String runOn() {
        return this.inner().runOn();
    }

    @Override
    public String type() {
        return this.inner().type();
    }

    @Override
    public String uri() {
        return this.inner().uri();
    }

    @Override
    public WebhookImpl withExistingAutomationAccount(String resourceGroupName, String automationAccountName) {
        this.resourceGroupName = resourceGroupName;
        this.automationAccountName = automationAccountName;
        return this;
    }

    @Override
    public WebhookImpl withExpiryTime(DateTime expiryTime) {
        this.createParameter.withExpiryTime(expiryTime);
        return this;
    }

    @Override
    public WebhookImpl withRunbook(RunbookAssociationProperty runbook) {
        this.createParameter.withRunbook(runbook);
        return this;
    }

    @Override
    public WebhookImpl withUri(String uri) {
        this.createParameter.withUri(uri);
        return this;
    }

    @Override
    public WebhookImpl withDescription(String description) {
        this.updateParameter.withDescription(description);
        return this;
    }

    @Override
    public WebhookImpl withName(String name) {
        if (isInCreateMode()) {
            this.createParameter.withName(name);
        } else {
            this.updateParameter.withName(name);
        }
        return this;
    }

    @Override
    public WebhookImpl withIsEnabled(Boolean isEnabled) {
        if (isInCreateMode()) {
            this.createParameter.withIsEnabled(isEnabled);
        } else {
            this.updateParameter.withIsEnabled(isEnabled);
        }
        return this;
    }

    @Override
    public WebhookImpl withParameters(Map<String, String> parameters) {
        if (isInCreateMode()) {
            this.createParameter.withParameters(parameters);
        } else {
            this.updateParameter.withParameters(parameters);
        }
        return this;
    }

    @Override
    public WebhookImpl withRunOn(String runOn) {
        if (isInCreateMode()) {
            this.createParameter.withRunOn(runOn);
        } else {
            this.updateParameter.withRunOn(runOn);
        }
        return this;
    }

}