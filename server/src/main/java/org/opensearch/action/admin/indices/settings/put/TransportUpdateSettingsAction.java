/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.action.admin.indices.settings.put;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.opensearch.action.ActionListener;
import org.opensearch.action.support.ActionFilters;
import org.opensearch.action.support.clustermanager.AcknowledgedResponse;
import org.opensearch.action.support.clustermanager.TransportClusterManagerNodeAction;
import org.opensearch.cluster.ClusterState;
import org.opensearch.cluster.ack.ClusterStateUpdateResponse;
import org.opensearch.cluster.block.ClusterBlockException;
import org.opensearch.cluster.block.ClusterBlockLevel;
import org.opensearch.cluster.metadata.IndexMetadata;
import org.opensearch.cluster.metadata.IndexNameExpressionResolver;
import org.opensearch.cluster.metadata.MetadataUpdateSettingsService;
import org.opensearch.cluster.service.ClusterService;
import org.opensearch.common.inject.Inject;
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.index.Index;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.transport.TransportService;

import java.io.IOException;

/**
 * Transport action for updating index settings
 *
 * @opensearch.internal
 */
public class TransportUpdateSettingsAction extends TransportClusterManagerNodeAction<UpdateSettingsRequest, AcknowledgedResponse> {

    private static final Logger logger = LogManager.getLogger(TransportUpdateSettingsAction.class);

    private final MetadataUpdateSettingsService updateSettingsService;

    @Inject
    public TransportUpdateSettingsAction(
        TransportService transportService,
        ClusterService clusterService,
        ThreadPool threadPool,
        MetadataUpdateSettingsService updateSettingsService,
        ActionFilters actionFilters,
        IndexNameExpressionResolver indexNameExpressionResolver
    ) {
        super(
            UpdateSettingsAction.NAME,
            transportService,
            clusterService,
            threadPool,
            actionFilters,
            UpdateSettingsRequest::new,
            indexNameExpressionResolver
        );
        this.updateSettingsService = updateSettingsService;
    }

    @Override
    protected String executor() {
        // we go async right away....
        return ThreadPool.Names.SAME;
    }

    @Override
    protected ClusterBlockException checkBlock(UpdateSettingsRequest request, ClusterState state) {
        // allow for dedicated changes to the metadata blocks, so we don't block those to allow to "re-enable" it
        ClusterBlockException globalBlock = state.blocks().globalBlockedException(ClusterBlockLevel.METADATA_WRITE);
        if (globalBlock != null) {
            return globalBlock;
        }
        if (request.settings().size() == 1 &&  // we have to allow resetting these settings otherwise users can't unblock an index
            IndexMetadata.INDEX_BLOCKS_METADATA_SETTING.exists(request.settings())
            || IndexMetadata.INDEX_READ_ONLY_SETTING.exists(request.settings())
            || IndexMetadata.INDEX_BLOCKS_READ_ONLY_ALLOW_DELETE_SETTING.exists(request.settings())) {
            return null;
        }
        return state.blocks()
            .indicesBlockedException(ClusterBlockLevel.METADATA_WRITE, indexNameExpressionResolver.concreteIndexNames(state, request));
    }

    @Override
    protected AcknowledgedResponse read(StreamInput in) throws IOException {
        return new AcknowledgedResponse(in);
    }

    @Override
    protected void masterOperation(
        final UpdateSettingsRequest request,
        final ClusterState state,
        final ActionListener<AcknowledgedResponse> listener
    ) {
        final Index[] concreteIndices = indexNameExpressionResolver.concreteIndices(state, request);
        UpdateSettingsClusterStateUpdateRequest clusterStateUpdateRequest = new UpdateSettingsClusterStateUpdateRequest().indices(
            concreteIndices
        )
            .settings(request.settings())
            .setPreserveExisting(request.isPreserveExisting())
            .ackTimeout(request.timeout())
            .masterNodeTimeout(request.masterNodeTimeout());

        updateSettingsService.updateSettings(clusterStateUpdateRequest, new ActionListener<ClusterStateUpdateResponse>() {
            @Override
            public void onResponse(ClusterStateUpdateResponse response) {
                listener.onResponse(new AcknowledgedResponse(response.isAcknowledged()));
            }

            @Override
            public void onFailure(Exception t) {
                logger.debug(() -> new ParameterizedMessage("failed to update settings on indices [{}]", (Object) concreteIndices), t);
                listener.onFailure(t);
            }
        });
    }
}
