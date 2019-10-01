package com.nhattien.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity
@Table(name = "users_roles", catalog = "springframework")
public class UsersRoles implements java.io.Serializable {
  private static final long serialVersionUID = 1L;
  private Integer id;
  private Role role;
  private User users;
  public UsersRoles() {
  }
  public UsersRoles(Role role, User users) {
    this.role = role;
    this.users = users;
  }
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  public Integer getId() {
    return this.id;
  }
  public void setId(Integer id) {
    this.id = id;
  }
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "role")
  public Role getRole() {
    return this.role;
  }
  public void setRole(Role role) {
    this.role = role;
  }
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user")
  public User getUsers() {
    return this.users;
  }
  public void setUsers(User users) {
    this.users = users;
  }
}