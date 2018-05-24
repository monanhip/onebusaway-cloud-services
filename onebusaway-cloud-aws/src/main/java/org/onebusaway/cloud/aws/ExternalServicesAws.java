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

public class ExternalServicesAws implements ExternalServices {
    private SNSServices _sns = new SNSServices();

    @Override
    public ExternalResult pubishMessage(String topic, String messageConents) {
        boolean result = _sns.publish(topic, messageConents);
        return new AwsExternalResult(result);
    }

    @Override
    public ExternalResult publishMetric(String metricName, String dimension, Number value) {
        return null;
    }
}
