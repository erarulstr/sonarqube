/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.server.user.index;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.server.es.EsTester;
import org.sonar.server.exceptions.NotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class UserIndexTest {

  @Rule
  public EsTester esTester = new EsTester().addDefinitions(new UserIndexDefinition(new Settings()));

  private UserIndex index;

  @Before
  public void setUp() {
    index = new UserIndex(esTester.client());
  }

  @Test
  public void get_nullable_by_login() throws Exception {
    esTester.putDocuments(UserIndexDefinition.INDEX, UserIndexDefinition.TYPE_USER, this.getClass(), "user1.json", "user2.json");

    UserDoc userDoc = index.getNullableByLogin("user1");
    assertThat(userDoc).isNotNull();
    assertThat(userDoc.login()).isEqualTo("user1");
    assertThat(userDoc.name()).isEqualTo("User1");
    assertThat(userDoc.email()).isEqualTo("user1@mail.com");
    assertThat(userDoc.active()).isTrue();
    assertThat(userDoc.scmAccounts()).containsOnly("user_1", "u1");
    assertThat(userDoc.createdAt()).isEqualTo(1500000000000L);
    assertThat(userDoc.updatedAt()).isEqualTo(1500000000000L);

    assertThat(index.getNullableByLogin("unknown")).isNull();
  }

  @Test
  public void get_nullable_by_login_should_be_case_sensitive() throws Exception {
    esTester.putDocuments(UserIndexDefinition.INDEX, UserIndexDefinition.TYPE_USER, this.getClass(), "user1.json");

    assertThat(index.getNullableByLogin("user1")).isNotNull();
    assertThat(index.getNullableByLogin("User1")).isNull();
  }

  @Test
  public void get_by_login() throws Exception {
    esTester.putDocuments(UserIndexDefinition.INDEX, UserIndexDefinition.TYPE_USER, this.getClass(), "user1.json", "user2.json");

    UserDoc userDoc = index.getByLogin("user1");
    assertThat(userDoc).isNotNull();
    assertThat(userDoc.login()).isEqualTo("user1");
    assertThat(userDoc.name()).isEqualTo("User1");
    assertThat(userDoc.email()).isEqualTo("user1@mail.com");
    assertThat(userDoc.active()).isTrue();
    assertThat(userDoc.scmAccounts()).containsOnly("user_1", "u1");
    assertThat(userDoc.createdAt()).isEqualTo(1500000000000L);
    assertThat(userDoc.updatedAt()).isEqualTo(1500000000000L);
  }

  @Test
  public void fail_to_get_by_login_on_unknown_user() throws Exception {
    try {
      index.getByLogin("unknown");
      fail();
    } catch (Exception e) {
      assertThat(e).isInstanceOf(NotFoundException.class).hasMessage("User 'unknown' not found");
    }
  }

  @Test
  public void get_nullable_by_scm_account() throws Exception {
    esTester.putDocuments(UserIndexDefinition.INDEX, UserIndexDefinition.TYPE_USER, this.getClass(), "user1.json", "user2.json");

    assertThat(index.getNullableByScmAccount("user_1").login()).isEqualTo("user1");
    assertThat(index.getNullableByScmAccount("user1@mail.com").login()).isEqualTo("user1");
    assertThat(index.getNullableByScmAccount("user1").login()).isEqualTo("user1");

    assertThat(index.getNullableByScmAccount("unknown")).isNull();
  }

  @Test
  public void get_nullable_by_scm_account_return_null_when_two_users_have_same_email() throws Exception {
    esTester.putDocuments(UserIndexDefinition.INDEX, UserIndexDefinition.TYPE_USER, this.getClass(), "user1.json", "user3-with-same-email-as-user1.json");

    assertThat(index.getNullableByScmAccount("user1@mail.com")).isNull();
  }

}
