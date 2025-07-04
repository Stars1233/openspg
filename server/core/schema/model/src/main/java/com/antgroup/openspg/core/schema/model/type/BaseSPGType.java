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

package com.antgroup.openspg.core.schema.model.type;

import com.antgroup.openspg.core.schema.model.BaseOntology;
import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.SchemaConstants;
import com.antgroup.openspg.core.schema.model.identifier.SPGTripleIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.semantic.SystemPredicateEnum;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Abstract parent class of subject and object types.<br>
 * <br>
 *
 * <p>In the knowledge representation of SPO triples, subject and object are used to represent
 * entities or literal constants in the real world, similar to node in the LPG framework. In SPG
 * knowledge modeling, subject types are divided into the following three categories:<br>
 *
 * <ul>
 *   <li>{@link EntityType}：such as User,Company etc.
 *   <li>{@link ConceptType}：such as brand,Administration etc.
 *   <li>{@link EventType}：such as Epidemic event,Terrorist incident etc.
 *   <li>{@link IndexType}: such as summary index, chunk2query index etc
 * </ul>
 *
 * <br>
 * The object type not only includes the above three subject types, but also includes the following
 * two additional types: <br>
 *
 * <ul>
 *   <li>{@link BasicType}：such as "abc",123 literal constant etc.
 *   <li>{@link StandardType}：such as PhoneNumber,Email etc.
 * </ul>
 *
 * <br>
 * Because these five types share similar model structures and scopes, SchemaType is defined as an
 * abstract parent class, and specific types can be identified through SpgTypeEnum. Additionally,
 * both subjects and objects include basic information such as ID, Chinese and English name,
 * description, etc.
 */
public abstract class BaseSPGType extends BaseOntology
    implements WithBasicInfo<SPGTypeIdentifier>,
        WithSPGTypeEnum,
        WithSpgTypeAdvancedConfig,
        WithParentTypeInfo {

  private static final long serialVersionUID = 168750646312268713L;

  /** The basic information of schema type, such as name, Chinese name, description, creator etc. */
  private final BasicInfo<SPGTypeIdentifier> basicInfo;

  /**
   * The information of the inherited schema type. basic types and standard types have no parent.
   * However, entity types, concept types or event types is inherited from the Thing type by
   * default.
   */
  private ParentTypeInfo parentTypeInfo;

  /** The category of schema types. */
  private final SPGTypeEnum spgTypeEnum;

  /**
   * The list of properties defined on a schema type, include properties inherited from the parent
   * type.
   */
  private List<Property> properties;

  /**
   * The list of relations defined on a schema type, include relations generated by semantic
   * properties, as well as the relations that deep inherited from parent type.
   */
  private List<Relation> relations;

  /** The advanced configuration of SPG type, such as visible scope, bound operators etc. */
  private SPGTypeAdvancedConfig advancedConfig;

  public BaseSPGType(
      BasicInfo<SPGTypeIdentifier> basicInfo,
      ParentTypeInfo parentTypeInfo,
      SPGTypeEnum spgTypeEnum,
      List<Property> properties,
      List<Relation> relations,
      SPGTypeAdvancedConfig advancedConfig) {
    this.basicInfo = basicInfo;
    this.spgTypeEnum = spgTypeEnum;
    if (!SPGTypeEnum.BASIC_TYPE.equals(spgTypeEnum)
        && !SPGTypeEnum.STANDARD_TYPE.equals(spgTypeEnum)
        && basicInfo != null
        && basicInfo.getName() != null
        && !SchemaConstants.ROOT_TYPE_UNIQUE_NAME.equals(getName())
        && parentTypeInfo == null) {
      this.parentTypeInfo = ParentTypeInfo.THING;
    } else {
      this.parentTypeInfo = parentTypeInfo;
    }
    this.properties = (properties == null ? new ArrayList<>() : properties);
    this.relations = (relations == null ? new ArrayList<>() : relations);
    this.advancedConfig = (advancedConfig == null ? new SPGTypeAdvancedConfig() : advancedConfig);
  }

  @Override
  public BasicInfo<SPGTypeIdentifier> getBasicInfo() {
    return basicInfo;
  }

  @Override
  public SPGTypeEnum getSpgTypeEnum() {
    return spgTypeEnum;
  }

  @Override
  public ParentTypeInfo getParentTypeInfo() {
    return parentTypeInfo;
  }

  public void setParentTypeInfo(ParentTypeInfo parentTypeInfo) {
    this.parentTypeInfo = parentTypeInfo;
  }

  public List<Property> getProperties() {
    return properties;
  }

  public void setProperties(List<Property> properties) {
    this.properties = properties;
  }

  public void setRelations(List<Relation> relations) {
    this.relations = relations;
  }

  public Property getPropertyByName(SPGTripleIdentifier spgTripleIdentifier) {
    if (CollectionUtils.isEmpty(properties)) {
      return null;
    }

    for (Property property : properties) {
      if (Objects.equals(toRef().getBaseSpgIdentifier(), spgTripleIdentifier.getSubject())
          && Objects.equals(property.getBaseSpgIdentifier(), spgTripleIdentifier.getPredicate())
          && Objects.equals(
              property.getObjectTypeRef().getBaseSpgIdentifier(),
              spgTripleIdentifier.getObject())) {
        return property;
      }
    }
    return null;
  }

  public Relation getRelationByName(SPGTripleIdentifier spgTripleIdentifier) {
    if (CollectionUtils.isEmpty(relations)) {
      return null;
    }

    for (Relation relation : relations) {
      if (Objects.equals(toRef().getBaseSpgIdentifier(), spgTripleIdentifier.getSubject())
          && Objects.equals(relation.getBaseSpgIdentifier(), spgTripleIdentifier.getPredicate())
          && Objects.equals(
              relation.getObjectTypeRef().getBaseSpgIdentifier(),
              spgTripleIdentifier.getObject())) {
        return relation;
      }
    }
    return null;
  }

  public Map<String, Property> getPropertyMap() {
    if (CollectionUtils.isEmpty(properties)) {
      return Collections.emptyMap();
    }
    return properties.stream()
        .collect(Collectors.toMap(WithBasicInfo::getName, Function.identity(), (x1, x2) -> x1));
  }

  public List<Relation> getRelations() {
    return relations;
  }

  @Override
  public SPGTypeAdvancedConfig getAdvancedConfig() {
    return advancedConfig;
  }

  public void setAdvancedConfig(SPGTypeAdvancedConfig advancedConfig) {
    this.advancedConfig = advancedConfig;
  }

  public Property getPredicateProperty(SystemPredicateEnum predicate) {
    for (Property property : properties) {
      if (predicate.getName().equals(property.getName())) {
        return property;
      }
    }
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BaseSPGType)) {
      return false;
    }
    BaseSPGType that = (BaseSPGType) o;
    return Objects.equals(getBasicInfo(), that.getBasicInfo());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getBasicInfo());
  }

  public SPGTypeRef toRef() {
    SPGTypeRef spgTypeRef = new SPGTypeRef(basicInfo, spgTypeEnum);
    spgTypeRef.setProjectId(getProjectId());
    spgTypeRef.setOntologyId(getOntologyId());
    return spgTypeRef;
  }
}
