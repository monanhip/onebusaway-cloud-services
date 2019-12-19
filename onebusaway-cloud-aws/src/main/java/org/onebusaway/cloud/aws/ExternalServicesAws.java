/**
 * Copyright (C) 2018 Cambridge Systematics, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.cloud.aws;

import org.onebusaway.cloud.api.ExternalResult;
import org.onebusaway.cloud.api.ExternalServices;
import org.onebusaway.cloud.api.InputStreamConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class ExternalServicesAws implements ExternalServices {

    private Logger _log = LoggerFactory.getLogger(ExternalServicesAws.class);

    private SNSServices _sns = new SNSServices();
    private CloudWatchServices _cloudwatch = new CloudWatchServices();
    private S3Services _s3 = new S3Services();
    private AwsLeadershipElectionService _election = new AwsLeadershipElectionService();

    @Override
    public ExternalResult publishMessage(String topic, String messageConents) {
        boolean result = _sns.publish(topic, messageConents);
        return new AwsExternalResult(result);
    }

    @Override
    public ExternalResult publishMetric(String namespace, String metricName, String dimensionName, String dimensionValue, double value) {
        return _cloudwatch.publishMetric(namespace, metricName, dimensionName, dimensionValue, value);
    }

    @Override
    public ExternalResult publishMultiDimensionalMetric(String namespace, String metricName, String[] dimensionName, String[] dimensionValue, double value) {
        return _cloudwatch.publishMultiDimensionalMetric(namespace, metricName, dimensionName, dimensionValue, value);
    }
        @Override
    public ExternalResult getFileAsStream(String url, InputStreamConsumer consumer) {
        return getFileAsStream(url, consumer, null);
    }

    @Override
    public ExternalResult getFileAsStream(String url, InputStreamConsumer consumer, String profile) {
        return getFileAsStream(url, consumer, profile, CredentialContainer.DEFAULT_REGION);
    }
    @Override
    public ExternalResult getFileAsStream(String url, InputStreamConsumer consumer, String profile, String region) {
        CredentialContainer credentials;
        if (profile == null || "default".equals(profile)) {
            credentials = CredentialContainer.getDefault();
        } else {
            credentials = new CredentialContainer(profile, region);
        }
        try (InputStream inputStream = _s3.fetch(url, credentials)) {
            consumer.accept(inputStream);
            return new AwsExternalResult(true);
        } catch (IOException ex) {
            _log.error("Error reading from S3: {}", ex);
            ex.printStackTrace();
            return new AwsExternalResult(false, ex.toString(), null);
        }
    }

    @Override
    public boolean isInstancePrimary() {
        return _election.isInstancePrimary();
    }
}
