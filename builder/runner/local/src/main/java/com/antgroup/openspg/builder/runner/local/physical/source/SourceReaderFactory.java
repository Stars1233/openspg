/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.builder.runner.local.physical.source;

import com.antgroup.openspg.builder.core.logical.BaseLogicalNode;
import com.antgroup.openspg.builder.core.logical.CsvSourceNode;
import com.antgroup.openspg.builder.core.logical.StringSourceNode;
import com.antgroup.openspg.builder.runner.local.physical.source.impl.CsvFileSourceReader;
import com.antgroup.openspg.builder.runner.local.physical.source.impl.StringSourceReader;

public class SourceReaderFactory {

  public static BaseSourceReader<?> getSourceReader(BaseLogicalNode<?> baseNode) {
    switch (baseNode.getType()) {
      case CSV_SOURCE:
        return new CsvFileSourceReader(
            baseNode.getId(), baseNode.getName(), ((CsvSourceNode) baseNode).getNodeConfig());
      case STRING_SOURCE:
        return new StringSourceReader(
            baseNode.getId(), baseNode.getName(), ((StringSourceNode) baseNode).getNodeConfig());
      default:
        throw new IllegalArgumentException("illegal nodeType=" + baseNode.getType());
    }
  }
}
