/*
 *  Copyright (c) 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */
package org.eclipse.dataspaceconnector.iam.did.credentials;

import org.eclipse.dataspaceconnector.iam.did.spi.credentials.CredentialsResult;
import org.eclipse.dataspaceconnector.iam.did.spi.credentials.CredentialsVerifier;
import org.eclipse.dataspaceconnector.iam.did.spi.hub.IdentityHubClient;
import org.eclipse.dataspaceconnector.iam.did.spi.hub.keys.PublicKeyWrapper;
import org.eclipse.dataspaceconnector.iam.did.spi.hub.message.ObjectQuery;
import org.eclipse.dataspaceconnector.iam.did.spi.hub.message.ObjectQueryRequest;

import java.util.HashMap;

/**
 * Implements a sample credentials validator that checks for signed registration credentials.
 */
public class IdentityHubCredentialsVerifier implements CredentialsVerifier {
    private final IdentityHubClient hubClient;

    public IdentityHubCredentialsVerifier(IdentityHubClient hubClient) {
        this.hubClient = hubClient;
    }

    @Override
    public CredentialsResult verifyCredentials(String hubBaseUrl, PublicKeyWrapper publicKey) {
        var query = ObjectQuery.Builder.newInstance().context("ION Demo").type("RegistrationCredentials").build();
        var queryRequest = ObjectQueryRequest.Builder.newInstance().query(query).iss("did:ion:EiDfkaPHt8Yojnh15O7egrj5pA9tTefh_SYtbhF1-XyAeA").aud("aud").sub("sub").build();
        var credentials = hubClient.queryCredentials(queryRequest, hubBaseUrl, publicKey);
        if (credentials.isError()) {
            return new CredentialsResult("Error resolving credentials");
        }

        // only support String credentials; filter out others
        var map = new HashMap<String, String>();
        credentials.getResponse().entrySet().stream().filter(entry -> entry.getValue() instanceof String).forEach(entry -> map.put(entry.getKey(), (String) entry.getValue()));
        return new CredentialsResult(map);
    }
}