/*
 * This file is part of ToroDB.
 *
 * ToroDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ToroDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with repl. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2016 8Kdata.
 * 
 */

package com.torodb.mongodb.repl.topology;

import com.eightkdata.mongowp.client.core.MongoClient;
import com.eightkdata.mongowp.client.core.MongoClientFactory;
import com.eightkdata.mongowp.client.core.MongoConnection;
import com.eightkdata.mongowp.client.core.MongoConnection.RemoteCommandResponse;
import com.eightkdata.mongowp.client.core.UnreachableMongoServerException;
import com.eightkdata.mongowp.mongoserver.api.safe.library.v3m0.commands.internal.ReplSetHeartbeatCommand;
import com.eightkdata.mongowp.mongoserver.api.safe.library.v3m0.commands.internal.ReplSetHeartbeatCommand.ReplSetHeartbeatArgument;
import com.eightkdata.mongowp.mongoserver.api.safe.library.v3m0.commands.internal.ReplSetHeartbeatCommand.ReplSetHeartbeatReply;
import com.torodb.core.concurrent.ConcurrentToolsFactory;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import javax.inject.Inject;
import org.jooq.lambda.UncheckedException;

/**
 *
 */
public class MongoClientHeartbeatSender implements HeartbeatSender {

    private final MongoClientFactory mongoClientFactory;
    private final ExecutorService executorService;

    @Inject
    public MongoClientHeartbeatSender(MongoClientFactory mongoClientFactory, 
            ConcurrentToolsFactory concurrentToolsFactory) {
        this.mongoClientFactory = mongoClientFactory;
        executorService = concurrentToolsFactory.createExecutorServiceWithMaxThreads("topology-heartbeat", 1);
    }

    @Override
    public CompletableFuture<RemoteCommandResponse<ReplSetHeartbeatReply>> sendHeartbeat(
            RemoteCommandRequest<ReplSetHeartbeatArgument> req) {
        return CompletableFuture.completedFuture(req)
                .thenApplyAsync(this::sendHeartbeatTask, executorService);
    }

    private RemoteCommandResponse<ReplSetHeartbeatReply> sendHeartbeatTask(
            RemoteCommandRequest<ReplSetHeartbeatArgument> req) {
        MongoClient client;
        try {
            client = mongoClientFactory.createClient(req.getTarget());
        } catch (UnreachableMongoServerException ex) {
            throw new UncheckedException(ex);
        }
        try (MongoConnection connection = client.openConnection()) {
            return connection.execute(
                    ReplSetHeartbeatCommand.INSTANCE,
                    req.getDbname(),
                    true,
                    req.getCmdObj()
            );
        }
    }
}
