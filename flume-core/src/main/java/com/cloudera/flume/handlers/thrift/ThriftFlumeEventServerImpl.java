/**
 * Licensed to Cloudera, Inc. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  Cloudera, Inc. licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cloudera.flume.handlers.thrift;

import java.io.IOException;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudera.flume.core.EventSink;
import com.cloudera.flume.handlers.hdfs.WriteableEvent;
import com.cloudera.flume.handlers.thrift.ThriftFlumeEventServer.Iface;
import com.google.common.base.Preconditions;

class ThriftFlumeEventServerImpl implements Iface {
  private static final Logger LOG = LoggerFactory
      .getLogger(ThriftFlumeEventServerImpl.class);
  EventSink sink;
  boolean truncates;

  ThriftFlumeEventServerImpl(EventSink sink, boolean truncates) {
    this.sink = sink;
    this.truncates = truncates;
  }

  @Override
  public void append(ThriftFlumeEvent evt) throws TException {
    Preconditions.checkState(sink != null);
    Preconditions.checkNotNull(evt);
    try {
      sink.append(ThriftEventConvertUtil.toFlumeEvent(evt, truncates));
    } catch (IOException e) {
      e.printStackTrace();
      throw new TException("Caught IO exception " + e);
    } catch (InterruptedException e) {

    }
  }

  @Override
  public void close() throws TException {
    try {
      sink.close();
    } catch (Exception e) {
      // TODO figure out how to deal with different exns
      throw new TException("Caught exception " + e, e);
    }
  }
}
