/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2011 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.persistence.dao;

import org.apache.ibatis.session.SqlSession;
import org.sonar.api.BatchComponent;
import org.sonar.api.ServerComponent;
import org.sonar.persistence.MyBatis;
import org.sonar.persistence.model.LoadedTemplate;
import org.sonar.persistence.model.LoadedTemplateMapper;

public class LoadedTemplateDao implements BatchComponent, ServerComponent {

  private MyBatis mybatis;

  public LoadedTemplateDao(MyBatis mybatis) {
    this.mybatis = mybatis;
  }

  public LoadedTemplate selectByKeyAndType(String key, String type) {
    SqlSession session = mybatis.openSession();
    LoadedTemplateMapper mapper = session.getMapper(LoadedTemplateMapper.class);
    try {
      return mapper.selectByKeyAndType(key, type);
    } finally {
      session.close();
    }
  }

  public void insert(LoadedTemplate loadedTemplate) {
    SqlSession session = mybatis.openSession();
    LoadedTemplateMapper mapper = session.getMapper(LoadedTemplateMapper.class);
    try {
      mapper.insert(loadedTemplate);
      session.commit();
    } finally {
      session.close();
    }
  }

}
